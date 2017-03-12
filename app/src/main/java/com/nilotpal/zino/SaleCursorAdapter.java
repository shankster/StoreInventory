package com.nilotpal.zino;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.nilotpal.zino.data.SaleContract;

public class SaleCursorAdapter extends CursorAdapter {
    public SaleCursorAdapter(Context context, Cursor c) {
        super(context, c);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.item_sale,parent,false);
    }

    @Override
    public void bindView(View view, final Context context, final Cursor cursor) {
        TextView name=(TextView)view.findViewById(R.id.name);
        TextView quantity=(TextView) view.findViewById(R.id.quantity);
        TextView price=(TextView) view.findViewById(R.id.price);

        final String nameCursor=cursor.getString(cursor.getColumnIndexOrThrow("name"));
        final String quantityCursor=cursor.getString(cursor.getColumnIndexOrThrow("quantity"));
        String priceCursor=cursor.getString(cursor.getColumnIndexOrThrow("price"));

        name.setText(nameCursor);
        quantity.setText("Units: "+quantityCursor);
        price.setText("Rs. "+priceCursor);

        final int position = cursor.getPosition();

        Button sale = (Button) view.findViewById(R.id.saleInList);
        sale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                decreaseUnits();
            }
            private void decreaseUnits(){
                int quantity,id;
                Cursor c=cursor;
                c.moveToPosition(position);
                quantity=cursor.getInt(cursor.getColumnIndexOrThrow("quantity"));
                id=cursor.getInt(cursor.getColumnIndexOrThrow(BaseColumns._ID));
                Uri currentItemUri = ContentUris.withAppendedId(SaleContract.SaleEntry.CONTENT_URI, id);
                Log.e(SaleCursorAdapter.class.getName(),"the quantity for this element is "+quantity+" and  the id is "+id);
                int modifiedQuantity=quantity-1;
                if (modifiedQuantity>=0){
                String entryModifiedQuantity=Integer.toString(modifiedQuantity);
                ContentValues values=new ContentValues();
                values.put(SaleContract.SaleEntry.COLUMN_ITEM_QUANTITY,entryModifiedQuantity);
                int rowsAffected=context.getContentResolver().update(currentItemUri,values,null,null);
                if(rowsAffected==0){
                    Log.e(SaleCursorAdapter.class.getName(),"sale failed");
                }
                else {
                    Log.e(SaleCursorAdapter.class.getName(),"sale succesfull");
                }

            }
            else {
                }
            }
        });


    }
}
