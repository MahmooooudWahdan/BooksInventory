package com.example.mahmo.booksinventory.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public class BooksContract {

    private BooksContract() {

    }

    public static final class BookEntry implements BaseColumns {
        /**
         * The MIME type of the  for a list of pets.
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_BOOKS;

        /**
         * The MIME type of the for a single pet.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_BOOKS;

        public static final String TABLE_NAME = "Books";

        public static final String _ID = BaseColumns._ID;

        public static final String COLUMN_BOOK_NAME = "BookName";

        public static final String COLUMN_BOOK_PRICE = "Price";

        public static final String COLUMN_QUANTITY = "Quantity";

        public static final String COLUMN_SUPPLIER = "SupplierName";

        public static final String COLUMN_PHONE = "Phone";


        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_BOOKS);
    }

    public static final String CONTENT_AUTHORITY = "com.example.android.booksinventory";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_BOOKS = "Books";

}
