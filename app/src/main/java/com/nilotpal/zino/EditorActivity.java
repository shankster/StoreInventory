package com.nilotpal.zino;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.app.LoaderManager;
import android.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.nilotpal.zino.data.SaleContract.SaleEntry;

public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int EXISTING_SALE_LOADER=0;
    private Uri mCurrentSaleUri;

    private EditText mName;
    private EditText mDescription;
    private EditText mUnitPrice;
    private EditText mQuantity;
    private EditText mImage;

    //Boolean counter to keep track whether the data has changed or not
    private Boolean mItemHasChanged=false;

    private View.OnTouchListener mTouchListener=new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            mItemHasChanged=true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        //Gathering Data from the Main Activity
        Intent intent=getIntent();
        mCurrentSaleUri=intent.getData();

        if(mCurrentSaleUri==null){
            Button delete=(Button) findViewById(R.id.delete) ;
            delete.setVisibility(View.INVISIBLE);
            setTitle("Add a new item");
        }
        else {
            //Existing Item
            setTitle("Edit an existing item");
            getLoaderManager().initLoader(EXISTING_SALE_LOADER,null,this);
        }


        mName=(EditText)findViewById(R.id.name);
        mDescription=(EditText)findViewById(R.id.description);
        mUnitPrice=(EditText)findViewById(R.id.unitPrice);
        mQuantity=(EditText)findViewById(R.id.quantity);

        //Set On Touch Listeners
        mName.setOnTouchListener(mTouchListener);
        mDescription.setOnTouchListener(mTouchListener);
        mUnitPrice.setOnTouchListener(mTouchListener);
        mQuantity.setOnTouchListener(mTouchListener);


        Button receiveButton=(Button) findViewById(R.id.receive);

        receiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String quantityString=mQuantity.getText().toString();
                if(!quantityString.equals("")){
                    int quantity=Integer.parseInt(quantityString);
                    quantity++;
                    mQuantity.setText(""+quantity);
                }
                else if(quantityString.equals("")){
                    Toast.makeText(EditorActivity.this,"Enter some value for quantity",Toast.LENGTH_SHORT).show();
                }
            }
        });
        Button saleButton=(Button) findViewById(R.id.sale);

        saleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String quantityString=mQuantity.getText().toString();
                if(!quantityString.equals("")){
                    int quantity=Integer.parseInt(quantityString);
                    quantity--;
                    mQuantity.setText(""+quantity);
                }
                else if(quantityString.equals("")){
                    Toast.makeText(EditorActivity.this,"Enter some value for quantity",Toast.LENGTH_SHORT).show();
                }
            }
        });

        Button saveButton=(Button) findViewById(R.id.save);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Boolean isWrong=validateData();
                if(!isWrong){
                    saveItem();
                    Intent intent=new Intent(EditorActivity.this,MainActivity.class);
                    startActivity(intent);
                }
                else if(isWrong){
                    Toast.makeText(EditorActivity.this,"Enter valid values",Toast.LENGTH_SHORT).show();
                }
            }
        });

        final Button deleteItem=(Button) findViewById(R.id.delete);
        deleteItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteItem();
                Intent intent=new Intent(EditorActivity.this,MainActivity.class);
                startActivity(intent);
            }
        });
    }

    private void saveItem() {
        String nameValue = mName.getText().toString().trim();
        String descriptionValue = mDescription.getText().toString().trim();
        String unitPriceValue = mUnitPrice.getText().toString().trim();
        String quantityValue = mQuantity.getText().toString().trim();

        ContentValues values = new ContentValues();
        values.put(SaleEntry.COLUMN_ITEM_NAME, nameValue);
        values.put(SaleEntry.COLUMN_ITEM_DESCRIPTION, descriptionValue);
        values.put(SaleEntry.COLUMN_ITEM_UNIT_PRICE, unitPriceValue);
        values.put(SaleEntry.COLUMN_ITEM_QUANTITY, quantityValue);


        if (mCurrentSaleUri == null) {
            Uri newUri = getContentResolver().insert(SaleEntry.CONTENT_URI, values);

            if (newUri == null) {
                Toast.makeText(this, "Entry not sucessfull in Editor Activity", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Entry sucessfull in Editor Activity", Toast.LENGTH_SHORT).show();
            }
        } else {
            int rowsAffected = getContentResolver().update(mCurrentSaleUri, values, null, null);
            Log.e(EditorActivity.class.getName(),"Update item");
            if(rowsAffected==0){
                Toast.makeText(this,"Item update failed" +
                        "",Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(this,"Item update succesfully",Toast.LENGTH_SHORT).show();
            }
        }
    }
    private boolean validateData(){
        String nameValue=mName.getText().toString().trim();
        String descriptionValue=mDescription.getText().toString().trim();
        String unitPriceValue=mUnitPrice.getText().toString().trim();
        String quantityValue=mQuantity.getText().toString().trim();
        int count=0;
        if(TextUtils.isEmpty(nameValue)){
            Toast.makeText(this,"Enter a name value",Toast.LENGTH_SHORT).show();
            count++;
        }
        if(TextUtils.isEmpty(descriptionValue)){
            Toast.makeText(this,"Enter a description value",Toast.LENGTH_SHORT).show();
            count++;
        }
        if(TextUtils.isEmpty(unitPriceValue)){
            Toast.makeText(this,"Enter a price value",Toast.LENGTH_SHORT).show();
            count++;
        }
        if(TextUtils.isEmpty(quantityValue)){
            Toast.makeText(this,"Enter a quantity value",Toast.LENGTH_SHORT).show();
            count++;
        }
        if(count>0){
            return true;
        }
        return false;
    }
    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("You sure you want to lose this data ?");
        builder.setPositiveButton("Yes", discardButtonClickListener);
        builder.setNegativeButton("No,Keep Editing", new DialogInterface.OnClickListener() {
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

    private void deleteItem(){
        if(mCurrentSaleUri!=null){
            int rowsDeleted=getContentResolver().delete(mCurrentSaleUri,null,null);
            if(rowsDeleted==0){
                Toast.makeText(this,"Deletion of item failed",Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(this,"Deletion of item succesfull",Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onBackPressed() {

        if(!mItemHasChanged){
            super.onBackPressed();
            return;
        }
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
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.e(EditorActivity.class.getName(),"Cursor reaches onCreateLoader");
        String [] projection={
                SaleEntry._ID,
                SaleEntry.COLUMN_ITEM_NAME,
                SaleEntry.COLUMN_ITEM_DESCRIPTION,
                SaleEntry.COLUMN_ITEM_UNIT_PRICE,
                SaleEntry.COLUMN_ITEM_QUANTITY
        };
        return new CursorLoader(this,
                mCurrentSaleUri,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        Log.e(EditorActivity.class.getName(),"Cursor is "+cursor.getCount());
        if(cursor==null || cursor.getCount()<1){
            return;
        }

        if(cursor.moveToFirst()){
            int nameColumnIndex=cursor.getColumnIndex(SaleEntry.COLUMN_ITEM_NAME);
            int descriptionColumnIndex=cursor.getColumnIndex(SaleEntry.COLUMN_ITEM_DESCRIPTION);
            int unitPriceColumnIndex=cursor.getColumnIndex(SaleEntry.COLUMN_ITEM_UNIT_PRICE);
            int quantityColumnIndex=cursor.getColumnIndex(SaleEntry.COLUMN_ITEM_QUANTITY);


            String name=cursor.getString(nameColumnIndex);
            String description=cursor.getString(descriptionColumnIndex);
            int unitPrice=cursor.getInt(unitPriceColumnIndex);
            int quantity=cursor.getInt(quantityColumnIndex);

            mName.setText(name);
            mDescription.setText(description);
            mUnitPrice.setText(Integer.toString(unitPrice));
            mQuantity.setText(Integer.toString(quantity));
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mName.setText("");
        mDescription.setText("");
        mUnitPrice.setText("");
        mQuantity.setText("");

    }
}
