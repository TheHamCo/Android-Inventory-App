package com.example.android.inventoryapp.db;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;

/**
 * Created by mdd23 on 6/29/2016.
 */
public class ProductProvider extends ContentProvider{

    public static final UriMatcher sUriMatcher = buildUriMatcher();
    private ProductDbHelper mOpenHelper;

    static final int PRODUCT = 101;

    static UriMatcher buildUriMatcher(){
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = ProductContract.CONTENT_AUTHORITY;

        // Match URI to code
        matcher.addURI(authority, ProductContract.PATH_PRODUCT, PRODUCT);

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
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }


    @Override
    public Uri insert(Uri uri, ContentValues values) {
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }
}
