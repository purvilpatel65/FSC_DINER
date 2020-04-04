package com.example.fsc_diner.controller;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fsc_diner.R;
import com.example.fsc_diner.controller.adapter.FoodItemInfoAdapter;
import com.example.fsc_diner.model.FoodItem;
import com.example.fsc_diner.model.FoodItemInfo;
import com.example.fsc_diner.model.RestaurantInfo;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class FoodMenuManagerSide extends AppCompatActivity {

    private Dialog addFoodItemDialog;
    private TextView imgText;
    private Bitmap bitmap;

    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef;
    private FirebaseAuth mAuth;

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private List<FoodItemInfo> foodItemInfo = new ArrayList<>();

    private String restaurantName;
    private  String restaurantKey;


    private final int GALLERY_REQUEST_CODE = 1245;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_menu_manager_side);
        setToolbar();

        restaurantName = getIntent().getStringExtra("RestaurantName");
        restaurantKey = getIntent().getStringExtra("RestaurantKey");

        bitmap = null;

        addFoodItemDialog = new Dialog(this);

        mStorageRef = FirebaseStorage.getInstance().getReference("Images");
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("Restaurant").child(restaurantKey).child("Menu");

        mRecyclerView = (RecyclerView) findViewById(R.id.food_menu_list_manager_side);
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new FoodItemInfoAdapter(getBaseContext(), foodItemInfo, restaurantName);
        mRecyclerView.setAdapter(mAdapter);

        addFoodItemList();

    }

    public void addFoodItemList() {

        mDatabaseRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {


                foodItemInfo.add(new FoodItemInfo(dataSnapshot.getValue(FoodItemInfo.class).getItemName()
                        , dataSnapshot.getValue(FoodItemInfo.class).getItemImage()
                        , dataSnapshot.getValue(FoodItemInfo.class).getItemPrice()
                        , dataSnapshot.getKey()
                        , restaurantKey
                        , dataSnapshot.getValue(FoodItemInfo.class).getItemCalories()));


                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void setToolbar(){
        androidx.appcompat.widget.Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar_food_menu);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back_pressed);

        TextView mTitle = (TextView) myToolbar.findViewById(R.id.toolbar_title_food_menu);
        mTitle.setText("Menu");
    }

    public void onFoodItemAdd(View view) {
        addFoodItemDialog.setContentView(R.layout.dialog_add_food_item);

        Button cancel = (Button)addFoodItemDialog.findViewById(R.id.foodItemCancelButtonDialog);
        Button save = (Button)addFoodItemDialog.findViewById(R.id.foodItemSaveButtonDialog);
        Button addImg = (Button)addFoodItemDialog.findViewById(R.id.addFoodImageButtonDialog);
        imgText = (TextView) addFoodItemDialog.findViewById(R.id.foodImageNamePopUp);
        final EditText foodItemName = (EditText)addFoodItemDialog.findViewById(R.id.dialog_food_item_name);
        final EditText foodItemCalorie = (EditText)addFoodItemDialog.findViewById(R.id.dialog_food_item_calories);
        final EditText foodItemPrice = (EditText)addFoodItemDialog.findViewById(R.id.dialog_food_item_price);
        final ProgressBar pgb = (ProgressBar) addFoodItemDialog.findViewById(R.id.foodItemAddProgressBar);

        pgb.setVisibility(View.INVISIBLE);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addFoodItemDialog.dismiss();
            }
        });

        addImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickFromGallery();
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(bitmap!=null && !TextUtils.isEmpty(foodItemName.getText())){

                    pgb.setVisibility(View.VISIBLE);

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    byte[] imgdata = baos.toByteArray();

                    final StorageReference fileToUpload = mStorageRef.child(imgText.getText().toString().trim());

                    fileToUpload.putBytes(imgdata)
                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                    fileToUpload.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {

                                            String uploadId = mDatabaseRef.push().getKey();

                                            FoodItemInfo info = new FoodItemInfo(foodItemName.getText().toString().trim()
                                                                               , uri.toString()
                                                                               , Double.parseDouble(foodItemPrice.getText().toString().trim())
                                                                               , uploadId
                                                                               , restaurantKey
                                                                               , Integer.parseInt(foodItemCalorie.getText().toString().trim()));

                                            mDatabaseRef.child(uploadId).setValue(info);

                                            mAdapter.notifyDataSetChanged();
                                            foodItemName.setText("");
                                            foodItemCalorie.setText("");
                                            foodItemPrice.setText("");
                                            imgText.setText("");
                                            bitmap = null;

                                            pgb.setVisibility(View.INVISIBLE);

                                            Toast.makeText(getApplicationContext(), "Successfuly uploaded", Toast.LENGTH_LONG).show();
                                        }
                                    });

                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                    pgb.setVisibility(View.INVISIBLE);
                                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                                }
                            });
                }

            }
        });

        //addRestaurantDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        addFoodItemDialog.show();

    }

    private void pickFromGallery(){

        //Create an Intent with action as ACTION_PICK
        Intent intent=new Intent(Intent.ACTION_PICK);

        // Sets the type as image/*. This ensures only components of type image are selected
        intent.setType("image/*");

        //We pass an extra array with the accepted mime types. This will ensure only components with these MIME types as targeted.
        String[] mimeTypes = {"image/jpeg", "image/png"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES,mimeTypes);

        // Launching the Intent
        startActivityForResult(intent, GALLERY_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode,int resultCode,Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        // Result code is RESULT_OK only if the user selects an Image
        if (resultCode == Activity.RESULT_OK)
            switch (requestCode){

                case GALLERY_REQUEST_CODE:

                    //data.getData return the content URI for the selected Image
                    Uri selectedImage = data.getData();
                    String[] filePathColumn = { MediaStore.Images.Media.DATA };

                    // Get the cursor
                    Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);

                    // Move to first row
                    cursor.moveToFirst();

                    //Get the column index of MediaStore.Images.Media.DATA
                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);

                    //Gets the String value in the column
                    String imgDecodableString = cursor.getString(columnIndex);
                    cursor.close();

                    //setting the image name
                    String[] tempName = imgDecodableString.split("/");
                    imgText.setText(tempName[tempName.length - 1]);

                    // Set the Image to bitmap after decoding the String
                    bitmap = BitmapFactory.decodeFile(imgDecodableString);


                    break;

            }
    }


}
