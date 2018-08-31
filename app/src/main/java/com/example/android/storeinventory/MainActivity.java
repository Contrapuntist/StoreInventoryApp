package com.example.android.storeinventory;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.storeinventory.data.InventoryContract.ProductEntry;
import com.example.android.storeinventory.data.InventoryDbHelper;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {

    private InventoryDbHelper inventoryDbHelper;
    private SQLiteDatabase db;
    private TextView logDbView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        inventoryDbHelper = new InventoryDbHelper(this);
        logDbView = (TextView) findViewById(R.id.db_log);

        // log db in terminal and in app.
        logCompleteDb();

        Button addRow = (Button) findViewById(R.id.add_row);
        addRow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertProduct();

                // Show message confirming a new row added.
                Toast toast = Toast.makeText(getApplicationContext(), "New row added", Toast.LENGTH_LONG);
                toast.show();

                // clear/refresh TextView displaying logs
                logDbView.setText(null);

                // Reload db log
                logCompleteDb();
            }
        });
    }

    private void insertProduct() {
        db = inventoryDbHelper.getWritableDatabase();

        ContentValues vals = new ContentValues();
        vals.put(ProductEntry.PRODUCT_NAME, "Funky Phone");
        vals.put(ProductEntry.PRODUCT_DESCRIPTION, "Phone shaped like a banana");
        vals.put(ProductEntry.PRODUCT_PRICE, 99.99);
        vals.put(ProductEntry.PRODUCT_QUANTITY, 100);
        vals.put(ProductEntry.SUPPLIER_NAME, "Fruity, Inc.");
        vals.put(ProductEntry.SUPPLIER_PHONE, "223-325-6655");

        long newRow = db.insert(ProductEntry.PRODUCTS_TABLE, null, vals);

        Log.i("NEW ROW INSERTED", "New Row: " + newRow);
    }

    /**
     * Method specific for this project to check db is adding rows
     * and then log them within the console.
     *
     * Will be removed/updated for project 9 when full UI to display db.
     *
     */
    private void logCompleteDb() {
        db = inventoryDbHelper.getReadableDatabase();

        String[] tableAllProjection = {
                ProductEntry._ID,
                ProductEntry.PRODUCT_NAME,
                ProductEntry.PRODUCT_DESCRIPTION,
                ProductEntry.PRODUCT_PRICE,
                ProductEntry.PRODUCT_QUANTITY,
                ProductEntry.SUPPLIER_NAME,
                ProductEntry.SUPPLIER_PHONE
        };

        Cursor cursor = db.query(
                ProductEntry.PRODUCTS_TABLE,
                tableAllProjection,
                null,
                null,
                null,
                null,
                null
        );

        try {
            // Log total rows in table
            Log.i("CURSOR COUNT", "Total product entries: " + cursor.getCount());

            // capture column indexes
            int idColumnIdx = cursor.getColumnIndex(ProductEntry._ID);
            int nameColumnIdx = cursor.getColumnIndex(ProductEntry.PRODUCT_NAME);
            int descriptionColumnIdx = cursor.getColumnIndex(ProductEntry.PRODUCT_DESCRIPTION);
            int priceColumnIdx = cursor.getColumnIndex(ProductEntry.PRODUCT_PRICE);
            int quantityColumnIdx = cursor.getColumnIndex(ProductEntry.PRODUCT_QUANTITY);
            int supplierNameColumnIdx = cursor.getColumnIndex(ProductEntry.SUPPLIER_NAME);
            int supplierPhoneColumnIdx = cursor.getColumnIndex(ProductEntry.SUPPLIER_PHONE);

            // Loop and retrieve each row
            while (cursor.moveToNext()) {
                int productId = cursor.getInt(idColumnIdx);
                String productName = cursor.getString(nameColumnIdx);
                String productDescription = cursor.getString(descriptionColumnIdx);
                double productPrice = cursor.getDouble(priceColumnIdx);
                int productQuantity = cursor.getInt(quantityColumnIdx);
                String productSupplierName = cursor.getString(supplierNameColumnIdx);
                String productSupplierPhone = cursor.getString(supplierPhoneColumnIdx);


                String rowDetails = "*********************************\n"
                        + "Product ID: " + productId + "\n"
                        + "Product Name: " + productName + "\n"
                        + "Product Description: " + productDescription + "\n"
                        + "Product Price: " + Double.toString(productPrice) + "\n"
                        + "Product Quantity: " + Integer.toString(productQuantity) + "\n"
                        + "Product Supplier Name: " + productSupplierName + "\n"
                        + "Product Supplier Phone: " + productSupplierPhone + "\n\n";


                Log.i("Product Row Log", rowDetails);

                logDbView.append(rowDetails);
            }

        } catch (SQLException ex) {
            Log.e("SQL ERROR", "Error retreiving data from SQL db", ex);
        } finally {
            cursor.close();
        }
    }
}

