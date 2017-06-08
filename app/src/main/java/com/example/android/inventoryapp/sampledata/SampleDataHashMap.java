package com.example.android.inventoryapp.sampledata;

import com.example.android.inventoryapp.db.ProductContract;

import java.util.HashMap;

/**
 * Created by mdd23 on 6/8/2017.
 */

public class SampleDataHashMap extends HashMap<String, String> {

    public SampleDataHashMap addProductName(String productName) {
        this.put(ProductContract.ProductEntry.COLUMN_PRODUCT, productName);
        return this;
    }

    public SampleDataHashMap addPrice(double price) {
        this.put(ProductContract.ProductEntry.COLUMN_PRICE, String.valueOf(price));
        return this;
    }

    public SampleDataHashMap addQuantity(int qty) {
        this.put(ProductContract.ProductEntry.COLUMN_QTY, String.valueOf(qty));
        return this;
    }

    public SampleDataHashMap addSupplierName(String supplierName) {
        this.put(ProductContract.ProductEntry.COLUMN_SUPPLIER_NAME, supplierName);
        return this;
    }

    public SampleDataHashMap addSupplierEmail(String supplierEmail) {
        this.put(ProductContract.ProductEntry.COLUMN_SUPPLIER_EMAIL, supplierEmail);
        return this;
    }

    public SampleDataHashMap addImageUrl(String imageUrl) {
        this.put(ProductContract.ProductEntry.COLUMN_IMAGE_URL, imageUrl);
        return this;
    }
}
