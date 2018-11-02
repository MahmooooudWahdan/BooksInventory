package com.example.mahmo.booksinventory.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class dbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "books.db";

    private static final int DATABASE_VERSION = 1;

    private static final String TAG = dbHelper.class.getSimpleName();


    public dbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_CREATE_BOOKS_TABLE = "CREATE TABLE " + BooksContract.BookEntry.TABLE_NAME + " ("
                + BooksContract.BookEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + BooksContract.BookEntry.COLUMN_BOOK_NAME + " TEXT NOT NULL,"
                + BooksContract.BookEntry.COLUMN_BOOK_PRICE + " INTEGER NOT NULL , "
                + BooksContract.BookEntry.COLUMN_QUANTITY + " INTEGER DEFAULT 0 ,"
                + BooksContract.BookEntry.COLUMN_SUPPLIER + " TEXT ,"
                + BooksContract.BookEntry.COLUMN_PHONE + " TEXT" + ");";
        Log.i(TAG, "onCreate: " + SQL_CREATE_BOOKS_TABLE);
        // Execute the SQL statement
        db.execSQL(SQL_CREATE_BOOKS_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
