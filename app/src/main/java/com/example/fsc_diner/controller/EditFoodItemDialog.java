package com.example.fsc_diner.controller;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fsc_diner.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import static android.app.Activity.RESULT_OK;

public class EditFoodItemDialog extends DialogFragment {

    private TextView imgText;
    private boolean imageSelected;
    private Uri selectedImage;
    private EditText menuItemName;
    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef;
    private final int GALLERY_REQUEST_CODE = 1234;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.dialog_edit_food_item, container,false);
        Button cancel = view.findViewById(R.id.cancelButtonDialog);
        Button save = view.findViewById(R.id.saveButtonDialog);
        Button addImg = view.findViewById(R.id.EditImageButtonDialog);
        imgText = view.findViewById(R.id.imageNamePopUp);
        menuItemName = view.findViewById(R.id.edit_dialog_food_item_name);

        final String restaurantKeyStr = getArguments().getString("RestaurantKeyCurrent");
        final String menuItemNameStr = getArguments().getString("ItemName");
        final String menuItemKeyStr = getArguments().getString("ItemKey");

        menuItemName.setText(menuItemNameStr);
        mStorageRef = FirebaseStorage.getInstance().getReference(restaurantKeyStr).child("Menu").child(menuItemKeyStr);
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("Restaurant").child(restaurantKeyStr).child("Menu").child(menuItemKeyStr);

        imageSelected = false;

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDialog().dismiss();
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

                if(imageSelected == false && selectedImage == null && !TextUtils.isEmpty(menuItemName.getText())){
                    mDatabaseRef.child("itemName").setValue(menuItemName.getText().toString());

                    Toast.makeText(getActivity().getApplicationContext(), "Restaurant Information has been updated", Toast.LENGTH_LONG).show();
                    getDialog().dismiss();
                } else if(imageSelected && selectedImage != null && !TextUtils.isEmpty(menuItemName.getText())){

                    //final StorageReference fileToUpload = mStorageRef;

                    mStorageRef.putFile(selectedImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            mStorageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    mDatabaseRef.child("itemName").setValue(menuItemName.getText().toString());
                                    mDatabaseRef.child("itemImage").setValue(uri.toString());
                                    imageSelected = false;
                                    selectedImage = null;

                                    Toast.makeText(getActivity().getApplicationContext(), "Menu Item Information has been updated", Toast.LENGTH_LONG).show();
                                    getDialog().dismiss();
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getActivity().getApplicationContext(), "An error has occurred", Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        });
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        getDialog().getWindow().setGravity(Gravity.CENTER);
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
        if (requestCode == GALLERY_REQUEST_CODE && data !=null)
            switch (resultCode){

                case RESULT_OK:
                    //data.getData return the content URI for the selected Image
                    selectedImage = data.getData();
                    imageSelected = true;

                    imgText.setText("Image Status: Updated!");
            }
    }
}
