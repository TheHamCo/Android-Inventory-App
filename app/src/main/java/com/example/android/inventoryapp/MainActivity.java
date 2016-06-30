package com.example.android.inventoryapp;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.example.android.inventoryapp.db.ProductContract.ProductEntry;

import java.util.Currency;
import java.util.Locale;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    ListView productList;

    // /product
    final static Uri productUri = ProductEntry.CONTENT_URI;

    private ProductAdapter productAdapter;

    public static final int LIST_LOADER = 0;

    //locale for currency
    Locale localeSetting;

    @Override
    public View onCreateView(View parent, String name, Context context, AttributeSet attrs) {
        getSupportLoaderManager().initLoader(0, null, this);
        return super.onCreateView(parent, name, context, attrs);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        localeSetting = getResources().getConfiguration().locale;

        Button seedButton = (Button)findViewById(R.id.reseed_button);
        if (seedButton != null) {
            seedButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    seedData();
                }
            });
        }

        // Query all data
//        Cursor products = getContentResolver().query(productUri, null, null, null, null);

        // ListView
        productList = (ListView) findViewById(R.id.product_list);
//        SimpleCursorAdapter adapter = new SimpleCursorAdapter(
//                this
//                ,R.layout.product_card
//                ,products
//                ,new String[] { ProductEntry._ID, ProductEntry.COLUMN_PRODUCT, ProductEntry.COLUMN_PRICE, ProductEntry.COLUMN_QTY }
//                ,new int[] { R.id._id, R.id.product_name, R.id.price, R.id.qty }
//        );
        productAdapter = new ProductAdapter(this, null);
        productList.setAdapter(productAdapter);

        // Track sale
        productList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Get the current qty
                Cursor prodCursor = (Cursor)parent.getItemAtPosition(position);
                int currQty = prodCursor.getInt(prodCursor.getColumnIndex(ProductEntry.COLUMN_QTY));
                //URI of this entry
                Uri productIdUri = ProductEntry.buildLocationuri(id);

                Log.d("product id uri", productIdUri.toString());

                //Update the qty
                ContentValues values = new ContentValues();
                values.put(ProductEntry.COLUMN_QTY, ++currQty);

                getContentResolver().update(productIdUri,values,null,null);
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_product, menu);
        return super.onCreateOptionsMenu(menu);
    }

    // Source: http://stackoverflow.com/a/18176909/5302182
    //TODO: add hints
    //TODO: try http://stackoverflow.com/questions/2335813/how-to-inflate-one-view-with-a-layout
    //TODO: email validate Patterns.EMAIL_ADDRESS.matcher(<your Email EditText input String here>).matches()
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.add_product:
//                final View addView = getLayoutInflater().inflate(R.layout.add_product_view, null);
                final EditText productNameEditText = new EditText(this);
                final EditText priceEditText = new EditText(this);
                final EditText qtyEditText = new EditText(this);
                final EditText supplierNameEditText = new EditText(this);
                final EditText supplierEmailEditText = new EditText(this);

                productNameEditText.setHint("Product Name");
                priceEditText.setHint("Price");
                qtyEditText.setHint("Quantity");
                supplierNameEditText.setHint("Supplier Name");
                supplierEmailEditText.setHint("Supplier Email");

                priceEditText.setRawInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                qtyEditText.setRawInputType(InputType.TYPE_CLASS_NUMBER);
                supplierEmailEditText.setRawInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);

                LinearLayout layout = new LinearLayout(getApplicationContext());
                layout.setOrientation(LinearLayout.VERTICAL);
                layout.addView(productNameEditText);
                layout.addView(priceEditText);
                layout.addView(qtyEditText);
                layout.addView(supplierNameEditText);
                layout.addView(supplierEmailEditText);

                final AlertDialog dialog = new AlertDialog.Builder(this)
                        .setTitle("Add a new product")
                        .setView(layout)
                        .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Do nothing here because we overwrite later,
                                // but this needs to be here to handle old versions of Android
                                // SOURCE: http://stackoverflow.com/a/15619098/5302182
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .create();
                dialog.show();

                // Override AlertDialog default closing behavior to validate data
                // SOURCE: http://stackoverflow.com/a/15619098/5302182
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String productName = productNameEditText.getText().toString();
                        String price = priceEditText.getText().toString();
                        String qty = qtyEditText.getText().toString();
                        String supplierName = supplierNameEditText.getText().toString();
                        String supplierEmail = supplierEmailEditText.getText().toString();

//                        Number priceDouble = 0;
//                        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance();
//                        try {
//                            priceDouble = currencyFormat.parse(price);
//                        } catch (ParseException e) {
//                            e.printStackTrace();
//                        }
                        Currency localCurrency = Currency.getInstance(localeSetting);
                        Log.d("currency symbol", localCurrency.getSymbol(localeSetting));
                        price = price.replaceAll("[" + localCurrency.getSymbol(localeSetting) + "]", "");
                        price = price.replaceAll("[,]", "");

                        Log.d("Price Number", price);

                        ProductValidation productValidation = validateAddProduct(productName, price, qty, supplierName, supplierEmail);

                        if (productValidation.isValid()) {
                            ContentValues values = new ContentValues();
                            values.put(ProductEntry.COLUMN_PRODUCT, productName);
                            values.put(ProductEntry.COLUMN_PRICE, price);
                            values.put(ProductEntry.COLUMN_QTY, qty);
                            values.put(ProductEntry.COLUMN_SUPPLIER_NAME, supplierName);
                            values.put(ProductEntry.COLUMN_SUPPLIER_EMAIL, supplierEmail);

                            Uri addedProdUri = getContentResolver().insert(productUri, values);
                            Log.d("Added Product", addedProdUri.toString());
                            Toast.makeText(
                                    getBaseContext()
                                    , "Successfully added " + "'" + productName + "'!"
                                    , Toast.LENGTH_LONG).show();
                            dialog.dismiss();
                        } else{
                            Toast.makeText(getBaseContext(), productValidation.getToastMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(
                this
                ,productUri
                ,null, null ,null, null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor products) {
        productAdapter.swapCursor(products);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {}

    private boolean priceIsValid(String priceString){

        // Source: http://docs.oracle.com/javase/6/docs/api/java/lang/Double.html#valueOf%28java.lang.String%29
        // Given by: http://stackoverflow.com/a/3543749/5302182

        final String Digits     = "(\\p{Digit}+)";
        final String HexDigits  = "(\\p{XDigit}+)";
        // an exponent is 'e' or 'E' followed by an optionally
        // signed decimal integer.
        final String Exp        = "[eE][+-]?"+Digits;
        final String fpRegex    =
                ("[\\x00-\\x20]*"+  // Optional leading "whitespace"
                        "[+-]?(" + // Optional sign character
                        "NaN|" +           // "NaN" string
                        "Infinity|" +      // "Infinity" string

                        // A decimal floating-point string representing a finite positive
                        // number without a leading sign has at most five basic pieces:
                        // Digits . Digits ExponentPart FloatTypeSuffix
                        //
                        // Since this method allows integer-only strings as input
                        // in addition to strings of floating-point literals, the
                        // two sub-patterns below are simplifications of the grammar
                        // productions from the Java Language Specification, 2nd
                        // edition, section 3.10.2.

                        // Digits ._opt Digits_opt ExponentPart_opt FloatTypeSuffix_opt
                        "((("+Digits+"(\\.)?("+Digits+"?)("+Exp+")?)|"+

                        // . Digits ExponentPart_opt FloatTypeSuffix_opt
                        "(\\.("+Digits+")("+Exp+")?)|"+

                        // Hexadecimal strings
                        "((" +
                        // 0[xX] HexDigits ._opt BinaryExponent FloatTypeSuffix_opt
                        "(0[xX]" + HexDigits + "(\\.)?)|" +

                        // 0[xX] HexDigits_opt . HexDigits BinaryExponent FloatTypeSuffix_opt
                        "(0[xX]" + HexDigits + "?(\\.)" + HexDigits + ")" +

                        ")[pP][+-]?" + Digits + "))" +
                        "[fFdD]?))" +
                        "[\\x00-\\x20]*");// Optional trailing "whitespace"

        return Pattern.matches(fpRegex, priceString);
    }

    private ProductValidation validateAddProduct(String productName, String price, String qty, String supplierName, String supplierEmail){
        boolean isValid = true;
//        String toastMessage = "Successfully added " + productName;
        String toastMessage = "\n";

        //TODO: Refactor existence validation
        //Validate product name existence
        if (productName.length()==0){
            isValid = false;
            toastMessage += "Please enter a product name." + "\n";
        }

        //Validate price
        if (!priceIsValid(price)){
            isValid = false;
            toastMessage += "Please enter a valid price." +"\n";
        } else if (Double.parseDouble(price)<0){
            isValid = false;
            toastMessage += "Please enter a valid price." + "\n";
        }

        // Validate unique product name
        Cursor checkDuplicate = getContentResolver().query(productUri,null, ProductEntry.COLUMN_PRODUCT + "=?", new String[] { productName }, null);
        if (checkDuplicate.getCount() > 0){
            isValid = false;
            toastMessage += "Product is already in inventory." + "\n";
        }

        //Validate supplier name existence
        if (supplierName.length()==0){
            isValid = false;
            toastMessage += "Please enter a supplier name." + "\n";
        }

        //Validate supplier email existence
        if (supplierEmail.length()==0){
            isValid = false;
            toastMessage += "Please enter a supplier email." + "\n";
        }

        // Validate Email
        if (!Patterns.EMAIL_ADDRESS.matcher(supplierEmail).matches()){
            isValid = false;
            toastMessage += "Please enter a valid email address."  + "\n";
        }


        return new ProductValidation(isValid, toastMessage);
    }

    private class ProductValidation {
        private boolean isValid;
        private String toastMessage;

        public ProductValidation(boolean isValid, String toastMessage) {
            this.isValid = isValid;
            this.toastMessage = toastMessage;
        }

        public boolean isValid() {
            return isValid;
        }

        public String getToastMessage() {
            return toastMessage;
        }
    }


    public void seedData(){
        // Clear DB
        getContentResolver().delete(productUri, null, null);

        // Seed data
        ContentValues values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT, "Ice Cream");
        values.put(ProductEntry.COLUMN_PRICE, "2.00");
        values.put(ProductEntry.COLUMN_QTY, "5");
        values.put(ProductEntry.COLUMN_SUPPLIER_NAME, "Purity Ice Cream");
        values.put(ProductEntry.COLUMN_SUPPLIER_EMAIL, "purity@icecream.com");
        getContentResolver().insert(productUri, values);

        values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT, "Coffee");
        values.put(ProductEntry.COLUMN_PRICE, "1.00");
        values.put(ProductEntry.COLUMN_QTY, "3");
        values.put(ProductEntry.COLUMN_SUPPLIER_NAME, "Starbucks");
        values.put(ProductEntry.COLUMN_SUPPLIER_EMAIL, "starbucks@starbucks.com");
        getContentResolver().insert(productUri, values);

        values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT, "Tea");
        values.put(ProductEntry.COLUMN_PRICE, "1.00");
        values.put(ProductEntry.COLUMN_QTY, "3");
        values.put(ProductEntry.COLUMN_SUPPLIER_NAME, "Starbucks");
        values.put(ProductEntry.COLUMN_SUPPLIER_EMAIL, "starbucks@starbucks.com");
        getContentResolver().insert(productUri, values);

        values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT, "Espresso");
        values.put(ProductEntry.COLUMN_PRICE, "1.00");
        values.put(ProductEntry.COLUMN_QTY, "3");
        values.put(ProductEntry.COLUMN_SUPPLIER_NAME, "Starbucks");
        values.put(ProductEntry.COLUMN_SUPPLIER_EMAIL, "starbucks@starbucks.com");
        getContentResolver().insert(productUri, values);

        values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT, "Latte");
        values.put(ProductEntry.COLUMN_PRICE, "1.00");
        values.put(ProductEntry.COLUMN_QTY, "3");
        values.put(ProductEntry.COLUMN_SUPPLIER_NAME, "Starbucks");
        values.put(ProductEntry.COLUMN_SUPPLIER_EMAIL, "starbucks@starbucks.com");
        getContentResolver().insert(productUri, values);

        values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT, "Americano");
        values.put(ProductEntry.COLUMN_PRICE, "1.00");
        values.put(ProductEntry.COLUMN_QTY, "3");
        values.put(ProductEntry.COLUMN_SUPPLIER_NAME, "Starbucks");
        values.put(ProductEntry.COLUMN_SUPPLIER_EMAIL, "starbucks@starbucks.com");
        getContentResolver().insert(productUri, values);
    }
}
