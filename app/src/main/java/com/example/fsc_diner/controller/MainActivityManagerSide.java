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
import com.example.fsc_diner.controller.adapter.RestaurantInfoAdapter;
import com.example.fsc_diner.model.RestaurantInfo;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class MainActivityManagerSide extends AppCompatActivity {

    private Dialog addRestaurantDialog;
    private TextView imgText;
    private Bitmap bitmap;

    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef;
    private FirebaseAuth mAuth;

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private List<RestaurantInfo> resInfo = new ArrayList<>();


    private final int GALLERY_REQUEST_CODE = 1234;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_manager_side);
        setToolbar();
        bitmap = null;

        addRestaurantDialog = new Dialog(this);

        mStorageRef = FirebaseStorage.getInstance().getReference("Images");
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("Restaurant");
        mAuth = FirebaseAuth.getInstance();

        mRecyclerView = (RecyclerView) findViewById(R.id.menu_list_chef_side);
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new RestaurantInfoAdapter(getBaseContext(), resInfo);
        mRecyclerView.setAdapter(mAdapter);


        addRestaurantList();

    }

    private void addRestaurantList(){


        mDatabaseRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {


                    resInfo.add(new RestaurantInfo(dataSnapshot.getValue(RestaurantInfo.class).getRestaurantname()
                            , dataSnapshot.getValue(RestaurantInfo.class).getRestaurantImage(), dataSnapshot.getKey()));


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
        androidx.appcompat.widget.Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        TextView mTitle = (TextView) myToolbar.findViewById(R.id.toolbar_title);
        mTitle.setText("Restaurants");

        ((TextView)myToolbar.findViewById(R.id.main_activity_toolbar_logout)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();

                Intent i = new Intent(v.getContext(), LoginActivity.class);
                Toast.makeText(v.getContext(), R.string.logout4, Toast.LENGTH_SHORT).show();
                startActivity(i);
            }
        });
    }

    public void onRestaurantAdd(View view) {
        addRestaurantDialog.setContentView(R.layout.dialog_add_restaurant);

        Button cancel = addRestaurantDialog.findViewById(R.id.cancelButtonDialog);
        Button save = addRestaurantDialog.findViewById(R.id.saveButtonDialog);
        Button addImg = addRestaurantDialog.findViewById(R.id.addImageButtonDialog);
        imgText = addRestaurantDialog.findViewById(R.id.imageNamePopUp);
        final EditText restaurantName = addRestaurantDialog.findViewById(R.id.dialog_restaurant_name);
        final ProgressBar pgb = addRestaurantDialog.findViewById(R.id.progressBar);

        pgb.setVisibility(View.INVISIBLE);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addRestaurantDialog.dismiss();
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

                if(bitmap!=null && !TextUtils.isEmpty(restaurantName.getText())){

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

                                            String uploadKey = mDatabaseRef.push().getKey();

                                            RestaurantInfo info = new RestaurantInfo(restaurantName.getText().toString().trim()
                                                                                   , uri.toString(), uploadKey);

                                            mDatabaseRef.child(uploadKey).setValue(info);

                                            mAdapter.notifyDataSetChanged();
                                            restaurantName.setText("");
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
        addRestaurantDialog.show();

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
