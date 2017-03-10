package com.nilotpal.zino;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.widget.SimpleCursorAdapter;


import com.nilotpal.zino.data.SaleContract.SaleEntry;
import com.nilotpal.zino.data.SaleDbHelper;

public class MainActivity extends AppCompatActivity implements  LoaderManager.LoaderCallbacks<Cursor>
{
    private static final String LOG_TAG=MainActivity.class.getName();
    public static SaleDbHelper mDbHelper;
    private ListView listItems;
    private static final int SALE_LOADER=0;
    private SaleCursorAdapter mCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mDbHelper=new SaleDbHelper(this);

        //Add dummy content
        Button dummyAdd=(Button) findViewById(R.id.addDummy);
        dummyAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addDummyItem();
            }
        });

        //Add an item to the database
        Button addItem=(Button) findViewById(R.id.addItem);
        addItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this,EditorActivity.class);
                startActivity(intent);
            }
        });


        final Button deleteTable=(Button) findViewById(R.id.deleteTable);
        deleteTable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteEntireTable();
            }
        });


        listItems=(ListView) findViewById(R.id.list);

        //Set Empty View
        View emptyView=(View)findViewById(R.id.emptyView);
        listItems.setEmptyView(emptyView);

        mCursorAdapter=new SaleCursorAdapter(this,null);
        listItems.setAdapter(mCursorAdapter);
        getLoaderManager().initLoader(SALE_LOADER,null,this);
    }

    private void addDummyItem(){
//        SQLiteDatabase db= mDbHelper.getWritableDatabase();
        ContentValues value=new ContentValues();

        value.put(SaleEntry.COLUMN_ITEM_NAME,"Tester");
        value.put(SaleEntry.COLUMN_ITEM_DESCRIPTION,"This is testing testing");
        value.put(SaleEntry.COLUMN_ITEM_UNIT_PRICE,60);
        value.put(SaleEntry.COLUMN_ITEM_QUANTITY,35);
        value.put(SaleEntry.COLUMN_ITEM_IMAGE,"null");

        Uri newUri=getContentResolver().insert(SaleEntry.CONTENT_URI,value);

        if(newUri==null){
            Toast.makeText(this,"Entry not sucessfull in Main Activity",Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(this,"Entry sucessfull in Main Activity",Toast.LENGTH_SHORT).show();
        }
    }

    private Cursor returnCursor(){
        String [] projection={
                SaleEntry._ID,
                SaleEntry.COLUMN_ITEM_NAME,
                SaleEntry.COLUMN_ITEM_DESCRIPTION,
                SaleEntry.COLUMN_ITEM_UNIT_PRICE,
                SaleEntry.COLUMN_ITEM_QUANTITY,
                SaleEntry.COLUMN_ITEM_IMAGE};
//        Cursor cursor=db.query(SaleEntry.TABLE_NAME,projection,null,null,null,null,null);
        Cursor cursor=getContentResolver().query(SaleEntry.CONTENT_URI,projection,null,null,null);
        return cursor;
    }

    private void displayDatabaseInfo() {
        SaleCursorAdapter cursorAdapter=new SaleCursorAdapter(this,returnCursor());
        listItems.setAdapter(cursorAdapter);

    }

    private void deleteEntireTable(){
        int noOfDeletedItems=getContentResolver().delete(SaleEntry.CONTENT_URI,null,null);
        Toast.makeText(this,noOfDeletedItems+" items deleted",Toast.LENGTH_SHORT).show();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle args) {
        String[] projection={
                SaleEntry._ID,
                SaleEntry.COLUMN_ITEM_NAME,
                SaleEntry.COLUMN_ITEM_QUANTITY,
                SaleEntry.COLUMN_ITEM_UNIT_PRICE,
        };
            return new CursorLoader(this,
                    SaleEntry.CONTENT_URI,
                    projection,
                    null,
                    null,
                    null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursorAdapter.swapCursor(null);
    }
}
