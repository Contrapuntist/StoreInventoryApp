package com.example.android.storeinventory;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.storeinventory.cursor.ProductCursorAdapter;
import com.example.android.storeinventory.data.InventoryContract.ProductEntry;
import com.example.android.storeinventory.data.InventoryDbHelper;

import org.w3c.dom.Text;

import java.net.URI;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private InventoryDbHelper inventoryDbHelper;
    private SQLiteDatabase db;
    private TextView logDbView;
    private ProductCursorAdapter productCursorAdapter;
    private static final int PRODUCT_LOADER = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        inventoryDbHelper = new InventoryDbHelper(this);

        FloatingActionButton addItem = (FloatingActionButton) findViewById(R.id.add_new_product);
        addItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ProductEditorActivity.class);
                startActivity(intent);
            }
        });

        ListView listView = (ListView) findViewById(R.id.inventory_list);


        // TODO: add emptyview

        productCursorAdapter = new ProductCursorAdapter(this, null);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getBaseContext(), ProductEditorActivity.class);
                Uri currentProductUri = ContentUris.withAppendedId(ProductEntry.CONTENT_URI, id);
                intent.setData(currentProductUri);
                startActivity(intent);
            }
        });

        listView.setAdapter(productCursorAdapter);
        getLoaderManager().initLoader(PRODUCT_LOADER, null, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.insert_dummy_data:
                Toast.makeText(this, "in added dummy row", Toast.LENGTH_SHORT).show();
                insertDummyProduct();
                return true;
            case R.id.delete_database:

//              TODO: SETUP delete database call

                Log.i("DELETE MENU BUTTON", "DELETE MENU BUTTON CLICKED");
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void insertDummyProduct() {

        ContentValues vals = new ContentValues();
        vals.put(ProductEntry.PRODUCT_NAME, "Funky Phone");
        vals.put(ProductEntry.PRODUCT_DESCRIPTION, "Phone shaped like a banana");
        vals.put(ProductEntry.PRODUCT_PRICE, 99.99);
        vals.put(ProductEntry.PRODUCT_QUANTITY, 100);
        vals.put(ProductEntry.SUPPLIER_NAME, "Fruity, Inc.");
        vals.put(ProductEntry.SUPPLIER_PHONE, "223-325-6655");

        //long newRow = db.insert(ProductEntry.PRODUCTS_TABLE, null, vals);

        Uri newDbRow = getContentResolver().insert(ProductEntry.CONTENT_URI, vals);
        if (ContentUris.parseId(newDbRow) == -1) {
            Toast.makeText(this, "Error with adding dummy data", Toast.LENGTH_SHORT ).show();
        } else {
            Toast.makeText(this, "Fake data added", Toast.LENGTH_SHORT).show();
        }
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

                // log in terminal
                Log.i("Product Row Log", rowDetails);

                // log in activity_main
                //logDbView.append(rowDetails);
            }

        } catch (SQLException ex) {
            Log.e("SQL ERROR", "Error retreiving data from SQL db", ex);
        } finally {
            cursor.close();
        }
    }


    /**
     * CursorLoader required methods implemented. Passes results to create list
     * with CursorLoader Adapter
     */

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        String[] projection = {
                ProductEntry._ID,
                ProductEntry.PRODUCT_NAME,
                ProductEntry.PRODUCT_PRICE,
                ProductEntry.PRODUCT_QUANTITY};

        return new CursorLoader(this,
                                ProductEntry.CONTENT_URI,
                                projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) { productCursorAdapter.swapCursor(data); }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        productCursorAdapter.swapCursor(null);
    }
}

