package com.example.android.inventoryapp;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.inventoryapp.db.ProductContract.ProductEntry;

/**
 * TABLE OF CONTENTS:
 * - INITIAL STUFF
 * - BUTTON FOR TRACKING SALE
 * - CLICKABLE AREA FOR ROUTING TO DETAIL ACTIVITY
 */
public class ProductAdapter extends CursorAdapter {

    // Get currency symbol from parent context
    private String currencySymbol;

    private Toast saleToast;

    public ProductAdapter(Context context, Cursor c, String currencySymbol) {
        super(context, c, 0);
        // Get currency symbol from parent context
        this.currencySymbol = currencySymbol;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.product_card, parent, false);
    }

    @Override
    public void bindView(View view, final Context context, final Cursor cursor) {
        TextView productTextView = (TextView) view.findViewById(R.id.product_name);
        TextView priceTextView = (TextView) view.findViewById(R.id.price);
        TextView qtyTextView = (TextView) view.findViewById(R.id.qty);


        int productIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT);
        int priceIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRICE);
        int qtyIndex = cursor.getColumnIndex(ProductEntry.COLUMN_QTY);


        String productName = cursor.getString(productIndex);
        // Cast for displaying price in format "xx.xx"
        Double price = cursor.getDouble(priceIndex);
        int qty = cursor.getInt(qtyIndex);


        productTextView.setText(productName);
        // Display price in format "[currency symbol]xx.xx"
        priceTextView.setText(currencySymbol + String.format("%.02f", price));
        qtyTextView.setText(Integer.toString(qty));


        // Button for tracking sale (qty--)
        // SOLUTION SOURCE: http://stackoverflow.com/a/22444284/5302182
        final Button saleButton = (Button) view.findViewById(R.id.sale_button);

        // Disable to prevent negative quantities
        if (qty == 0) {
            saleButton.setEnabled(false);
        } else {
            saleButton.setEnabled(true);
        }

        // Pass cursor data into the onClickListener to change quantity
        final int position = cursor.getPosition();
        saleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // cursor must look at the relevant row data
                cursor.moveToPosition(position);

                int currQty = cursor.getInt(cursor.getColumnIndex(ProductEntry.COLUMN_QTY));

                // Get URI to update
                int idIndex = cursor.getColumnIndex(ProductEntry._ID);
                int id = cursor.getInt(idIndex);
                Uri productIdUri = ProductEntry.buildLocationuri(id);

                //Update the qty
                ContentValues values = new ContentValues();
                values.put(ProductEntry.COLUMN_QTY, --currQty);

                context.getContentResolver().update(productIdUri, values, null, null);


                // Prevent sale confirmation toasts from overlapping
                if (saleToast != null) {
                    saleToast.cancel();
                }

                String productName = cursor.getString(cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT));
                saleToast = Toast.makeText(context, "'" + productName + "' " + "sold!", Toast.LENGTH_SHORT);
                saleToast.show();
            }
        });


        // Clickable Area for getting to DetailActivity
        final LinearLayout detailClickable = (LinearLayout) view.findViewById(R.id.detail_clickable);
        detailClickable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // cursor must look at the relevant row data
                cursor.moveToPosition(position);

                // Get URI to route to
                int idIndex = cursor.getColumnIndex(ProductEntry._ID);
                int id = cursor.getInt(idIndex);
                Uri productIdUri = ProductEntry.buildLocationuri(id);

                Intent detailIntent = new Intent(context, DetailActivity.class);
                // Pass URI and currency through to intent
                detailIntent.putExtra("detailUri", productIdUri.toString());
                detailIntent.putExtra("currencySymbol", currencySymbol);

                context.startActivity(detailIntent);
            }
        });
    }
}
