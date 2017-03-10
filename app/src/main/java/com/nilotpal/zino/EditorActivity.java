package com.nilotpal.zino;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.nilotpal.zino.data.SaleContract.SaleEntry;

import com.nilotpal.zino.data.SaleContract;

public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int EXISTING_SALE_LOADER=0;

    private EditText mName;
    private EditText mDescription;
    private EditText mUnitPrice;
    private EditText mQuantity;
    private EditText mImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        //Gathering Data from the Main Activity
        Intent intent=getIntent();
        Uri currentItemUri=intent.getData();

        if(currentItemUri==null){
            setTitle("Add a new item");
        }
        else {
            setTitle("Edit an existing item");
        }


        mName=(EditText)findViewById(R.id.name);
        mDescription=(EditText)findViewById(R.id.description);
        mUnitPrice=(EditText)findViewById(R.id.unitPrice);
        mQuantity=(EditText)findViewById(R.id.quantity);
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
                    insertItem();
                    Intent intent=new Intent(EditorActivity.this,MainActivity.class);
                    startActivity(intent);
                }
                else if(isWrong){
                    Toast.makeText(EditorActivity.this,"Enter valid values",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void insertItem(){
        String nameValue=mName.getText().toString().trim();
        String descriptionValue=mDescription.getText().toString().trim();
        String unitPriceValue=mUnitPrice.getText().toString().trim();
        String quantityValue=mQuantity.getText().toString().trim();

        ContentValues values=new ContentValues();
        values.put(SaleEntry.COLUMN_ITEM_NAME,nameValue);
        values.put(SaleEntry.COLUMN_ITEM_DESCRIPTION,descriptionValue);
        values.put(SaleEntry.COLUMN_ITEM_UNIT_PRICE,unitPriceValue);
        values.put(SaleEntry.COLUMN_ITEM_QUANTITY,quantityValue);

        Uri newUri=getContentResolver().insert(SaleEntry.CONTENT_URI,values);

        if(newUri==null){
            Toast.makeText(this,"Entry not sucessfull in Editor Activity",Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(this,"Entry sucessfull in Editor Activity",Toast.LENGTH_SHORT).show();
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

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
