package com.example.android.inventoryapp.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.android.inventoryapp.db.ProductContract.ProductEntry;

/**
 * Manages a local database for the product data
 */
public class ProductDbHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "products.db";
    // Save the context to use with deleteDatabase()
    Context context;

    public ProductDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        // Save the context to use with deleteDatabase()
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_HABITS_TABLE = "CREATE TABLE " + ProductEntry.TABLE_NAME + "("
                + ProductEntry._ID + " INTEGER PRIMARY KEY, "
                + ProductEntry.COLUMN_PRODUCT + " TEXT UNIQUE NOT NULL, "
                // Streaks start at 0 days
                + ProductEntry.COLUMN_PRICE + " REAL NOT NULL , "
                + ProductEntry.COLUMN_QTY + " INTEGER DEFAULT 0 , "
                + ProductEntry.COLUMN_SUPPLIER_NAME + " TEXT NOT NULL, "
                + ProductEntry.COLUMN_SUPPLIER_EMAIL + " TEXT NOT NULL "
                + ");";
        db.execSQL(SQL_CREATE_HABITS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
