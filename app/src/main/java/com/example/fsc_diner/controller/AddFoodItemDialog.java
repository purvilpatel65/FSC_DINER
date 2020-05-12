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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fsc_diner.R;
import static android.app.Activity.RESULT_OK;
import com.example.fsc_diner.model.FoodItemInfo;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class AddFoodItemDialog extends DialogFragment {

    private TextView imgText;
    private Uri selectedImage;
    private String restaurantName;
    private String restaurantKey;
    private boolean imageSelected;
    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef;
    private final int GALLERY_REQUEST_CODE = 1245;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_add_food_item, container, false);
        Button cancel = view.findViewById(R.id.foodItemCancelButtonDialog);
        Button save = view.findViewById(R.id.foodItemSaveButtonDialog);
        Button addImg = view.findViewById(R.id.addFoodImageButtonDialog);
        imgText = view.findViewById(R.id.foodImageNamePopUp);
        final EditText foodItemName = view.findViewById(R.id.dialog_food_item_name);
        final EditText foodItemCalorie = view.findViewById(R.id.dialog_food_item_calories);
        final EditText foodItemPrice = view.findViewById(R.id.dialog_food_item_price);

        restaurantName = getArguments().getString("RestaurantName");
        restaurantKey = getArguments().getString("RestaurantKey");
        imageSelected = false;
        //mStorageRef = FirebaseStorage.getInstance().getReference("Menu").child(restaurantKeyStr).child(menuItemKeyStr);
        mStorageRef = FirebaseStorage.getInstance().getReference(restaurantKey).child("Menu");
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("Restaurant").child(restaurantKey).child("Menu");

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

                if (imageSelected && selectedImage != null && !TextUtils.isEmpty(foodItemName.getText())) {

                    final String uploadId = mDatabaseRef.push().getKey();
                    final StorageReference fileToUpload = mStorageRef.child(uploadId);

                    fileToUpload.putFile(selectedImage)
                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                    fileToUpload.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {

                                            FoodItemInfo info = new FoodItemInfo(foodItemName.getText().toString().trim()
                                                    , uri.toString()
                                                    , Double.parseDouble(foodItemPrice.getText().toString().trim())
                                                    , uploadId
                                                    , restaurantKey
                                                    , Integer.parseInt(foodItemCalorie.getText().toString().trim()));

                                            mDatabaseRef.child(uploadId).setValue(info);

                                            foodItemName.setText("");
                                            foodItemCalorie.setText("");
                                            foodItemPrice.setText("");
                                            imgText.setText("");
                                            imageSelected = false;
                                            selectedImage = null;

                                            Toast.makeText(getActivity().getApplicationContext(), "Successfully uploaded", Toast.LENGTH_LONG).show();
                                            getDialog().dismiss();
                                        }
                                    });
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                    Toast.makeText(getActivity().getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
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

    private void pickFromGallery() {

        //Create an Intent with action as ACTION_PICK
        Intent intent = new Intent(Intent.ACTION_PICK);

        // Sets the type as image/*. This ensures only components of type image are selected
        intent.setType("image/*");

        //We pass an extra array with the accepted mime types. This will ensure only components with these MIME types as targeted.
        String[] mimeTypes = {"image/jpeg", "image/png"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);

        // Launching the Intent
        startActivityForResult(intent, GALLERY_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result code is RESULT_OK only if the user selects an Image
        if (requestCode == GALLERY_REQUEST_CODE && data != null)
            if (resultCode == RESULT_OK) {
                //data.getData return the content URI for the selected Image
                imageSelected = true;
                selectedImage = data.getData();
                imgText.setText("Image Status: Image Selected!");
            }
    }
}
