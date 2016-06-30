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
import android.util.AttributeSet;
import android.util.Log;
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

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    ListView productList;

    // /product
    final static Uri productUri = ProductEntry.CONTENT_URI;

    private ProductAdapter productAdapter;

    public static final int LIST_LOADER = 0;

    @Override
    public View onCreateView(View parent, String name, Context context, AttributeSet attrs) {
        getSupportLoaderManager().initLoader(0, null, this);
        return super.onCreateView(parent, name, context, attrs);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
                        .setPositiveButton("Add", null)
                        .setNegativeButton("Cancel", null)
                        .create();
                dialog.show();

                dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialogInterface) {
                        Button addButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);

                        addButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
//                                EditText productNameEditText = (EditText)findViewById(R.id.product_name_new);
//                                EditText priceEditText = (EditText)findViewById(R.id.price_new);
//                                EditText qtyEditText = (EditText)findViewById(R.id.qty_new);
//                                EditText supplierNameEditText = (EditText)findViewById(R.id.supplier_name_new);
//                                EditText supplierEmailEditText = (EditText)findViewById(R.id.supplier_email_new);

                                String productName = productNameEditText.getText().toString();
                                String price = priceEditText.getText().toString();
                                String qty = qtyEditText.getText().toString();
                                String supplierName = supplierNameEditText.getText().toString();
                                String supplierEmail = supplierEmailEditText.getText().toString();



                                if (true) {
                                    Toast.makeText(getBaseContext(), "Need a product name!", Toast.LENGTH_SHORT).show();
                                } else{
                                    Toast.makeText(
                                            getBaseContext()
                                            , productName + "\n" + price + "\n" + qty + "\n" + supplierName + "\n" + supplierEmail
                                            , Toast.LENGTH_LONG).show();
                                    dialog.dismiss();
                                }
                            }
                        });
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
