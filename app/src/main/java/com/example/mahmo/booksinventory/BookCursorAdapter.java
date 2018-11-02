package com.example.mahmo.booksinventory;

import android.Manifest;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.Image;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mahmo.booksinventory.data.BooksContract.BookEntry;
import com.example.mahmo.booksinventory.data.BooksProvider;


public class BookCursorAdapter extends CursorAdapter {
    public static final String LOG_TAG = BookCursorAdapter.class.getSimpleName();

    private Context mContext;


    public BookCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0 /* flags */);
        mContext = context;
    }


    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {

        // Find individual views that we want to modify in the list item layout
        TextView nameTextView = (TextView) view.findViewById(R.id.name);
        TextView priceTextView = (TextView) view.findViewById(R.id.price);
        TextView supplierTextView = view.findViewById(R.id.supplier);
        TextView StockTextView = view.findViewById(R.id.stock);


        ImageView call = view.findViewById(R.id.call);
        ImageView buy = view.findViewById(R.id.Buy);


        int nameColumnIndex = cursor.getColumnIndexOrThrow(BookEntry.COLUMN_BOOK_NAME);
        Log.i(LOG_TAG, "bindView: " + nameColumnIndex);
        int PriceColumnIndex = cursor.getColumnIndexOrThrow(BookEntry.COLUMN_BOOK_PRICE);
        Log.i(LOG_TAG, "bindView: " + PriceColumnIndex);
        int StockColumnIndex = cursor.getColumnIndexOrThrow(BookEntry.COLUMN_QUANTITY);
        Log.i(LOG_TAG, "bindView: " + StockColumnIndex);
        int SupplierColumnIndex = cursor.getColumnIndexOrThrow(BookEntry.COLUMN_SUPPLIER);
        Log.i(LOG_TAG, "bindView: " + SupplierColumnIndex);


        final int IdColumnIndex = cursor.getInt(cursor.getColumnIndex(BookEntry._ID));

        final String phoneColumn = cursor.getString(cursor.getColumnIndex(BookEntry.COLUMN_PHONE));


        // Read the pet attributes from the Cursor for the current pet
        String BookName = cursor.getString(nameColumnIndex);
        int BookPrice = cursor.getInt(PriceColumnIndex);
        final int BookQuantity = cursor.getInt(StockColumnIndex);
        Log.i("Book Quantity", "bindView: " + BookQuantity);
        String BookSupplier = cursor.getString(SupplierColumnIndex);

        if (TextUtils.isEmpty(BookSupplier)) {
            supplierTextView.setText(R.string.unknown_Supplier);
        } else {
            supplierTextView.setText("Supplied by " + BookSupplier);
        }

        nameTextView.setText(BookName);

        priceTextView.setText(BookPrice + "$");


        if (BookQuantity > 0) {
            StockTextView.setText(BookQuantity + " In Stock");
        } else {
            StockTextView.setText("Out of Stock");
        }

        buy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Uri BookUri = ContentUris.withAppendedId(BookEntry.CONTENT_URI, IdColumnIndex);
                bookQuantity(context, BookUri, BookQuantity);
            }
        });
        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mContext.startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phoneColumn)));
            }
        });

    }

    private void bookQuantity(Context context, Uri BookUri, int currentQuantityInStock) {


        int newQuantityValue;
        if (currentQuantityInStock > 1) {
            newQuantityValue = currentQuantityInStock -= 1;
        } else {
            newQuantityValue = 0;
        }

        if (currentQuantityInStock == 0) {
            Toast.makeText(context.getApplicationContext(), "Out of stock", Toast.LENGTH_SHORT).show();
        }
        ContentValues contentValues = new ContentValues();
        contentValues.put(BookEntry.COLUMN_QUANTITY, newQuantityValue);
        int numRowsUpdated = context.getContentResolver().update(BookUri, contentValues, null, null);
        if (numRowsUpdated > 0) {
            Toast.makeText(context.getApplicationContext(), "Happy Shopping 😊", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context.getApplicationContext(), "out of stock", Toast.LENGTH_SHORT).show();

        }
    }


}