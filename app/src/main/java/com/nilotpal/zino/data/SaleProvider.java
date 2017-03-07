package com.nilotpal.zino.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.nilotpal.zino.data.SaleContract.SaleEntry;

public class SaleProvider extends ContentProvider {
    public static final String LOG_TAG = SaleProvider.class.getName();
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    private static final int SALE = 100;
    private static final int SALE_ID = 101;

    static {
        sUriMatcher.addURI(SaleContract.CONTENT_AUTHORITY, SaleContract.PATH_SALE, SALE);
        sUriMatcher.addURI(SaleContract.CONTENT_AUTHORITY, SaleContract.PATH_SALE + "/#", SALE_ID);
    }

    private SaleDbHelper mDbHelper;

    @Override
    public boolean onCreate() {
        mDbHelper = new SaleDbHelper(getContext());
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        Cursor cursor;
        int match = sUriMatcher.match(uri);
        switch (match) {
            case SALE:
                cursor = db.query(SaleEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case SALE_ID:
                selection = SaleEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = db.query(SaleEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown Uri " + uri);
        }
        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        final int match=sUriMatcher.match(uri);
        switch (match){
            case SALE:
                return SaleEntry.CONTENT_LIST_TYPE;
            case SALE_ID:
                return SaleEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("Unknown URI "+uri+" with match "+match);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case SALE:
                return insertSale(uri, values);
            default:
                throw new IllegalArgumentException("Wrong URI: " + uri);
        }
    }

    private Uri insertSale(Uri uri, ContentValues values) {
        //Get Writable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        String name = values.getAsString(SaleEntry.COLUMN_ITEM_NAME);
        if (name == null) {
            throw new IllegalArgumentException("Item needs a name");
        }

        String description = values.getAsString(SaleEntry.COLUMN_ITEM_DESCRIPTION);
        if (description == null) {
            throw new IllegalArgumentException("Item needs a description");
        }


        //Insert new sale with values
        long id = database.insert(SaleEntry.TABLE_NAME, null, values);
        if (id == -1) {
            Log.e(LOG_TAG, "Insert Sale Failed in Provider");
            return null;
        } else
            Log.e(LOG_TAG, "Inserted Sale Succesfully using Provider Class");
        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db=mDbHelper.getWritableDatabase();
        final int match=sUriMatcher.match(uri);
        switch (match){
            case SALE:
                return db.delete(SaleEntry.TABLE_NAME,selection,selectionArgs);
            case SALE_ID:
                selection=SaleEntry._ID+"=?";
                selectionArgs=new String[]{String.valueOf(ContentUris.parseId(uri))};
                return db.delete(SaleEntry.TABLE_NAME,selection,selectionArgs);
            default:
                throw new IllegalArgumentException("Delete operation not supported for "+uri);
        }
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case SALE:
                return updateSale(uri, values, selection, selectionArgs);
            case SALE_ID:
                selection = SaleEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateSale(uri, values, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    private int updateSale(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        if (values.size() == 0) {
            return 0;
        }

        if (values.containsKey(SaleEntry.COLUMN_ITEM_NAME)) {
            String name = values.getAsString(SaleEntry.COLUMN_ITEM_NAME);
            if (name == null) {
                throw new IllegalArgumentException("Item requires a name");
            }
        }

        if (values.containsKey(SaleEntry.COLUMN_ITEM_DESCRIPTION)) {
            String name = values.getAsString(SaleEntry.COLUMN_ITEM_DESCRIPTION);
            if (name == null) {
                throw new IllegalArgumentException("Item requires a description");
            }
        }

        if (values.containsKey(SaleEntry.COLUMN_ITEM_UNIT_PRICE)) {
            Integer name = values.getAsInteger(SaleEntry.COLUMN_ITEM_DESCRIPTION);
            if (name == null) {
                throw new IllegalArgumentException("Item requires an unit price");
            }
        }

        SQLiteDatabase db=mDbHelper.getWritableDatabase();
        return db.update(SaleEntry.TABLE_NAME,values,selection,selectionArgs);
    }
}
