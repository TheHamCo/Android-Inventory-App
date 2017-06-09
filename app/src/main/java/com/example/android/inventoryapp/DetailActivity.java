package com.example.android.inventoryapp;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.inventoryapp.db.ProductContract.ProductEntry;

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
 * - ASYNC IMAGE DOWNLOADER
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
    private String productName;
    private String supplierName;
    private String currencySymbol;
    private Double price;
    private int currQty;
    private String supplierEmail;

    // Make decreaseQtyButton available globally to disable in CursorLoader onLoadFinished
    Button decreaseQtyButton;

    // Unique CursorLoader ID
    public static final int DETAIL_LOADER = 0;

    private static final String DEFAULT_PICTURE_URL = "http://i.imgur.com/k3A1aSj.jpg";


    /*ON CREATE STUFF*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        getIntentData();
        startCursorLoader();
        setDecreaseQuantityButton();
        setIncreaseQuantityButton();
        setDeleteProductButton();
        setOrderMoreButton();
    }

    private void setOrderMoreButton() {
        Button orderMoreButton = (Button)findViewById(R.id.order_more_button);
        if (orderMoreButton != null) {
            orderMoreButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sendUserToEmailApp();
                }
            });
        }
    }

    private void sendUserToEmailApp() {
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

    private void setDeleteProductButton() {
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
                                    displaySuccessMessage();
                                    takeUserBackHome();
                                }

                                private void takeUserBackHome() {
                                    Intent intent = new Intent(DetailActivity.this, MainActivity.class);
                                    startActivity(intent);
                                }

                                private void displaySuccessMessage() {
                                    Toast.makeText(getBaseContext(), productName + getString(R.string.deleted), Toast.LENGTH_SHORT).show();
                                }
                            })
                            .setNegativeButton(R.string.cancel, null)
                            .create();
                    dialog.show();
                }
            });
        }
    }

    private void setIncreaseQuantityButton() {
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

    private void setDecreaseQuantityButton() {
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
    }

    private void startCursorLoader() {
        getSupportLoaderManager().initLoader(DETAIL_LOADER, null, this);
    }

    private void getIntentData() {
        Intent detailIntent = getIntent();
        currencySymbol = detailIntent.getStringExtra("currencySymbol");
        productIdUri = Uri.parse(detailIntent.getStringExtra("detailUri"));
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

        boolean isCursorInitializedToPreventError = data.moveToFirst();
        if (isCursorInitializedToPreventError) {
            // Get data in order they appear in the view
            displayProductImage(data);

            setProductName(data);
            displayProductName(data);

            setSupplierName(data);
            displaySupplierName(data);

            setPrice(data);
            displayPrice(data);

            setCurrentQuantity(data);
            displayQuantity(data);

            setSupplierEmail(data);

            preventUserCausingNegativeQuantities();
        }
    }

    private void displayDataInTextView(Cursor data, int textViewId, String textToDisplay) {
        TextView supplierTextView = (TextView) findViewById(textViewId);
        if (supplierTextView != null) {
            supplierTextView.setText(textToDisplay);
        }
    }

    private void setProductName(Cursor data){
        int productIndex = data.getColumnIndex(ProductEntry.COLUMN_PRODUCT);
        productName = data.getString(productIndex);
    }

    private void setSupplierName(Cursor data){
        int supplierIndex = data.getColumnIndex(ProductEntry.COLUMN_SUPPLIER_NAME);
        supplierName = data.getString(supplierIndex);
    }

    private void setPrice(Cursor data) {
        int priceIndex = data.getColumnIndex(ProductEntry.COLUMN_PRICE);
        price = data.getDouble(priceIndex);
    }

    private void setCurrentQuantity(Cursor data) {
        // Make QTY global for adding / subtracting in buttons
        int qtyIndex = data.getColumnIndex(ProductEntry.COLUMN_QTY);
        currQty = data.getInt(qtyIndex);
    }

    private void setSupplierEmail(Cursor data) {
        // Make global for "order more" button
        supplierEmail = data.getString(data.getColumnIndex(ProductEntry.COLUMN_SUPPLIER_EMAIL));
    }

    private void displayProductImage(Cursor data) {
        int imageIndex = data.getColumnIndex(ProductEntry.COLUMN_IMAGE_URL);
        ImageView productImageView = (ImageView)findViewById(R.id.product_image);
        String imageUrl = data.getString(imageIndex);
        // DEFAULT PICTURE
        if (imageUrl.length() == 0 ){
            imageUrl = DEFAULT_PICTURE_URL;
        }
        new DownloadImageTask(productImageView).execute(imageUrl);
    }

    private void displayProductName(Cursor data) {
        displayDataInTextView(data, R.id.product_name, productName);
    }

    private void displaySupplierName(Cursor data){
        displayDataInTextView(data, R.id.supplier_name, supplierName);
    }

    private void displayPrice(Cursor data) {
        // Display price in format "[currrency symbol]xx.xx"
        String formattedPrice = currencySymbol + String.format("%.02f", price);
        displayDataInTextView(data, R.id.price, formattedPrice);
    }

    private void displayQuantity(Cursor data) {
        displayDataInTextView(data, R.id.qty, String.valueOf(currQty));
    }

    private void preventUserCausingNegativeQuantities() {
        if (currQty == 0){
            decreaseQtyButton.setEnabled(false);
        }else{
            decreaseQtyButton.setEnabled(true);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {}
}
