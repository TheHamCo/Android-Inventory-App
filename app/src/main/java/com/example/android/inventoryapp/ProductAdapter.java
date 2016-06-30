package com.example.android.inventoryapp;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.android.inventoryapp.db.ProductContract.ProductEntry;

/**
 * Created by mdd23 on 6/29/2016.
 */
public class ProductAdapter extends CursorAdapter {

    public ProductAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.product_card, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView idTextView = (TextView)view.findViewById(R.id._id);
        TextView productTextView = (TextView)view.findViewById(R.id.product_name);
        TextView priceTextView = (TextView)view.findViewById(R.id.price);
        TextView qtyTextView = (TextView)view.findViewById(R.id.qty);

        int idIndex = cursor.getColumnIndex(ProductEntry._ID);
        int productIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT);
        int priceIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRICE);
        int qtyIndex = cursor.getColumnIndex(ProductEntry.COLUMN_QTY);

        long id = cursor.getLong(idIndex);
        String product = cursor.getString(productIndex);
        String price = cursor.getString(priceIndex);
        int qty = cursor.getInt(qtyIndex);

        idTextView.setText(Long.toString(id));
        productTextView.setText(product);
        priceTextView.setText(price);
        qtyTextView.setText(Integer.toString(qty));
    }
}
