package com.example.android.storeinventory.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.text.TextUtils;

import com.example.android.storeinventory.data.InventoryContract.ProductEntry;
import com.example.android.storeinventory.data.InventoryDbHelper;

import org.w3c.dom.Text;

public class InventoryProvider extends ContentProvider {

    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    private static final int PRODUCTS = 100;
    private static final int PRODUCTS_ID = 101;

    static {
        uriMatcher.addURI(InventoryContract.CONTENT_AUTHORITY, InventoryContract.PATH_PRODUCTS, PRODUCTS);
        uriMatcher.addURI(InventoryContract.CONTENT_AUTHORITY, InventoryContract.PATH_PRODUCTS + "/#", PRODUCTS_ID);
    }

    private InventoryDbHelper inventoryDbHelper;

    @Override
    public boolean onCreate() {
        inventoryDbHelper = new InventoryDbHelper(getContext());
        return true;
    }


    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        SQLiteDatabase db = inventoryDbHelper.getReadableDatabase();
        Cursor cursor = null;

        int match = uriMatcher.match(uri);
        switch (match) {
            case PRODUCTS:
                cursor = db.query(InventoryContract.ProductEntry.PRODUCTS_TABLE, projection,
                        selection, selectionArgs, null, null, sortOrder);
                break;
            case PRODUCTS_ID:
                selection = InventoryContract.ProductEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                cursor = db.query(InventoryContract.ProductEntry.PRODUCTS_TABLE, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unkown URI " + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final int match = uriMatcher.match(uri);
        switch(match) {
            case PRODUCTS:
                return addProduct(uri, contentValues);
            default:
                throw new IllegalArgumentException("Adding a product is not supported for " + uri );
        }
    }

    private Uri addProduct(Uri uri, ContentValues vals) {

        if (vals.containsKey(ProductEntry.PRODUCT_NAME)) {
            String hasName = vals.getAsString(ProductEntry.PRODUCT_NAME);
            if (hasName == null) {
                throw new IllegalArgumentException("Name is required");
            }
        }

        if (vals.containsKey(ProductEntry.PRODUCT_PRICE)) {
            double hasPrice = vals.getAsDouble(ProductEntry.PRODUCT_PRICE);
            if ( hasPrice < 0) {
                throw new IllegalArgumentException("We're trying to make money, not give money. Price should have positive values");
            }
        }

        if (vals.containsKey(ProductEntry.PRODUCT_QUANTITY)) {
            int hasQuantity = vals.getAsInteger(ProductEntry.PRODUCT_QUANTITY);
            if ( hasQuantity < 0 ) {
                throw new IllegalArgumentException("Quantity should not be a negative number");
            }
        }

        if (vals.containsKey(ProductEntry.SUPPLIER_NAME)) {
            String hasSupplierName = vals.getAsString(ProductEntry.SUPPLIER_NAME);
            if (TextUtils.isEmpty(hasSupplierName)) {
                throw new IllegalArgumentException("Supplier name is required");
            }
        }

        if (vals.containsKey(ProductEntry.SUPPLIER_PHONE)) {
            String hasSupplierName = vals.getAsString(ProductEntry.SUPPLIER_PHONE);
            if (TextUtils.isEmpty(hasSupplierName)) {
                throw new IllegalArgumentException("Supplier phone number is required");
            }
        }

        if (vals.size() == 0) {
            return null;
        }

        SQLiteDatabase db = inventoryDbHelper.getWritableDatabase();
        Long id = db.insert(ProductEntry.PRODUCTS_TABLE, null, vals);

        if (id == -1) {
            return null;
        }

        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int update(Uri uri, ContentValues contentValues,
                      String selection, String[] selectionArgs) {
        final int match = uriMatcher.match(uri);
        switch (match) {
            case PRODUCTS:
                return updateProduct(uri, contentValues, selection, selectionArgs);
            case PRODUCTS_ID:
                // For the PET_ID code, extract out the ID from the URI,
                // so we know which row to update. Selection will be "_id=?" and selection
                // arguments will be a String array containing the actual ID.
                selection = InventoryContract.ProductEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                return updateProduct(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }

    }

    private int updateProduct(Uri uri, ContentValues vals, String selection, String[] selectionArgs) {

        if (vals.containsKey(ProductEntry.PRODUCT_NAME)) {
            String hasName = vals.getAsString(ProductEntry.PRODUCT_NAME);
            if (hasName == null) {
                throw new IllegalArgumentException("Name is required");
            }
        }

        if (vals.containsKey(ProductEntry.PRODUCT_PRICE)) {
            double hasPrice = vals.getAsDouble(ProductEntry.PRODUCT_PRICE);
            if ( hasPrice < 0) {
                throw new IllegalArgumentException("We're trying to make money, not give money. Price should have positive values");
            }
        }

        if (vals.containsKey(ProductEntry.PRODUCT_QUANTITY)) {
            int hasQuantity = vals.getAsInteger(ProductEntry.PRODUCT_QUANTITY);
            if ( hasQuantity < 0 ) {
                throw new IllegalArgumentException("Quantity should not be a negative number");
            }
        }

        if (vals.containsKey(ProductEntry.SUPPLIER_NAME)) {
            String hasSupplierName = vals.getAsString(ProductEntry.SUPPLIER_NAME);
            if (TextUtils.isEmpty(hasSupplierName)) {
                throw new IllegalArgumentException("Supplier name is required");
            }
        }

        if (vals.containsKey(ProductEntry.SUPPLIER_PHONE)) {
            String hasSupplierName = vals.getAsString(ProductEntry.SUPPLIER_PHONE);
            if (TextUtils.isEmpty(hasSupplierName)) {
                throw new IllegalArgumentException("Supplier phone number is required");
            }
        }


        if (vals.size() == 0) {
            return 0;
        }

        SQLiteDatabase db = inventoryDbHelper.getWritableDatabase();

        int rowsUpdated = db.update(InventoryContract.ProductEntry.PRODUCTS_TABLE, vals, selection, selectionArgs);

        // if 1 or more rows updated, then update listeners.
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(InventoryContract.ProductEntry.CONTENT_URI, null);
        }

        return rowsUpdated;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = inventoryDbHelper.getWritableDatabase();
        int productsRemoved;
        final int match = uriMatcher.match(uri);

        switch (match) {
            case PRODUCTS:
                productsRemoved = db.delete(ProductEntry.PRODUCTS_TABLE, selection, selectionArgs);
                break;
            case PRODUCTS_ID:
                selection = ProductEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                productsRemoved = db.delete(ProductEntry.PRODUCTS_TABLE, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Delete is not allowed for uri " + uri);
        }

        return productsRemoved;
    }

    @Override
    public String getType(Uri uri) {
        final int match = uriMatcher.match(uri);
        switch (match) {
            case PRODUCTS:
                return InventoryContract.ProductEntry.CONTENT_LIST_TYPE;
            case PRODUCTS_ID:
                return InventoryContract.ProductEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }

    }
}
