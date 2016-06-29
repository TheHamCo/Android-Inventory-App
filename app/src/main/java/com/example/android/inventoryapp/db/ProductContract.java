package com.example.android.inventoryapp.db;

import android.provider.BaseColumns;

/**
 * Defines table and column names for the product database
 */
public class ProductContract {

    public static final class ProductEntry implements BaseColumns {

        public static final String TABLE_NAME = "product";

        // Name of the product
        public static final String COLUMN_PRODUCT = "product";

        // Price of the product (xx.xx) where x are numbers
        public static final String COLUMN_PRICE = "price";

        // Quantity of the product (int)
        public static final String COLUMN_QTY = "quantity";

        // Name of the supplier of the product
        public static final String COLUMN_SUPPLIER_NAME = "supplier_name";

        // Email address of the supplier
        public static final String COLUMN_SUPPLIER_EMAIL = "supplier_email";
    }
}
