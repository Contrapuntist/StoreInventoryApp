package com.example.android.storeinventory.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.android.storeinventory.data.InventoryContract.ProductEntry;

public class InventoryDbHelper extends SQLiteOpenHelper {

    /**
     * Values for class constructor
     */
    private static final String TABLE_NAME = "inventory.db";
    private static int DATABASE_VERSION = 1;

    public InventoryDbHelper(Context context) {
        super(context, TABLE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        /*
         table name products
         ID required primary key autoincrement
         Product Name (Text) required
         Product Description (Text) required
         Price (Double) required default 0.00
         Quantity (integer) default 0
         Supplier Name Text required
         Supplier phone number text required
        */
        String SQL_CREATE_INVENTORY_TABLE = "CREATE TABLE " + ProductEntry.PRODUCTS_TABLE + " ("
                + ProductEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + ProductEntry.PRODUCT_NAME + " TEXT NOT NULL, "
                + ProductEntry.PRODUCT_DESCRIPTION + " TEXT, "
                + ProductEntry.PRODUCT_PRICE + " DECIMAL(10,2) NOT NULL DEFAULT 0, "
                + ProductEntry.PRODUCT_QUANTITY + " INTEGER NOT NULL DEFAULT 0, "
                + ProductEntry.SUPPLIER_NAME + " TEXT NOT NULL, "
                + ProductEntry.SUPPLIER_PHONE + " TEXT)";

        db.execSQL(SQL_CREATE_INVENTORY_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
