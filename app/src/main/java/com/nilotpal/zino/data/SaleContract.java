package com.nilotpal.zino.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;
import android.util.Log;

public class SaleContract {
    private SaleContract(){}
    public static final String LOG_TAG=SaleContract.class.getName();
    public static final String CONTENT_AUTHORITY="com.nilotpal.zino";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_SALE = "sale";
    public static final class SaleEntry implements BaseColumns{
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_SALE);

        public static final String CONTENT_LIST_TYPE= ContentResolver.CURSOR_DIR_BASE_TYPE+"/"+CONTENT_AUTHORITY+"/"+PATH_SALE;
        public static final String CONTENT_ITEM_TYPE =ContentResolver.CURSOR_ITEM_BASE_TYPE+"/"+CONTENT_AUTHORITY+"/"+PATH_SALE;


        public static final String TABLE_NAME="sale";
        public static final String _ID=BaseColumns._ID;
        public static final String COLUMN_ITEM_NAME="name";
        public static final String COLUMN_ITEM_DESCRIPTION="description";
        public static final String COLUMN_ITEM_UNIT_PRICE="price";
        public static final String COLUMN_ITEM_QUANTITY="quantity";
        public static final String COLUMN_ITEM_IMAGE="image";
        public static final String COLUMN_ITEM_SUPPLIER="supplier";

    }
}
