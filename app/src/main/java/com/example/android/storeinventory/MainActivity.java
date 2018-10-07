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

    private ProductCursorAdapter productCursorAdapter;
    private static final int PRODUCT_LOADER = 1;
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FloatingActionButton addItem = (FloatingActionButton) findViewById(R.id.add_new_product);
        addItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ProductEditorActivity.class);
                startActivity(intent);
            }
        });

        listView = (ListView) findViewById(R.id.inventory_list);
        View emptyView = findViewById(R.id.empty_view);
        listView.setEmptyView(emptyView);

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

    /**
     * Refreshes/Reloads ListVIew: Used to make sale button visible if
     * quantity changed to above zero quantity.
     */
    @Override
    protected void onResume() {
        super.onResume();
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
                deleteDatabase();
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

        Uri newDbRow = getContentResolver().insert(ProductEntry.CONTENT_URI, vals);
        if (ContentUris.parseId(newDbRow) == -1) {
            Toast.makeText(this, "Error with adding dummy data", Toast.LENGTH_SHORT ).show();
        } else {
            Toast.makeText(this, "Fake data added", Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteDatabase() {
        int rowsDeleted = getContentResolver().delete(ProductEntry.CONTENT_URI, null, null);
        if (rowsDeleted != 0) {
            Toast.makeText(this, "All products deleted", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Error deleting database", Toast.LENGTH_SHORT).show();
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
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        productCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        productCursorAdapter.swapCursor(null);
    }
}

