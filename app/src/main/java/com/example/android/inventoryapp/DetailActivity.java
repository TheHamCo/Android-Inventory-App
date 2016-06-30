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
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.inventoryapp.db.ProductContract.ProductEntry;

// Used to extend Activity instead of AppCompatActivity to support AlertDialog
// SOURCE: http://stackoverflow.com/a/21815015/5302182
// Then FragmentActivity
// Then back to AppCompatActivity on recc of:
// http://stackoverflow.com/questions/28795544/adding-actionbar-to-fragmentactivity
// TODO: Figure out why
public class DetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    Uri productIdUri;
    int currQty;
    String supplierName;
    String supplierEmail;
    String productName;
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

        // Delete product button
        Button deleteProductButton = (Button)findViewById(R.id.delete_product_button);
        if (deleteProductButton != null) {
            deleteProductButton.setOnClickListener(new View.OnClickListener() {
                //TODO: put strings in resources
                @Override
                public void onClick(View v) {
                    final AlertDialog dialog = new AlertDialog.Builder(DetailActivity.this)
                            .setTitle("Confirm")
                            .setMessage("Are you sure you want to delete")
                            .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // Do nothing here because we overwrite later,
                                    // but this needs to be here to handle old versions of Android
                                    // SOURCE: http://stackoverflow.com/a/15619098/5302182
                                    Toast.makeText(getBaseContext(), "Product deleted", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .setNegativeButton("Cancel", null)
                            .create();
                    dialog.show();
                }
            });
        }

        Button orderMoreButton = (Button)findViewById(R.id.order_more_button);
        if (orderMoreButton != null) {
            orderMoreButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_SENDTO);
                    intent.setData(Uri.parse("mailto:"))
                            .putExtra(Intent.EXTRA_EMAIL, new String[] {supplierEmail})
                            //TODO: Move to strings resources
                            //TODO: format punctuation
                            .putExtra(Intent.EXTRA_SUBJECT, "Order Request for '" + productName +"'")
                            .putExtra(Intent.EXTRA_TEXT, "Dear " + supplierName + ":\n\nWe would like to order more of your product '" + productName + ".'\n\nRegards,\n");
                    if (intent.resolveActivity(getPackageManager()) != null){
                        startActivity(intent);
                    }
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
            supplierEmail = data.getString(data.getColumnIndex(ProductEntry.COLUMN_SUPPLIER_EMAIL));

            int productIndex = data.getColumnIndex(ProductEntry.COLUMN_PRODUCT);
            TextView productNameTextView = (TextView) findViewById(R.id.product_name);
            productName = data.getString(productIndex);
            productNameTextView.setText(productName);

            int supplierIndex = data.getColumnIndex(ProductEntry.COLUMN_SUPPLIER_NAME);
            TextView supplierTextView = (TextView) findViewById(R.id.supplier_name);
            supplierName = data.getString(supplierIndex);
            supplierTextView.setText(supplierName);

            int priceIndex = data.getColumnIndex(ProductEntry.COLUMN_PRICE);
            TextView priceTextView = (TextView) findViewById(R.id.price);
            String price = data.getString(priceIndex);
            priceTextView.setText(price);

            int qtyIndex = data.getColumnIndex(ProductEntry.COLUMN_QTY);
            TextView qtyTextView = (TextView)findViewById(R.id.qty);
            String qty = data.getString(qtyIndex);
            qtyTextView.setText(qty);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {}
}
