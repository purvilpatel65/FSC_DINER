package com.example.fsc_diner.controller;

import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.fsc_diner.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class DeleteFoodItemDialog extends DialogFragment {

    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.delete_food_item_dialog, container, false);
        Button deleteButton = view.findViewById(R.id.deleteMenuItemButton);
        Button cancelButton = view.findViewById(R.id.cancelButtonDialog);

        final String restaurantKeyStr = getArguments().getString("RestaurantKeyCurrent");
        final String menuItemKeyStr = getArguments().getString("ItemKey");

        mStorageRef = FirebaseStorage.getInstance().getReference(restaurantKeyStr).child("Menu").child(menuItemKeyStr);
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("Restaurant").child(restaurantKeyStr).child("Menu").child(menuItemKeyStr);

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mStorageRef.delete();
                mDatabaseRef.removeValue();
                getDialog().dismiss();
                Toast.makeText(getActivity().getApplicationContext(), "Menu Item Deletion was Successful!", Toast.LENGTH_LONG).show();
            }
        });
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDialog().dismiss();
            }
        });
        return view;
    }

    @Override
    public void onActivityCreated(Bundle arg0) {
        super.onActivityCreated(arg0);
        //getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        getDialog().getWindow().setGravity(Gravity.CENTER);
    }
}
