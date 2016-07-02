package com.example.android.inventoryapp;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.inventoryapp.db.ProductContract.ProductEntry;

import java.io.InputStream;

/**
 * TABLE OF CONTENTS
 * - STATE VARIABLES
 *      - Product with ID Uri
 *      - Product info
 *      - Decrease Qty Button
 *      - Unique CursorLoader ID
 * - ONCREATE STUFF
 *      - Get intent data
 *      - Initialize CursorLoader
 *              (Buttons:)
 *      - Decrease quantity button
 *      - Increase quantity button
 *      - Delete product button
 *      - Order more button
 * - CURSORLOADER STUFF
 *      - Get and bind data
 *      - Make quantity global
 *      - Make supplier email global
 *      - Disable decrease button
 */
// Used to extend Activity instead of AppCompatActivity to support AlertDialog
// SOURCE: http://stackoverflow.com/a/21815015/5302182
// Then FragmentActivity
// Then back to AppCompatActivity on recc of:
// http://stackoverflow.com/questions/28795544/adding-actionbar-to-fragmentactivity
// TODO: Figure out why this magically works
// http://stackoverflow.com/questions/21814825/you-need-to-use-a-theme-appcompat-theme-or-descendant-with-this-activity
public class DetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    // "content://com.example.android.inventoryapp.app/product/#"
    Uri productIdUri;

    // Product info
    // TODO: Why is price not on here?
    String productName;
    String supplierName;
    String currencySymbol;
    int currQty;
    String supplierEmail;

    // Make decreaseQtyButton available globally to disable in CursorLoader onLoadFinished
    Button decreaseQtyButton;

    // Unique CursorLoader ID
    public static final int DETAIL_LOADER = 0;


    /*ON CREATE STUFF*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        // Get intent data
        Intent detailIntent = getIntent();
        currencySymbol = detailIntent.getStringExtra("currencySymbol");
        productIdUri = Uri.parse(detailIntent.getStringExtra("detailUri"));

        // Initialize CursorLoader
        getSupportLoaderManager().initLoader(DETAIL_LOADER, null, this);

        // Decrease Quantity Button
        decreaseQtyButton = (Button)findViewById(R.id.decrease_button);
        if (decreaseQtyButton != null) {
            decreaseQtyButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ContentValues values = new ContentValues();
                    values.put(ProductEntry.COLUMN_QTY, --currQty);

                    getContentResolver().update(productIdUri,values,null,null);
                }
            });
        }

        // Increase Quantity Button
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

        // Delete Product Button
        Button deleteProductButton = (Button)findViewById(R.id.delete_product_button);
        if (deleteProductButton != null) {
            deleteProductButton.setOnClickListener(new View.OnClickListener() {
                //TODO: put strings in resources
                @Override
                public void onClick(View v) {
                    final AlertDialog dialog = new AlertDialog.Builder(DetailActivity.this)
                            .setTitle(getString(R.string.confirm))
                            .setMessage(getString(R.string.are_you_sure_you_want_to_delete) + productName +"?")
                            .setPositiveButton(getString(R.string.delete), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // TODO: Delete product error handling
                                    getContentResolver().delete(productIdUri, null, null);

                                    // Display success message
                                    Toast.makeText(getBaseContext(), productName +getString(R.string.deleted), Toast.LENGTH_SHORT).show();

                                    // Reroute back home
                                    Intent intent = new Intent(DetailActivity.this, MainActivity.class);
                                    startActivity(intent);
                                }
                            })
                            .setNegativeButton(R.string.cancel, null)
                            .create();
                    dialog.show();
                }
            });
        }

        // Order More Button
        // Formats an email to the supplier
        // Sends user to email app
        Button orderMoreButton = (Button)findViewById(R.id.order_more_button);
        if (orderMoreButton != null) {
            orderMoreButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_SENDTO);
                    intent.setData(Uri.parse("mailto:"))
                            .putExtra(Intent.EXTRA_EMAIL, new String[] {supplierEmail})
                            .putExtra(Intent.EXTRA_SUBJECT, getString(R.string.order_request_for) + productName +"'")
                            .putExtra(Intent.EXTRA_TEXT
                                    , getString(R.string.dear) + supplierName
                                            + getString(R.string.we_would_like_to_order_more_of_your_product) + productName
                                            + getString(R.string.regards));
                    if (intent.resolveActivity(getPackageManager()) != null){
                        startActivity(intent);
                    }
                }
            });
        }
    }

    /*CURSORLOADER STUFF*/
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(
                  this
                , productIdUri
                , null, null ,null, null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        // Init cursor (required to prevent error)
        if (data.moveToFirst()) {
            // Get data in order they appear in the view

            // Image
            int imageIndex = data.getColumnIndex(ProductEntry.COLUMN_IMAGE_URL);
            ImageView productImageView = (ImageView)findViewById(R.id.product_image);
            String imageUrl = data.getString(imageIndex);
            new DownloadImageTask(productImageView).execute(imageUrl);

            // Product Name
            int productIndex = data.getColumnIndex(ProductEntry.COLUMN_PRODUCT);
            TextView productNameTextView = (TextView) findViewById(R.id.product_name);
            productName = data.getString(productIndex);
            if (productNameTextView != null) {
                productNameTextView.setText(productName);
            }

            // Supplier Name
            int supplierIndex = data.getColumnIndex(ProductEntry.COLUMN_SUPPLIER_NAME);
            TextView supplierTextView = (TextView) findViewById(R.id.supplier_name);
            supplierName = data.getString(supplierIndex);
            if (supplierTextView != null) {
                supplierTextView.setText(supplierName);
            }

            // Price
            int priceIndex = data.getColumnIndex(ProductEntry.COLUMN_PRICE);
            TextView priceTextView = (TextView) findViewById(R.id.price);
            // Display price in format "[currrency symbol]xx.xx"
            Double price = data.getDouble(priceIndex);
            if (priceTextView != null) {
                priceTextView.setText(currencySymbol + String.format("%.02f", price));
            }

            // Quantity
            // Make QTY global for adding / subtracting in buttons
            currQty = data.getInt(data.getColumnIndex(ProductEntry.COLUMN_QTY));
            TextView qtyTextView = (TextView)findViewById(R.id.qty);
            if (qtyTextView != null) {
                qtyTextView.setText(Integer.toString(currQty));
            }

            // Supplier Email
            // Make global for "order more" button
            supplierEmail = data.getString(data.getColumnIndex(ProductEntry.COLUMN_SUPPLIER_EMAIL));

            // Disable decrease button to prevent negative quantity
            if (currQty == 0){
                decreaseQtyButton.setEnabled(false);
            }else{
                decreaseQtyButton.setEnabled(true);
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {}

    /*ASYNC IMAGE DOWNLOADER*/
    // Source: http://stackoverflow.com/a/10868126/5302182
    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }
}
