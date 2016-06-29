package com.example.android.inventoryapp.db;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

/**
 * Provider for Product database
 */
public class ProductProvider extends ContentProvider{

    public static final UriMatcher sUriMatcher = buildUriMatcher();
    private ProductDbHelper mOpenHelper;

    static final int PRODUCT = 101;
    static final int PRODUCT_WITH_ID = 102;

    // _id=?
    public static final String sProductIdSelection = ProductContract.ProductEntry._ID + "=?";

    static UriMatcher buildUriMatcher(){
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = ProductContract.CONTENT_AUTHORITY;

        // Match URI to code
        matcher.addURI(authority, ProductContract.PATH_PRODUCT, PRODUCT);
        matcher.addURI(authority, ProductContract.PATH_PRODUCT + "/#", PRODUCT_WITH_ID);

        return matcher;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new ProductDbHelper(getContext());
        return true;
    }

    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);

        switch (match){
            case PRODUCT:
                return ProductContract.ProductEntry.CONTENT_TYPE;
            case PRODUCT_WITH_ID:
                return ProductContract.ProductEntry.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor retCursor;
        switch (sUriMatcher.match(uri)){
            case PRODUCT:
                retCursor = mOpenHelper.getReadableDatabase().query(
                        ProductContract.ProductEntry.TABLE_NAME
                        ,projection
                        ,selection
                        ,selectionArgs
                        ,null
                        ,null
                        ,sortOrder
                );
                break;
            case PRODUCT_WITH_ID:
                long _id = ProductContract.ProductEntry.getIdFromUri(uri);

                retCursor = mOpenHelper.getReadableDatabase().query(
                         ProductContract.ProductEntry.TABLE_NAME
                        ,projection
                        ,sProductIdSelection
                        ,new String[]{ Long.toString(_id) }
                        ,null
                        ,null
                        ,sortOrder
                );
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }


    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        Uri returnUri;

        switch(sUriMatcher.match(uri)) {
            case PRODUCT:
                long _id = db.insert(ProductContract.ProductEntry.TABLE_NAME, null, values);
                if (_id>0){
                    returnUri = ProductContract.ProductEntry.buildLocationuri(_id);
                } else {
                    throw new SQLException("Failed to insert row into " + uri);
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;
        // Delete all rows if no selection
        if (null == selection) selection = "1";
        switch (match){
            case PRODUCT:
                rowsDeleted = db.delete(
                        ProductContract.ProductEntry.TABLE_NAME
                        ,selection
                        ,selectionArgs
                );
                break;
            case PRODUCT_WITH_ID:
                long _id = ProductContract.ProductEntry.getIdFromUri(uri);
                rowsDeleted = db.delete(
                        ProductContract.ProductEntry.TABLE_NAME
                        ,sProductIdSelection
                        ,new String[] {Long.toString(_id)}
                );
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsDeleted!=0){
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;

        switch (match){
            case PRODUCT:
                rowsUpdated = db.update(
                        ProductContract.ProductEntry.TABLE_NAME
                        ,values
                        ,selection
                        ,selectionArgs
                );
                break;
            case PRODUCT_WITH_ID:
                long _id = ProductContract.ProductEntry.getIdFromUri(uri);
                rowsUpdated = db.update(
                        ProductContract.ProductEntry.TABLE_NAME
                        ,values
                        ,sProductIdSelection
                        ,new String[] { Long.toString(_id) }
                );
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsUpdated != 0){
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }
}
