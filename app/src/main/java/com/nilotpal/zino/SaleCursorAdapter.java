package com.nilotpal.zino;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class SaleCursorAdapter extends CursorAdapter {
    public SaleCursorAdapter(Context context, Cursor c) {
        super(context, c);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.item_sale,parent,false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView name=(TextView)view.findViewById(R.id.name);
        TextView quantity=(TextView) view.findViewById(R.id.quantity);
        TextView price=(TextView) view.findViewById(R.id.price);

        String nameCursor=cursor.getString(cursor.getColumnIndexOrThrow("name"));
        String quantityCursor=cursor.getString(cursor.getColumnIndexOrThrow("quantity"));
        String priceCursor=cursor.getString(cursor.getColumnIndexOrThrow("price"));

        name.setText(nameCursor);
        quantity.setText("Units: "+quantityCursor);
        price.setText("Rs. "+priceCursor);

    }
}
