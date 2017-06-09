package com.example.android.inventoryapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.android.inventoryapp.db.ProductContract.ProductEntry;
import com.example.android.inventoryapp.sampledata.PresetSampleData;

import java.util.Currency;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

// General TODO: decide between "qty" or "quantity" in documentation
/**
 * TABLE OF CONTENTS:
 * - STATE VARS
 * - ONCREATE STUFF
 *      - Init CursorLoader
*       - Get locale currency
 *      - Set button for seeding data
 *      - Bind ListView
 * - CURSOR LOADER STUFF
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
        startCursorLoader();
        return super.onCreateView(parent, name, context, attrs);
    }

    private void startCursorLoader() {
        getSupportLoaderManager().initLoader(LIST_LOADER, null, this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findLocaleSpecificCurrencySymbol();
        setButtonForResettingSampleData();
        populateItemListView();
    }

    private void populateItemListView() {
        // ListView
        productList = (ListView) findViewById(R.id.product_list);
        productAdapter = new ProductAdapter(this, null, currencySymbol);
        productList.setAdapter(productAdapter);
    }

    private void setButtonForResettingSampleData() {
        // Button for resetting and seeding data
        Button seedButton = (Button)findViewById(R.id.reseed_button);
        if (seedButton != null) {
            seedButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    seedData(PresetSampleData.getPresetData());
                }
            });
        }
    }

    private void findLocaleSpecificCurrencySymbol() {
        // Use locale-specific currency symbol
        // (TODO: Currency conversion?)
        // Source: http://stackoverflow.com/a/14389640/5302182
        localeSetting = getResources().getConfiguration().locale;
        Currency localCurrency = Currency.getInstance(localeSetting);
        currencySymbol = localCurrency.getSymbol(localeSetting);
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
                new AddProductDialog(this).showAddProductDialog(currencySymbol);
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
                , productUri
                , null, null ,null, null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor products) {
        TextView noProductsFoundTextView = (TextView)findViewById(R.id.no_products_found);
        if (products.getCount() == 0) {
            noProductsFoundTextView.setVisibility(TextView.VISIBLE);
        } else {
            noProductsFoundTextView.setVisibility(TextView.GONE);
            productAdapter.swapCursor(products);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {}

    /*SEED DATA STUFF*/
    /**
     * Add starting data to the database
     */
    public void seedData(List<HashMap<String, String>> sampleData){
        deleteAllDatabaseEntries();
        addSampleDataToDatabase(sampleData);
    }

    private void addSampleDataToDatabase(List<HashMap<String, String>> sampleData) {
        for (HashMap<String, String> item : sampleData) {
            addColumnsToDatabase(item);
        }
    }

    private void addColumnsToDatabase(HashMap<String, String> item) {
        ContentValues values;
        values = new ContentValues();
        for (Map.Entry<String,String> column : item.entrySet()) {
            values.put(column.getKey(), column.getValue());
        }
        getContentResolver().insert(productUri, values);
    }

    private void deleteAllDatabaseEntries() {
        getContentResolver().delete(productUri, null, null);
    }
}
