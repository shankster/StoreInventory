package com.nilotpal.zino.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.nilotpal.zino.data.SaleContract.SaleEntry;


public class SaleDbHelper extends SQLiteOpenHelper{

    private static final String LOG_TAG=SaleDbHelper.class.getSimpleName();
    private static final String DATABASE_NAME="sale.db";
    private static final int DATABASE_VERSION=1;


    public SaleDbHelper(Context context) {
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        //Create a String that creates the SQL query
        String SQL_CREATE_SALE_TABLE="CREATE TABLE "+SaleEntry.TABLE_NAME
                +" ("+SaleEntry._ID+" INTEGER PRIMARY KEY AUTOINCREMENT,"
                +SaleEntry.COLUMN_ITEM_NAME+" TEXT NOT NULL,"
                +SaleEntry.COLUMN_ITEM_DESCRIPTION+" TEXT ,"
                +SaleEntry.COLUMN_ITEM_UNIT_PRICE+" INTEGER NOT NULL ,"
                +SaleEntry.COLUMN_ITEM_QUANTITY+" INTEGER DEFAULT 0 ,"
                +SaleEntry.COLUMN_ITEM_IMAGE+" TEXT DEFAULT NULL ,"
                +SaleEntry.COLUMN_ITEM_SUPPLIER+" TEXT"+
                ");"
                ;
        Log.e(LOG_TAG,SQL_CREATE_SALE_TABLE);
        db.execSQL(SQL_CREATE_SALE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
