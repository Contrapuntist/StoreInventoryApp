package com.example.android.storeinventory.cursor;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.android.storeinventory.R;
import com.example.android.storeinventory.data.InventoryContract;

import org.w3c.dom.Text;

public class ProductCursorAdapter extends CursorAdapter {

    public ProductCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.product_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView nameView = (TextView) view.findViewById(R.id.product_name);
        TextView priceView = (TextView) view.findViewById(R.id.product_price);
        TextView quantityView = (TextView) view.findViewById(R.id.product_quantity);

        int nameColIdx = cursor.getColumnIndex(InventoryContract.ProductEntry.PRODUCT_NAME);
        int priceColIdx = cursor.getColumnIndex(InventoryContract.ProductEntry.PRODUCT_PRICE);
        int quantityColIdx = cursor.getColumnIndex(InventoryContract.ProductEntry.PRODUCT_QUANTITY);

        String productName = cursor.getString(nameColIdx);
        double productPrice = cursor.getDouble(priceColIdx);
        int productQuantity = cursor.getInt(quantityColIdx);

        nameView.setText(productName);
        priceView.setText(Double.toString(productPrice));
        quantityView.setText(Integer.toString(productQuantity));
    }
}
