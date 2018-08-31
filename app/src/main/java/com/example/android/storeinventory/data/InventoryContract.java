package com.example.android.storeinventory.data;

import android.provider.BaseColumns;

public final class InventoryContract{

    private InventoryContract() {}

    public static final class ProductEntry implements BaseColumns {

    /*
        table name products
        ID required autoincrement
        Product Name (Text) required
        Product Description (Text) required
        Price (Double) required default 0.00
        Quantity (integer) default 0
        Supplier Name Text required
        Supplier phone number text required
    */

        public final static String PRODUCTS_TABLE = "products";
        public final static String _ID = BaseColumns._ID;
        public final static String PRODUCT_NAME = "name";
        public final static String PRODUCT_DESCRIPTION = "description";
        public final static String PRODUCT_PRICE = "price";
        public final static String  PRODUCT_QUANTITY = "quantity";
        public final static String SUPPLIER_NAME = "supplier_name";
        public final static String SUPPLIER_PHONE = "supplier_phone";

    }



}
