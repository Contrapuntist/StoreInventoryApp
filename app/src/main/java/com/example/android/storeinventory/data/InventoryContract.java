package com.example.android.storeinventory.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public final class InventoryContract {

    public static final String CONTENT_AUTHORITY = "com.example.android.storeinventory";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_PRODUCTS = "products";

    private InventoryContract() {}

    public static final class ProductEntry implements BaseColumns {

        // content uri
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_PRODUCTS);

        // table name
        public final static String PRODUCTS_TABLE = "products";

        // Table columns
        public final static String _ID = BaseColumns._ID;
        public final static String PRODUCT_NAME = "name";
        public final static String PRODUCT_DESCRIPTION = "description";
        public final static String PRODUCT_PRICE = "price";
        public final static String PRODUCT_QUANTITY = "quantity";
        public final static String SUPPLIER_NAME = "supplier_name";
        public final static String SUPPLIER_PHONE = "supplier_phone";

        // Mime types
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PRODUCTS;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PRODUCTS;
    }
}
