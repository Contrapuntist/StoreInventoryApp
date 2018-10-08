package com.example.android.storeinventory;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.telephony.PhoneNumberUtils;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.android.storeinventory.data.InventoryContract;
import com.example.android.storeinventory.data.InventoryContract.ProductEntry;
import com.example.android.storeinventory.data.InventoryDbHelper;
import com.example.android.storeinventory.data.InventoryProvider;

import org.w3c.dom.Text;

import java.security.ProtectionDomain;

public class ProductEditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    /**
     * Variables for capturing inputs about product to
     * either update or save new product details in db
     */
    private EditText productNameEditText;
    private EditText productDescriptionEditText;
    private EditText productPriceEditText;
    private EditText productQuantityEditText;
    private EditText productSupplierNameEditText;
    private EditText productSupplierPhoneEditText;
    private Button callSupplierButton;

    // setup global variables for database updates and
    private Uri productDataUri;
    Boolean isEditProduct;
    private static final int EDIT_PRODUCT_LOADER = 2;
    private InventoryDbHelper inventoryDbHelper;
    private boolean productHasChanged = false;

    /**
     * Listener for checking if any editable fields changes and changes
     * isEditProduct to true, which is used for a dialog.
     */
    private View.OnTouchListener touchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            productHasChanged = true;
            return false;
        }
    };

    @Override
    public void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.product_editor);

        Intent intent = getIntent();
        productDataUri = intent.getData();
        callSupplierButton = findViewById(R.id.restock_call_btn);

        if (productDataUri == null) {
            getSupportActionBar().setTitle(R.string.add_product_title);
            isEditProduct = false;
            invalidateOptionsMenu();
            callSupplierButton.setVisibility(View.INVISIBLE);

       } else {
            getSupportActionBar().setTitle(R.string.edit_product_details);
            isEditProduct = true;
        }

        productNameEditText = findViewById(R.id.product_name_input);
        productDescriptionEditText = findViewById(R.id.product_description_input);
        productPriceEditText = findViewById(R.id.product_price_input);
        productQuantityEditText = findViewById(R.id.product_quantity_input);
        productSupplierNameEditText = findViewById(R.id.product_supplier_name_input);
        productSupplierPhoneEditText = findViewById(R.id.product_supplier_phone_input);

        productNameEditText.setOnTouchListener(touchListener);
        productDescriptionEditText.setOnTouchListener(touchListener);
        productPriceEditText.setOnTouchListener(touchListener);
        productQuantityEditText.setOnTouchListener(touchListener);
        productSupplierNameEditText.setOnTouchListener(touchListener);
        productSupplierPhoneEditText.setOnTouchListener(touchListener);

        getLoaderManager().initLoader(EDIT_PRODUCT_LOADER, null, this);
    }

    /**
     * Save product method to SQL db
     */

    private void saveProductDetails() {

        /* retrieve text from all EditText (form) views */
        String productName = productNameEditText.getText().toString().trim();
        String productDescription = productDescriptionEditText.getText().toString().trim();
        String priceText = productPriceEditText.getText().toString().trim();
        double productPrice = 0.00;  /* set default value for price */
        String quantityText = productQuantityEditText.getText().toString().trim();
        Integer productQuantity = 0; /* set default value for quantity */
        String productSupplierName = productSupplierNameEditText.getText().toString().trim();
        String productSupplierPhone = productSupplierPhoneEditText.getText().toString().trim();

        if (TextUtils.isEmpty(productName)) {
            Toast.makeText(this, R.string.product_name_toast, Toast.LENGTH_LONG).show();
            return;
        } else if (TextUtils.isEmpty(priceText)) {
            Toast.makeText(this, R.string.product_price_toast, Toast.LENGTH_LONG).show();
            return;
        } else if (TextUtils.isEmpty(quantityText)) {
            Toast.makeText(this, R.string.product_quantity_toast, Toast.LENGTH_LONG).show();
            return;
        } else if (TextUtils.isEmpty(productSupplierName)) {
            Toast.makeText(this, R.string.supplier_info_toast, Toast.LENGTH_LONG).show();
            return;
        } else if (TextUtils.isEmpty(productSupplierPhone)) {
            Toast.makeText(this, R.string.supplier_info_toast, Toast.LENGTH_LONG).show();
            return;
        }

        productPrice = Double.parseDouble(priceText);
        productQuantity = Integer.parseInt(quantityText);

        // create Content values object and add row values
        ContentValues vals = new ContentValues();

        vals.put(ProductEntry.PRODUCT_NAME, productName);
        vals.put(ProductEntry.PRODUCT_DESCRIPTION, productDescription);
        vals.put(ProductEntry.PRODUCT_PRICE, productPrice);
        vals.put(ProductEntry.PRODUCT_QUANTITY, productQuantity);
        vals.put(ProductEntry.SUPPLIER_NAME, productSupplierName);
        vals.put(ProductEntry.SUPPLIER_PHONE, productSupplierPhone );

        if (productDataUri != null) {
            int updatedDbRow = getContentResolver().update(productDataUri, vals, null, null);

            if (updatedDbRow != 0) {
                Toast.makeText(this, "Product details updated", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Details did not update", Toast.LENGTH_SHORT).show();
            }

        } else {

            Uri newDbRow = getContentResolver().insert(ProductEntry.CONTENT_URI, vals);
            if (ContentUris.parseId(newDbRow) == -1) {
                Toast.makeText(this, "Error with adding new product", Toast.LENGTH_SHORT ).show();
            } else {
                Toast.makeText(this, "New product added", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Delete product from SQL db
     */
    private void removeProduct() {
        if (productDataUri != null) {
            int rowsRemoved = getContentResolver().delete(productDataUri, null, null);
            
            if (rowsRemoved == 0) {
                Toast.makeText(this, R.string.product_deletion_failed, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, R.string.product_removed_success, Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    /**
     * Menu methods section
     *
     */

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.product_editor_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if(productDataUri == null) {
            MenuItem menuItem = menu.findItem(R.id.delete_product_action);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()){
            case R.id.save_product_action:
                saveProductDetails();
                finish();
                return true;
            case R.id.delete_product_action:
                removeProduct();        
                return true;
            case android.R.id.home:
                if (!productHasChanged) {
                    NavUtils.navigateUpFromSameTask(ProductEditorActivity.this);
                    return true;
                }

                DialogInterface.OnClickListener discardCheckListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        NavUtils.navigateUpFromSameTask(ProductEditorActivity.this);
                    }
                };
                unsavedDialogDisplayDialog(discardCheckListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Cursor Loader Methods
     *
     */

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        if (productDataUri != null) {
            String[] projection = {
                    InventoryContract.ProductEntry._ID,
                    ProductEntry.PRODUCT_NAME,
                    ProductEntry.PRODUCT_DESCRIPTION,
                    ProductEntry.PRODUCT_PRICE,
                    ProductEntry.PRODUCT_QUANTITY,
                    ProductEntry.SUPPLIER_NAME,
                    ProductEntry.SUPPLIER_PHONE};

            return new CursorLoader(this,
                    productDataUri,
                    projection, null, null, null);
        }

        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        Log.i("CURSOR LOADER DATA", "" + data);
        if (data.moveToFirst()) {
            int nameColIdx = data.getColumnIndex(ProductEntry.PRODUCT_NAME);
            int descriptionColIdx = data.getColumnIndex(ProductEntry.PRODUCT_DESCRIPTION);
            int priceColIdx = data.getColumnIndex(ProductEntry.PRODUCT_PRICE);
            int quantityColIdx = data.getColumnIndex(ProductEntry.PRODUCT_QUANTITY);
            int supplierNameColIdx = data.getColumnIndex(ProductEntry.SUPPLIER_NAME);
            int supplierPhoneColIdx = data.getColumnIndex(ProductEntry.SUPPLIER_PHONE);

            String nameResult = data.getString(nameColIdx);
            String descriptionResult = data.getString(descriptionColIdx);
            double priceResult = data.getDouble(priceColIdx);
            Integer quantityResult = data.getInt(quantityColIdx);
            String supplierNameResult = data.getString(supplierNameColIdx);
            final String supplierPhoneResult = data.getString(supplierPhoneColIdx);

            productNameEditText.setText(nameResult);
            productDescriptionEditText.setText(descriptionResult);
            productPriceEditText.setText(Double.toString(priceResult));
            productQuantityEditText.setText(Integer.toString(quantityResult));
            productSupplierNameEditText.setText(supplierNameResult);
            productSupplierPhoneEditText.setText(supplierPhoneResult);

            callSupplierButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String phoneFormatted = PhoneNumberUtils.formatNumber(supplierPhoneResult);
                    Intent restockCallIntent = new Intent(Intent.ACTION_DIAL);
                    restockCallIntent.setData(Uri.parse("tel:" + phoneFormatted));
                    if (restockCallIntent.resolveActivity(getPackageManager()) != null) {
                        startActivity(restockCallIntent);
                    }
                }
            });
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        productNameEditText.setText(null);
        productDescriptionEditText.setText(null);
        productPriceEditText.setText(null);
        productQuantityEditText.setText(null);
        productSupplierNameEditText.setText(null);
        productSupplierPhoneEditText.setText(null);
    }

    @Override
    public void onBackPressed() {
        if (!productHasChanged) {
            super.onBackPressed();
            return;
        }

        DialogInterface.OnClickListener discardChangesListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                };

        unsavedDialogDisplayDialog(discardChangesListener);
    }

    private void unsavedDialogDisplayDialog (DialogInterface.OnClickListener listener) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setMessage(R.string.discard_changes_message);
        dialogBuilder.setPositiveButton(R.string.yes_button, listener);
        dialogBuilder.setNegativeButton(R.string.no_button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if( dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alert = dialogBuilder.create();
        alert.show();
    }
}
