package com.example.android.inventoryapp;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.example.android.inventoryapp.db.ProductContract.ProductEntry;

public class DetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    Uri productIdUri;
    public static final int DETAIL_LOADER = 0;

//    @Override
//    public View onCreateView(View parent, String name, Context context, AttributeSet attrs) {
//        Intent detailIntent = getIntent();
//        productIdUri = Uri.parse(detailIntent.getStringExtra("detailUri"));
//        getSupportLoaderManager().initLoader(DETAIL_LOADER, null, this);
//        Log.d("productIdUri", productIdUri.toString());
//        return super.onCreateView(parent, name, context, attrs);
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Intent detailIntent = getIntent();
        productIdUri = Uri.parse(detailIntent.getStringExtra("detailUri"));
        getSupportLoaderManager().initLoader(DETAIL_LOADER, null, this);
        Log.d("productIdUri", productIdUri.toString());

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(
                this
                ,productIdUri
                ,null, null ,null, null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        if (data.moveToFirst()) {
            int productIndex = data.getColumnIndex(ProductEntry.COLUMN_PRODUCT);
            TextView productNameTextView = (TextView) findViewById(R.id.product_name);

//        String productName = "hey";
//        String productName = data.getString(2);
            String productName = data.getString(productIndex);

            productNameTextView.setText(productName);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {}
}
