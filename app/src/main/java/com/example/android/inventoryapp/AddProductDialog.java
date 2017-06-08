package com.example.android.inventoryapp;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.android.inventoryapp.db.ProductContract.ProductEntry;

import static com.example.android.inventoryapp.MainActivity.productUri;

/**
 * Created by mdd23 on 6/8/2017.
 */

public class AddProductDialog extends AlertDialog {
    private static final String TAG = AddProductDialog.class.getSimpleName();

    private String currencySymbol;
    private LinearLayout addProductLayout;
    private EditText productNameEditText;
    private EditText priceEditText;
    private EditText qtyEditText;
    private EditText supplierNameEditText;
    private EditText supplierEmailEditText;
    private EditText imageUrlEditText;

    public AddProductDialog(Context context) {
        super(context);

        // Workarounds to get EditTexts in an AlertDialog
        // Source: http://stackoverflow.com/a/18176909/5302182
        //TODO: try http://stackoverflow.com/questions/2335813/how-to-inflate-one-view-with-a-layout
        //TODO: this might be useful http://stackoverflow.com/questions/5447092/get-context-inside-onclickdialoginterface-v-int-buttonid

        productNameEditText = new EditText(this.getContext());
        priceEditText = new EditText(this.getContext());
        qtyEditText = new EditText(this.getContext());
        supplierNameEditText = new EditText(this.getContext());
        supplierEmailEditText = new EditText(this.getContext());
        imageUrlEditText = new EditText(this.getContext());

        productNameEditText.setHint(R.string.product_name);
        priceEditText.setHint(R.string.price);
        qtyEditText.setHint(R.string.quantity);
        supplierNameEditText.setHint(R.string.supplier_name);
        supplierEmailEditText.setHint(R.string.supplier_email);
        imageUrlEditText.setHint(R.string.image_url);

        priceEditText.setRawInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        qtyEditText.setRawInputType(InputType.TYPE_CLASS_NUMBER);
        supplierEmailEditText.setRawInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        imageUrlEditText.setRawInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_URI);

        addProductLayout = new LinearLayout(this.getContext());
        addProductLayout.setOrientation(LinearLayout.VERTICAL);
        addProductLayout.addView(productNameEditText);
        addProductLayout.addView(priceEditText);
        addProductLayout.addView(qtyEditText);
        addProductLayout.addView(supplierNameEditText);
        addProductLayout.addView(supplierEmailEditText);
        addProductLayout.addView(imageUrlEditText);
    }

    private AlertDialog build() {
        return new Builder(this.getContext())
                .setTitle(this.getContext().getString(R.string.add_a_new_product))
                .setView(addProductLayout)
                .setPositiveButton(this.getContext().getString(R.string.add), new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Do nothing here because we overwrite later,
                        // but this needs to be here to handle old versions of Android
                        // SOURCE: http://stackoverflow.com/a/15619098/5302182
                    }
                })
                .setNegativeButton(this.getContext().getString(R.string.cancel), null)
                .create();
    }

    public void showAddProductDialog(final String currencySymbol) {
        Log.d(TAG, "showAddProductDialog: Run method");

        final AlertDialog dialog = this.build();
        dialog.show();
        new AddProductDialog(this.getContext()).build();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String productName = productNameEditText.getText().toString();
                String price = priceEditText.getText().toString();
                String qty = qtyEditText.getText().toString();
                String supplierName = supplierNameEditText.getText().toString();
                String supplierEmail = supplierEmailEditText.getText().toString();
                String imageUrl = imageUrlEditText.getText().toString();

                // Remove currency symbols and commas from price
                price = price.replaceAll("[" + currencySymbol + "]", "");
                price = price.replaceAll("[,]", "");

                // Product Validation
                ProductValidation.ProductValidationMessage productValidation =
                        ProductValidation.validateAddProduct(getContext(), productName, price, qty, supplierName, supplierEmail, imageUrl);

                // Pass/fail
                if (productValidation.isValid()) {
                    ContentValues values = new ContentValues();
                    values.put(ProductEntry.COLUMN_PRODUCT, productName);
                    values.put(ProductEntry.COLUMN_PRICE, price);
                    values.put(ProductEntry.COLUMN_QTY, qty);
                    values.put(ProductEntry.COLUMN_SUPPLIER_NAME, supplierName);
                    values.put(ProductEntry.COLUMN_SUPPLIER_EMAIL, supplierEmail);
                    values.put(ProductEntry.COLUMN_IMAGE_URL, imageUrl);

                    getContext().getContentResolver().insert(productUri, values);
                    Toast.makeText(
                            getContext()
                            , "Successfully added " + "'" + productName + "'!"
                            , Toast.LENGTH_LONG).show();
                    dialog.dismiss();
                } else {
                    Toast.makeText(getContext(), productValidation.getToastMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}
