package com.example.mahmo.booksinventory;

import android.Manifest;
import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.PhoneNumberUtils;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mahmo.booksinventory.data.BooksContract.BookEntry;

public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {


    private static final int EXISTING_PET_LOADER = 1;


    private Uri mCurrentBookUri;


    private EditText mNameEditText;

    private EditText mPriceEditText;

    private EditText mQuantityEditText;

    private EditText mSupplier;

    private EditText mPhoneEdit;


    private boolean mBookHasChanged = false;


    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mBookHasChanged = true;
            return false;
        }
    };


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editor_activity);
        // Examine the intent that was used to launch this activity,
        // in order to figure out if we're creating a new Book or editing an existing one.
        Intent intent = getIntent();
        mCurrentBookUri = intent.getData();


        // If the intent DOES NOT contain a pet content URI, then we know that we are
        // creating a new pet.
        if (mCurrentBookUri == null) {

            setTitle("Add a Book");

            invalidateOptionsMenu();
        } else {

            setTitle("Edit Book Info");

            // Initialize a loader to read the pet data from the database
            // and display the current values in the editor
            getLoaderManager().initLoader(EXISTING_PET_LOADER, null, this);
        }

        // Find all relevant views that we will need to read user input from
        mNameEditText = (EditText) findViewById(R.id.bookName);
        mPriceEditText = (EditText) findViewById(R.id.price);
        mQuantityEditText = (EditText) findViewById(R.id.quantity);
        mPhoneEdit = (EditText) findViewById(R.id.su_nu);
        mSupplier = findViewById(R.id.supplier);


        Button plus = findViewById(R.id.plus);
        Button Minus = findViewById(R.id.minus);


        plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String quantityString = mQuantityEditText.getText().toString().trim();
                int quantity = Integer.parseInt(quantityString);
                quantity++;
                String quantityReturn = Integer.toString(quantity);
                mQuantityEditText.setText(quantityReturn);
            }
        });

        Minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String quantityString = mQuantityEditText.getText().toString().trim();

                int quantity = Integer.parseInt(quantityString);
                if (quantity > 0) {
                    quantity--;
                    String quantityReturn = Integer.toString(quantity);
                    mQuantityEditText.setText(quantityReturn);
                } else {
                    quantity = 0;
                    String quantityReturn = Integer.toString(quantity);
                    mQuantityEditText.setText(quantityReturn);
                }
            }
        });

        // Setup OnTouchListeners on all the input fields, so we can determine if the user
        // has touched or modified them. This will let us know if there are unsaved changes
        // or not, if the user tries to leave the editor without saving.
        mNameEditText.setOnTouchListener(mTouchListener);
        mPriceEditText.setOnTouchListener(mTouchListener);
        mQuantityEditText.setOnTouchListener(mTouchListener);
        mPhoneEdit.setOnTouchListener(mTouchListener);
        mSupplier.setOnTouchListener(mTouchListener);


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.editor, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        // If this is a new Book, hide the "Delete" menu item.
        if (mCurrentBookUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete_one);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_done:
                if (isvalid()) {
                    saveBook();
                    finish();
                }
                return true;
            // Respond to a click on the "Delete" menu option
            case R.id.action_delete_one:
                // Pop up confirmation dialog for deletion
                showDeleteConfirmationDialog();
                return true;

            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                // If the pet hasn't changed, continue with navigating up to parent activity
                // which is the {@link CatalogActivity}.
                if (!mBookHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }

                // Otherwise if there are unsaved changes, setup a dialog to warn the user.
                // Create a click listener to handle the user confirming that
                // changes should be discarded.
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // User clicked "Discard" button, navigate to parent activity.
                                NavUtils.navigateUpFromSameTask(EditorActivity.this);
                            }
                        };

                // Show a dialog that notifies the user they have unsaved changes
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Discard your changes and Quit ?!");
        builder.setPositiveButton("Discard", discardButtonClickListener);
        builder.setNegativeButton("Keep Editing", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Keep editing" button, so dismiss the dialog
                // and continue editing the pet.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


    private void showDeleteConfirmationDialog() {
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to delete the book ?! ");
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteBook();
            }
        });
        builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        android.support.v7.app.AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void deleteBook() {
        // Only perform the delete if this is an existing pet.
        if (mCurrentBookUri != null) {
            // Call the ContentResolver to delete the pet at the given content URI.
            // Pass in null for the selection and selection args because the mCurrentBookUri
            // content URI already identifies the pet that we want.
            int rowsDeleted = getContentResolver().delete(mCurrentBookUri, null, null);
            // Show a toast message depending on whether or not the delete was successful.
            if (rowsDeleted == 0) {
                // If no rows were deleted, then there was an error with the delete.
                Toast.makeText(this, getString(R.string.deletion_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the delete was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.deletion_succesfull),
                        Toast.LENGTH_SHORT).show();
            }
            // Close the activity
            finish();
        }
    }

    private void saveBook() {
        // Read from input fields
        // Use trim to eliminate leading or trailing white space
        String nameString = mNameEditText.getText().toString().trim();
        String PriceString = mPriceEditText.getText().toString().trim();
        String QuantityString = mQuantityEditText.getText().toString().trim();
        String SupplierString = mSupplier.getText().toString().trim();
        String phoneString = mPhoneEdit.getText().toString().trim();

        // Check if this is supposed to be a new pet
        // and check if all the fields in the editor are blank
        if (mCurrentBookUri == null &&
                TextUtils.isEmpty(nameString) && TextUtils.isEmpty(SupplierString) &&
                TextUtils.isEmpty(PriceString) && TextUtils.isEmpty(QuantityString) && TextUtils.isEmpty(phoneString)) {
            return;
        }

        // Create a ContentValues object where column names are the keys,
        // and pet attributes from the editor are the values.
        ContentValues values = new ContentValues();
        values.put(BookEntry.COLUMN_BOOK_NAME, nameString);
        values.put(BookEntry.COLUMN_BOOK_PRICE, PriceString);
        values.put(BookEntry.COLUMN_SUPPLIER, SupplierString);
        values.put(BookEntry.COLUMN_PHONE, phoneString);

        int Price = 0;
        int Quantity = 0;
        if (!TextUtils.isEmpty(PriceString)) {
            Price = Integer.parseInt(PriceString);
        }
        values.put(BookEntry.COLUMN_BOOK_PRICE, Price);

        if (!TextUtils.isEmpty(QuantityString)) {
            Quantity = Integer.parseInt(QuantityString);
        }
        values.put(BookEntry.COLUMN_QUANTITY, Quantity);

        // Determine if this is a new or existing pet by checking if mCurrentBookUri is null or not
        if (mCurrentBookUri == null) {

            Uri newUri = getContentResolver().insert(BookEntry.CONTENT_URI, values);

            // Show a toast message depending on whether or not the insertion was successful.
            if (newUri == null) {
                // If the new content URI is null, then there was an error with insertion.
                Toast.makeText(this, getString(R.string.insert_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the insertion was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.insert_succecfull),
                        Toast.LENGTH_SHORT).show();
            }
        } else {

            int rowsAffected = getContentResolver().update(mCurrentBookUri, values, null, null);

            // Show a toast message depending on whether or not the update was successful.
            if (rowsAffected == 0) {
                // If no rows were affected, then there was an error with the update.
                Toast.makeText(this, getString(R.string.update_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the update was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.update_succeded),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (!mBookHasChanged) {
            super.onBackPressed();
            return;
        }
        // Otherwise if there are unsaved changes, setup a dialog to warn the user.
        // Create a click listener to handle the user confirming that changes should be discarded.
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User clicked "Discard" button, close the current activity.
                        finish();
                    }
                };

        // Show dialog that there are unsaved changes
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {

        String[] projection = {
                BookEntry.COLUMN_BOOK_NAME,
                BookEntry.COLUMN_BOOK_PRICE,
                BookEntry.COLUMN_QUANTITY,
                BookEntry.COLUMN_PHONE,
                BookEntry.COLUMN_SUPPLIER};

        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,   // Parent activity context
                mCurrentBookUri,         // Query the content URI for the current pet
                projection,             // Columns to include in the resulting Cursor
                null,                   // No selection clause
                null,                   // No selection arguments
                null);                  // Default sort order
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        // Bail early if the cursor is null or there is less than 1 row in the cursor
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        // Proceed with moving to the first row of the cursor and reading data from it
        // (This should be the only row in the cursor)
        if (cursor.moveToFirst()) {
            // Find the columns of pet attributes that we're interested in
            int nameColumnIndex = cursor.getColumnIndexOrThrow(BookEntry.COLUMN_BOOK_NAME);
            int PriceColumnIndex = cursor.getColumnIndexOrThrow(BookEntry.COLUMN_BOOK_PRICE);
            int QuantityColumnIndex = cursor.getColumnIndexOrThrow(BookEntry.COLUMN_QUANTITY);
            int SupplierColumnIndex = cursor.getColumnIndexOrThrow(BookEntry.COLUMN_SUPPLIER);
            int PhoneColumnIndex = cursor.getColumnIndexOrThrow(BookEntry.COLUMN_PHONE);

            // Extract out the value from the Cursor for the given column index
            String name = cursor.getString(nameColumnIndex);
            String Supplier = cursor.getString(SupplierColumnIndex);
            String Phone = cursor.getString(PhoneColumnIndex);
            int Quantity = cursor.getInt(QuantityColumnIndex);
            int Price = cursor.getInt(PriceColumnIndex);

            // Update the views on the screen with the values from the database
            mNameEditText.setText(name);
            mPhoneEdit.setText(Phone);
            mSupplier.setText(Supplier);
            mQuantityEditText.setText(Integer.toString(Quantity));
            mPriceEditText.setText(Integer.toString(Price));


        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // If the loader is invalidated, clear out all the data from the input fields.
        mNameEditText.setText("");
        mPhoneEdit.setText("");
        mSupplier.setText("");
        mQuantityEditText.setText("");
        mPriceEditText.setText("");

    }

    private boolean isvalid() {
        String nameString = mNameEditText.getText().toString().trim();
        String priceString = mPriceEditText.getText().toString().trim();
        String quantityString = mQuantityEditText.getText().toString().trim();
        String nameSupplierString = mSupplier.getText().toString().trim();
        String phoneSupplierString = mPhoneEdit.getText().toString().trim();

        if (TextUtils.isEmpty(nameString)) {
            Toast.makeText(this, "No Name inserted", Toast.LENGTH_SHORT).show();
            return false;
        } else if (TextUtils.isEmpty(priceString)) {
            Toast.makeText(this, "No Price is inserted", Toast.LENGTH_SHORT).show();
            return false;
        } else if (TextUtils.isEmpty(quantityString)) {
            Toast.makeText(this, "Please add the Quantity number", Toast.LENGTH_SHORT).show();
            return false;
        } else if (TextUtils.isEmpty(nameSupplierString)) {
            Toast.makeText(this, "Supplier name is not inserted", Toast.LENGTH_SHORT).show();
            return false;
        } else if (TextUtils.isEmpty(phoneSupplierString)) {
            Toast.makeText(this, "Supplier phone is needed", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            if (!PhoneNumberUtils.isGlobalPhoneNumber(phoneSupplierString)) {
                Toast.makeText(this, "Not a valid phone Number", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        return true;
    }
}

