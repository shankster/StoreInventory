package com.nilotpal.zino;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.app.LoaderManager;
import android.content.Loader;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.nilotpal.zino.data.SaleContract.SaleEntry;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int EXISTING_SALE_LOADER = 0;
    private static final int PICK_IMAGE_REQUEST = 0;
    private static final String STATE_URI = "STATE_URI";
    private static final String LOG_TAG=EditorActivity.class.getName();
    private Uri mCurrentSaleUri;

    private EditText mName;
    private EditText mDescription;
    private EditText mUnitPrice;
    private EditText mQuantity;
    private ImageView mImage;
    private EditText mSupplier;
    private Uri mImageUri;

    //Boolean counter to keep track whether the data has changed or not
    private Boolean mItemHasChanged = false;

    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            mItemHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        //Gathering Data from the Main Activity
        Intent intent = getIntent();
        mCurrentSaleUri = intent.getData();

        if (mCurrentSaleUri == null) {
            Button delete = (Button) findViewById(R.id.delete);
            Button order = (Button) findViewById(R.id.order);
            order.setVisibility(View.INVISIBLE);
            delete.setVisibility(View.INVISIBLE);
            setTitle("Add a new item");
        } else {
            //Existing Item
            setTitle("Edit an existing item");
            getLoaderManager().initLoader(EXISTING_SALE_LOADER, null, this);
        }


        mName = (EditText) findViewById(R.id.name);
        mDescription = (EditText) findViewById(R.id.description);
        mUnitPrice = (EditText) findViewById(R.id.unitPrice);
        mQuantity = (EditText) findViewById(R.id.quantity);
        mSupplier = (EditText) findViewById(R.id.supplier);
        mImage=(ImageView)findViewById(R.id.displayImage);


        //Set On Touch Listeners
        mName.setOnTouchListener(mTouchListener);
        mDescription.setOnTouchListener(mTouchListener);
        mUnitPrice.setOnTouchListener(mTouchListener);
        mQuantity.setOnTouchListener(mTouchListener);
        mSupplier.setOnTouchListener(mTouchListener);
        mImage.setOnTouchListener(mTouchListener);


        Button receiveButton = (Button) findViewById(R.id.receive);

        receiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String quantityString = mQuantity.getText().toString();
                if (!quantityString.equals("")) {
                    int quantity = Integer.parseInt(quantityString);
                    quantity++;
                    mQuantity.setText("" + quantity);
                } else if (quantityString.equals("")) {
                    Toast.makeText(EditorActivity.this, "Enter some value for quantity", Toast.LENGTH_SHORT).show();
                }
            }
        });
        Button saleButton = (Button) findViewById(R.id.sale);

        saleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String quantityString = mQuantity.getText().toString();
                if (!quantityString.equals("")) {
                    int quantity = Integer.parseInt(quantityString);
                    int next=quantity--;
                    if (next <= 0) {
                        quantity = 0;
                    }
                    mQuantity.setText("" + quantity);
                } else if (quantityString.equals("")) {
                    Toast.makeText(EditorActivity.this, "Enter some value for quantity", Toast.LENGTH_SHORT).show();
                }
            }
        });

        Button saveButton = (Button) findViewById(R.id.save);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Boolean isWrong = validateData();
                if (!isWrong) {
                    saveItem();
                    Intent intent = new Intent(EditorActivity.this, MainActivity.class);
                    startActivity(intent);
                } else if (isWrong) {
                    Toast.makeText(EditorActivity.this, "Enter valid values", Toast.LENGTH_SHORT).show();
                }
            }
        });

        Button order = (Button) findViewById(R.id.order);
        order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e(EditorActivity.class.getName(), getSupplierEmail());
                String emailid = getSupplierEmail().toLowerCase() + "@gmail.com";
                System.out.print(emailid);
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setType("*/*");
                intent.setData(Uri.parse("mailto:")); // only email apps should handle this
                intent.putExtra(Intent.EXTRA_EMAIL, new String[]{emailid});
                intent.putExtra(Intent.EXTRA_SUBJECT, "Please send me more of this stuff");
                intent.putExtra(Intent.EXTRA_TEXT, "I want to order more of this product. Please send me by mail.");
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                }
            }
        });

        final Button deleteItem = (Button) findViewById(R.id.delete);
        deleteItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteItem();
                Intent intent = new Intent(EditorActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        Button takePicture=(Button) findViewById(R.id.picture);
        takePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });
    }

    private void openGallery(){
        Intent intent;

        if (Build.VERSION.SDK_INT < 19) {
            intent = new Intent(Intent.ACTION_GET_CONTENT);
        } else {
            intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
        }

        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.e(LOG_TAG,"2");
        if (mImageUri != null)
            outState.putString(STATE_URI, mImageUri.toString());
    }

    public Bitmap getBitmapFromUri(Uri uri) {
        Log.e(LOG_TAG,"8");

        if (uri == null || uri.toString().isEmpty())
            return null;

        // Get the dimensions of the View
        int targetW = mImage.getWidth();
        int targetH = mImage.getHeight();

        InputStream input = null;
        try {
            input = this.getContentResolver().openInputStream(uri);

            // Get the dimensions of the bitmap
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            bmOptions.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(input, null, bmOptions);
            input.close();

            int photoW = bmOptions.outWidth;
            int photoH = bmOptions.outHeight;

            if(targetW==0){
                targetW=1;
            }
            if(targetH==0){
                targetH=1;
            }

            // Determine how much to scale down the image
            int scaleFactor = Math.min(photoW / targetW, photoH / targetH);

            // Decode the image file into a Bitmap sized to fill the View
            bmOptions.inJustDecodeBounds = false;
            bmOptions.inSampleSize = scaleFactor;
            bmOptions.inPurgeable = true;

            input = this.getContentResolver().openInputStream(uri);
            Bitmap bitmap = BitmapFactory.decodeStream(input, null, bmOptions);
            input.close();
            return bitmap;

        } catch (FileNotFoundException fne) {
            Log.e(LOG_TAG, "Failed to load image.", fne);
            return null;
        } catch (Exception e) {
            Log.e(LOG_TAG, "Failed to load image.", e);
            return null;
        } finally {
            try {
                input.close();
            } catch (IOException ioe) {

            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        Log.e(LOG_TAG,"7");
        // The ACTION_OPEN_DOCUMENT intent was sent with the request code READ_REQUEST_CODE.
        // If the request code seen here doesn't match, it's the response to some other intent,
        // and the below code shouldn't run at all.

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            // The document selected by the user won't be returned in the intent.
            // Instead, a URI to that document will be contained in the return intent
            // provided to this method as a parameter.  Pull that uri using "resultData.getData()"

            if (resultData != null) {
                mImageUri = resultData.getData();
                Log.i(LOG_TAG, "Uri: " + mImage.toString());

                mImage.setImageBitmap(getBitmapFromUri(mImageUri));
            }
        }
    }
    


    private void saveItem() {
        String nameValue = mName.getText().toString().trim();
        String descriptionValue = mDescription.getText().toString().trim();
        String unitPriceValue = mUnitPrice.getText().toString().trim();
        String quantityValue = mQuantity.getText().toString().trim();
        String supplierValue = mSupplier.getText().toString().trim();




        ContentValues values = new ContentValues();
        values.put(SaleEntry.COLUMN_ITEM_NAME, nameValue);
        values.put(SaleEntry.COLUMN_ITEM_DESCRIPTION, descriptionValue);
        values.put(SaleEntry.COLUMN_ITEM_UNIT_PRICE, unitPriceValue);
        values.put(SaleEntry.COLUMN_ITEM_QUANTITY, quantityValue);
        values.put(SaleEntry.COLUMN_ITEM_SUPPLIER, supplierValue);


        if (mCurrentSaleUri == null) {
            Uri newUri = getContentResolver().insert(SaleEntry.CONTENT_URI, values);

            if (newUri == null) {
                Toast.makeText(this, "Entry not sucessfull in Editor Activity", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Entry sucessfull in Editor Activity", Toast.LENGTH_SHORT).show();
            }
        } else {
            int rowsAffected = getContentResolver().update(mCurrentSaleUri, values, null, null);
            Log.e(EditorActivity.class.getName(), "Update item");
            if (rowsAffected == 0) {
                Toast.makeText(this, "Item update failed" +
                        "", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Item update succesfully", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean validateData() {
        String nameValue = mName.getText().toString().trim();
        String descriptionValue = mDescription.getText().toString().trim();
        String unitPriceValue = mUnitPrice.getText().toString().trim();
        String quantityValue = mQuantity.getText().toString().trim();
        String supplierValue = mSupplier.getText().toString().trim();
        int count = 0;
        if (TextUtils.isEmpty(nameValue)) {
            Toast.makeText(this, "Enter a name value", Toast.LENGTH_SHORT).show();
            count++;
        }
        if (TextUtils.isEmpty(descriptionValue)) {
            Toast.makeText(this, "Enter a description value", Toast.LENGTH_SHORT).show();
            count++;
        }
        if (TextUtils.isEmpty(unitPriceValue)) {
            Toast.makeText(this, "Enter a price value", Toast.LENGTH_SHORT).show();
            count++;
        }
        if (TextUtils.isEmpty(quantityValue)) {
            Toast.makeText(this, "Enter a quantity value", Toast.LENGTH_SHORT).show();
            count++;
        }
        if (TextUtils.isEmpty(supplierValue)) {
            Toast.makeText(this, "Enter a supplier value", Toast.LENGTH_SHORT).show();
            count++;
        }
        if (count > 0) {
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

    private void deleteItem() {
        if (mCurrentSaleUri != null) {
            int rowsDeleted = getContentResolver().delete(mCurrentSaleUri, null, null);
            if (rowsDeleted == 0) {
                Toast.makeText(this, "Deletion of item failed", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Deletion of item succesfull", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private String getSupplierEmail() {
        return mSupplier.getText().toString().trim();
    }

    @Override
    public void onBackPressed() {

        if (!mItemHasChanged) {
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
        Log.e(EditorActivity.class.getName(), "Cursor reaches onCreateLoader");
        String[] projection = {
                SaleEntry._ID,
                SaleEntry.COLUMN_ITEM_NAME,
                SaleEntry.COLUMN_ITEM_DESCRIPTION,
                SaleEntry.COLUMN_ITEM_UNIT_PRICE,
                SaleEntry.COLUMN_ITEM_QUANTITY,
                SaleEntry.COLUMN_ITEM_SUPPLIER
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
        Log.e(EditorActivity.class.getName(), "Cursor is " + cursor.getCount());
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        if (cursor.moveToFirst()) {
            int nameColumnIndex = cursor.getColumnIndex(SaleEntry.COLUMN_ITEM_NAME);
            int descriptionColumnIndex = cursor.getColumnIndex(SaleEntry.COLUMN_ITEM_DESCRIPTION);
            int unitPriceColumnIndex = cursor.getColumnIndex(SaleEntry.COLUMN_ITEM_UNIT_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(SaleEntry.COLUMN_ITEM_QUANTITY);
            int supplierColumnIndex = cursor.getColumnIndex(SaleEntry.COLUMN_ITEM_SUPPLIER);


            String name = cursor.getString(nameColumnIndex);
            String description = cursor.getString(descriptionColumnIndex);
            int unitPrice = cursor.getInt(unitPriceColumnIndex);
            int quantity = cursor.getInt(quantityColumnIndex);
            String supplier = cursor.getString(supplierColumnIndex);

            mName.setText(name);
            mDescription.setText(description);
            mUnitPrice.setText(Integer.toString(unitPrice));
            mQuantity.setText(Integer.toString(quantity));
            mSupplier.setText(supplier);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mName.setText("");
        mDescription.setText("");
        mUnitPrice.setText("");
        mQuantity.setText("");
        mSupplier.setText("");
    }
}
