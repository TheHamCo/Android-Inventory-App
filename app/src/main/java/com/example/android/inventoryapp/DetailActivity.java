package com.example.android.inventoryapp;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.android.inventoryapp.db.ProductContract.ProductEntry;

public class DetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    Uri productIdUri;
    int currQty;
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

        Button decreaseQtyButton = (Button)findViewById(R.id.decrease_button);
        assert decreaseQtyButton != null;
        decreaseQtyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContentValues values = new ContentValues();
                values.put(ProductEntry.COLUMN_QTY, --currQty);

                getContentResolver().update(productIdUri,values,null,null);
            }
        });

        Button increaseQtyButton = (Button)findViewById(R.id.increase_button);
        if (increaseQtyButton != null) {
            increaseQtyButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ContentValues values = new ContentValues();
                    values.put(ProductEntry.COLUMN_QTY, ++currQty);

                    getContentResolver().update(productIdUri,values,null,null);
                }
            });
        }
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
            currQty = data.getInt(data.getColumnIndex(ProductEntry.COLUMN_QTY));

            int productIndex = data.getColumnIndex(ProductEntry.COLUMN_PRODUCT);
            TextView productNameTextView = (TextView) findViewById(R.id.product_name);
            String productName = data.getString(productIndex);
            productNameTextView.setText(productName);

            int qtyIndex = data.getColumnIndex(ProductEntry.COLUMN_QTY);
            TextView qtyTextView = (TextView)findViewById(R.id.qty);
            String qty = data.getString(qtyIndex);
            qtyTextView.setText(qty);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {}
}
