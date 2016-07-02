package com.example.android.inventoryapp.db;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Defines table and column names for the product database
 */
public class ProductContract {

    public static final String CONTENT_AUTHORITY = "com.example.android.inventoryapp.app";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_PRODUCT = "product";

    public static final class ProductEntry implements BaseColumns {

        // content://com.example.android.inventoryapp.app/product
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_PRODUCT).build();

        //MIME types
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PRODUCT;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PRODUCT;

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

        // URL of image
        public static final String COLUMN_IMAGE_URL = "image_url";

        /**
         * Builds URI with ID routing
         * @param id
         * @return content://com.example.android.inventoryapp.app/product/#
         */
        public static Uri buildLocationuri(long id){
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        /**
         * Get ID from URI with ID routing
         * e.g. content://com.example.android.inventoryapp.app/product/#
         * @param uri
         * @return id
         */
        public static long getIdFromUri(Uri uri){
            return Long.parseLong(uri.getPathSegments().get(1));
        }
    }
}
