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
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.example.android.inventoryapp.db.ProductContract.ProductEntry;

import java.util.Currency;
import java.util.Locale;
import java.util.regex.Pattern;

// General TODO: decide between "qty" or "quantity" in documentation
/**
 * TABLE OF CONTENTS:
 * - STATE VARS
 * - ONCREATE STUFF
 *      - Init CursorLoader
*       - Get locale currency
 *      - Set button for seeding data
 *      - Bind ListView
 * - ADD PRODUCT STUFF
 * - CURSOR LOADER STUFF
 * - VALIDATION STUFF
 * - SEED DATA STUFF
 */
public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    // Listing data
    private ProductAdapter productAdapter;
    ListView productList;

    // "content://com.example.android.inventoryapp.app/product"
    final static Uri productUri = ProductEntry.CONTENT_URI;

    // Unique CursorLoader ID(s)
    public static final int LIST_LOADER = 0;

    // Locale for currency
    Locale localeSetting;
    //Currency symbol
    String currencySymbol;

    /*ONCREATE STUFF*/
    @Override
    public View onCreateView(View parent, String name, Context context, AttributeSet attrs) {
        // Start CursorLoader here
        getSupportLoaderManager().initLoader(LIST_LOADER, null, this);
        return super.onCreateView(parent, name, context, attrs);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Use locale-specific currency symbol
        // (TODO: Currency conversion?)
        // Source: http://stackoverflow.com/a/14389640/5302182
        localeSetting = getResources().getConfiguration().locale;
        Currency localCurrency = Currency.getInstance(localeSetting);
        currencySymbol = localCurrency.getSymbol(localeSetting);

        // Button for resetting and seeding data
        Button seedButton = (Button)findViewById(R.id.reseed_button);
        if (seedButton != null) {
            seedButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    seedData();
                }
            });
        }

        // ListView
        productList = (ListView) findViewById(R.id.product_list);
        productAdapter = new ProductAdapter(this, null, currencySymbol);
        productList.setAdapter(productAdapter);
    }

    /*ADD PRODUCT STUFF*/
    // Plus button in AppBar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_product, menu);
        return super.onCreateOptionsMenu(menu);
    }

    // Add product AlertDialog is within
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.add_product:

                // Workarounds to get EditTexts in an AlertDialog
                // Source: http://stackoverflow.com/a/18176909/5302182
                //TODO: try http://stackoverflow.com/questions/2335813/how-to-inflate-one-view-with-a-layout
                //TODO: this might be useful http://stackoverflow.com/questions/5447092/get-context-inside-onclickdialoginterface-v-int-buttonid

                final EditText productNameEditText = new EditText(this);
                final EditText priceEditText = new EditText(this);
                final EditText qtyEditText = new EditText(this);
                final EditText supplierNameEditText = new EditText(this);
                final EditText supplierEmailEditText = new EditText(this);

                productNameEditText.setHint(R.string.product_name);
                priceEditText.setHint(R.string.price);
                qtyEditText.setHint(R.string.quantity);
                supplierNameEditText.setHint(R.string.supplier_name);
                supplierEmailEditText.setHint(R.string.supplier_email);

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
                        .setTitle(getString(R.string.add_a_new_product))
                        .setView(layout)
                        .setPositiveButton(getString(R.string.add), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Do nothing here because we overwrite later,
                                // but this needs to be here to handle old versions of Android
                                // SOURCE: http://stackoverflow.com/a/15619098/5302182
                            }
                        })
                        .setNegativeButton(getString(R.string.cancel), null)
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

                        // Remove currency symbols and commas from price
                        price = price.replaceAll("[" + currencySymbol + "]", "");
                        price = price.replaceAll("[,]", "");

                        // Product Validation
                        ProductValidation productValidation = validateAddProduct(productName, price, qty, supplierName, supplierEmail);

                        // Pass/fail
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

    /*CURSOR LOADER STUFF*/
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

    /*VALIDATION STUFF*/
    /**
     * Validates if a price string is a properly-formatted decimal number
     * @param priceString
     * @return the string is a decimal number
     */
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

    /**
     * Validates all inputs at once
     * @param productName
     * @param price
     * @param qty
     * @param supplierName
     * @param supplierEmail
     * @return ProductValidation with isValid and failure toast message
     */
    // TODO: separation of concerns
    private ProductValidation validateAddProduct(String productName, String price, String qty, String supplierName, String supplierEmail){
        boolean isValid = true;
        // Start with line break to keep toast padding even on all sides
        String toastMessage = "\n";

        //TODO: Refactor existence validation to an else if with duplicate validation?
        // Validate product name
        // Validate product name existence
        if (productName.length()==0){
            isValid = false;
            toastMessage += "Please enter a product name." + "\n";
        }
        // Validate unique product name
        Cursor checkDuplicate = getContentResolver().query(
                  productUri,null
                , ProductEntry.COLUMN_PRODUCT + "=?"
                , new String[] { productName }
                , null
        );
        if (checkDuplicate.getCount() > 0){
            isValid = false;
            toastMessage += "Product is already in inventory." + "\n";
        }
        checkDuplicate.close();

        // Validate price
        // Validate decimal number format
        if (!priceIsValid(price)){
            isValid = false;
            toastMessage += "Please enter a valid price." +"\n";
        // No negative prices
        } else if (Double.parseDouble(price)<0){
            isValid = false;
            toastMessage += "Please enter a valid price." + "\n";
        }

        // Validate supplier name existence
        if (supplierName.length()==0){
            isValid = false;
            toastMessage += "Please enter a supplier name." + "\n";
        }

        // Validate supplier email
        // Validate supplier email existence
        if (supplierEmail.length()==0){
            isValid = false;
            toastMessage += "Please enter a supplier email." + "\n";
        }
        // Validate email proper format
        if (!Patterns.EMAIL_ADDRESS.matcher(supplierEmail).matches()){
            isValid = false;
            toastMessage += "Please enter a valid email address."  + "\n";
        }

        return new ProductValidation(isValid, toastMessage);
    }

    /**
     * Wrapper for all validation-related variables:
     * Add product isValid
     * Add product message
     */
    // Source: http://stackoverflow.com/questions/457629/how-to-return-multiple-objects-from-a-java-method
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


    /*SEED DATA STUFF*/
    /**
     * Add starting data to the database
     */
    public void seedData(){
        // Clear DB
        getContentResolver().delete(productUri, null, null);

        // Seed data
        ContentValues values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT, "Pumpkin Spice Latte");
        values.put(ProductEntry.COLUMN_PRICE, "3.99");
        values.put(ProductEntry.COLUMN_QTY, "12");
        values.put(ProductEntry.COLUMN_SUPPLIER_NAME, "Basic B Industries");
        values.put(ProductEntry.COLUMN_SUPPLIER_EMAIL, "bby@bbi.com");
        getContentResolver().insert(productUri, values);

        values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT, "Ice Cream");
        values.put(ProductEntry.COLUMN_PRICE, "2.00");
        values.put(ProductEntry.COLUMN_QTY, "5");
        values.put(ProductEntry.COLUMN_SUPPLIER_NAME, "Purity Ice Cream");
        values.put(ProductEntry.COLUMN_SUPPLIER_EMAIL, "purity@purityicecream.com");
        getContentResolver().insert(productUri, values);

        values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT, "Nougat");
        values.put(ProductEntry.COLUMN_PRICE, "7.00");
        values.put(ProductEntry.COLUMN_QTY, "7");
        values.put(ProductEntry.COLUMN_SUPPLIER_NAME, "Android");
        values.put(ProductEntry.COLUMN_SUPPLIER_EMAIL, "nutella@android.com");
        getContentResolver().insert(productUri, values);

        values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT, "Nutella");
        values.put(ProductEntry.COLUMN_PRICE, "10.00");
        values.put(ProductEntry.COLUMN_QTY, "10");
        values.put(ProductEntry.COLUMN_SUPPLIER_NAME, "Wishful Thinking");
        values.put(ProductEntry.COLUMN_SUPPLIER_EMAIL, "nutella@android.com");
        getContentResolver().insert(productUri, values);

        values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT, "Tea");
        values.put(ProductEntry.COLUMN_PRICE, "0.99");
        values.put(ProductEntry.COLUMN_QTY, "3");
        values.put(ProductEntry.COLUMN_SUPPLIER_NAME, "Dutch East India Company");
        values.put(ProductEntry.COLUMN_SUPPLIER_EMAIL, "1602@deic.com");
        getContentResolver().insert(productUri, values);

        values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT, "Espresso");
        values.put(ProductEntry.COLUMN_PRICE, "3.00");
        values.put(ProductEntry.COLUMN_QTY, "3");
        values.put(ProductEntry.COLUMN_SUPPLIER_NAME, "Starbucks");
        values.put(ProductEntry.COLUMN_SUPPLIER_EMAIL, "starbucks@starbucks.com");
        getContentResolver().insert(productUri, values);

        values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT, "Latte");
        values.put(ProductEntry.COLUMN_PRICE, "4.00");
        values.put(ProductEntry.COLUMN_QTY, "3");
        values.put(ProductEntry.COLUMN_SUPPLIER_NAME, "Starbucks");
        values.put(ProductEntry.COLUMN_SUPPLIER_EMAIL, "starbucks@starbucks.com");
        getContentResolver().insert(productUri, values);

        values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT, "Americano");
        values.put(ProductEntry.COLUMN_PRICE, "3.50");
        values.put(ProductEntry.COLUMN_QTY, "3");
        values.put(ProductEntry.COLUMN_SUPPLIER_NAME, "Tim Hortons");
        values.put(ProductEntry.COLUMN_SUPPLIER_EMAIL, "canada@canada.ca");
        getContentResolver().insert(productUri, values);
    }
}
