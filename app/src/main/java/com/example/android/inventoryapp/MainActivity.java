package com.example.android.inventoryapp;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import com.example.android.inventoryapp.db.ProductContract;
import com.example.android.inventoryapp.db.ProductContract.ProductEntry;

public class MainActivity extends AppCompatActivity {

    ListView productList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Uri productUri = ProductContract.BASE_CONTENT_URI;

        ContentValues values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT, "Ice Cream");
        values.put(ProductEntry.COLUMN_PRICE, "2.00");
        values.put(ProductEntry.COLUMN_QTY, "5");
        values.put(ProductEntry.COLUMN_SUPPLIER_NAME, "Purity Ice Cream");
        values.put(ProductEntry.COLUMN_SUPPLIER_EMAIL, "purity@icecream.com");

        getContentResolver().insert(productUri, values);

        Cursor products = getContentResolver().query(productUri, null, null, null, null);

        productList = (ListView) findViewById(R.id.product_list);

        SimpleCursorAdapter adapter = new SimpleCursorAdapter(
                this
                ,R.layout.product_card
                ,products
                ,new String[] { ProductEntry.COLUMN_PRODUCT, ProductEntry.COLUMN_PRICE, ProductEntry.COLUMN_QTY }
                ,new int[] { R.id.product_name, R.id.price, R.id.qty }
        );

        productList.setAdapter(adapter);

    }
}
