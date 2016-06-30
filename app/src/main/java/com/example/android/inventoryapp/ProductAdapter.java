package com.example.android.inventoryapp;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;
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
 * Created by mdd23 on 6/29/2016.
 */
public class ProductAdapter extends CursorAdapter {

    String currencySymbol;

    public ProductAdapter(Context context, Cursor c, String currencySymbol) {
        super(context, c, 0);
        this.currencySymbol = currencySymbol;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.product_card, parent, false);
    }

    @Override
    public void bindView(View view, final Context context, final Cursor cursor) {
        TextView idTextView = (TextView)view.findViewById(R.id._id);
        TextView productTextView = (TextView)view.findViewById(R.id.product_name);
        TextView priceTextView = (TextView)view.findViewById(R.id.price);
        TextView qtyTextView = (TextView)view.findViewById(R.id.qty);

        int idIndex = cursor.getColumnIndex(ProductEntry._ID);
        int productIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT);
        int priceIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRICE);
        int qtyIndex = cursor.getColumnIndex(ProductEntry.COLUMN_QTY);

        final long id = cursor.getLong(idIndex);
        String product = cursor.getString(productIndex);
        // Display price in format "xx.xx"
        Double price = cursor.getDouble(priceIndex);
        int qty = cursor.getInt(qtyIndex);

        idTextView.setText(Long.toString(id));
        productTextView.setText(product);
        // Display price in format "[currency symbol]xx.xx"
        priceTextView.setText(currencySymbol + String.format("%.02f", price));
        qtyTextView.setText(Integer.toString(qty));

        //Button
        //SOLUTION SOURCE: http://stackoverflow.com/a/22444284/5302182
        final Button saleButton = (Button)view.findViewById(R.id.sale_button);

        if (qty == 0){
            saleButton.setEnabled(false);
        }else{
            saleButton.setEnabled(true);
        }

        final int position = cursor.getPosition();
        saleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cursor.moveToPosition(position);
                int currQty = cursor.getInt(cursor.getColumnIndex(ProductEntry.COLUMN_QTY));

                int idIndex = cursor.getColumnIndex(ProductEntry._ID);
                int id = cursor.getInt(idIndex);
                Uri productIdUri = ProductEntry.buildLocationuri(id);

                Log.d("product id uri", productIdUri.toString());
                Log.d("ID", Integer.toString(v.getId()));

                //Update the qty
                ContentValues values = new ContentValues();
                values.put(ProductEntry.COLUMN_QTY, --currQty);

                context.getContentResolver().update(productIdUri,values,null,null);

                String productName = cursor.getString(cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT));

                // Display sale confirmation toast with custom, shorter duration
                // SOURCE: http://stackoverflow.com/a/14503803/5302182
                final Toast saleToast = Toast.makeText(context, "'" + productName + "' " + "sold!", Toast.LENGTH_SHORT);
                saleToast.show();

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        saleToast.cancel();
                    }
                }, 550);
            }
        });

        //Detail
        final LinearLayout detailClickable = (LinearLayout)view.findViewById(R.id.detail_clickable);
        detailClickable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cursor.moveToPosition(position);
                int currQty = cursor.getInt(cursor.getColumnIndex(ProductEntry.COLUMN_QTY));

                int idIndex = cursor.getColumnIndex(ProductEntry._ID);
                int id = cursor.getInt(idIndex);
                Uri productIdUri = ProductEntry.buildLocationuri(id);

                Intent detailIntent = new Intent(context, DetailActivity.class);
                detailIntent.putExtra("detailUri", productIdUri.toString());
                detailIntent.putExtra("currencySymbol", currencySymbol);

                Log.d("product id uri", productIdUri.toString());
                context.startActivity(detailIntent);
//                Log.d("ID", "DETAIL!!" + Integer.toString(v.getId()));
//
//                //Update the qty
//                ContentValues values = new ContentValues();
//                values.put(ProductEntry.COLUMN_PRICE, ++currQty);
//
//                context.getContentResolver().update(productIdUri,values,null,null);
            }
        });
    }
}
