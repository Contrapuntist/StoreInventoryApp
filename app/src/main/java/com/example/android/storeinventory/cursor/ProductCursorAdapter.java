package com.example.android.storeinventory.cursor;

import android.app.Activity;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.storeinventory.MainActivity;
import com.example.android.storeinventory.R;
import com.example.android.storeinventory.data.InventoryContract;
import com.example.android.storeinventory.data.InventoryContract.ProductEntry;

import org.w3c.dom.Text;

public class ProductCursorAdapter extends CursorAdapter {

    private ListView listView;

    public ProductCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.product_item, parent, false);
    }

    @Override
    public void bindView(final View view, final Context context, final Cursor cursor) {
        TextView nameView = (TextView) view.findViewById(R.id.product_name);
        TextView priceView = (TextView) view.findViewById(R.id.product_price);
        TextView quantityView = (TextView) view.findViewById(R.id.product_quantity);

        int nameColIdx = cursor.getColumnIndex(InventoryContract.ProductEntry.PRODUCT_NAME);
        int priceColIdx = cursor.getColumnIndex(InventoryContract.ProductEntry.PRODUCT_PRICE);
        int quantityColIdx = cursor.getColumnIndex(InventoryContract.ProductEntry.PRODUCT_QUANTITY);

        final String productName = cursor.getString(nameColIdx);
        double productPrice = cursor.getDouble(priceColIdx);
        final int productQuantity = cursor.getInt(quantityColIdx);

        nameView.setText(productName);
        priceView.setText(Double.toString(productPrice));
        quantityView.setText(Integer.toString(productQuantity));

        // sale button setup
        ImageView saleBtn = (ImageView) view.findViewById(R.id.sale_btn);

//        if ( productQuantity == 0 ) {
//            saleBtn.setVisibility(View.INVISIBLE);
//        }

        saleBtn.setClickable(true);
        saleBtn.setOnClickListener(new View.OnClickListener() {

            int rowId = cursor.getInt(cursor.getColumnIndex(ProductEntry._ID));
            Uri contentUri = Uri.withAppendedPath(ProductEntry.CONTENT_URI, Integer.toString(rowId));

            @Override
            public void onClick(View v) {

                int newQuantity = 0;
                if (productQuantity > 0) {
                   newQuantity = productQuantity - 1;
                }

                ContentValues vals = new ContentValues();
                vals.put(ProductEntry.PRODUCT_QUANTITY, newQuantity);
                int rowUpdate = context.getContentResolver().update(contentUri, vals, null, null);
                notifyDataSetChanged();
            }
        });

    }
}

