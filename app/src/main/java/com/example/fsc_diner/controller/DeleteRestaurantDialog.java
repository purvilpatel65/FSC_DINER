package com.example.fsc_diner.controller;

import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.example.fsc_diner.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class DeleteRestaurantDialog extends DialogFragment {

    private String restaurantNameStr;
    private EditText restaurantNameET;
    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_delete_restaurant_dialog, container, false);
        Button deleteButton = view.findViewById(R.id.deleteRestaurantButton);
        Button cancelButton = view.findViewById(R.id.cancelButtonDialog);
        restaurantNameET = view.findViewById(R.id.delete_dialog_restaurant_name);
        restaurantNameStr = getArguments().getString("RestaurantName");
        final String restaurantKeyStr = getArguments().getString("RestaurantKey");
        mStorageRef = FirebaseStorage.getInstance().getReference(restaurantKeyStr).child("coverPhoto");
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("Restaurant").child(restaurantKeyStr);

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(matchingName()){
                    mStorageRef.delete();
                    mDatabaseRef.removeValue();
                    getDialog().dismiss();
                    Toast.makeText(getActivity().getApplicationContext(), "Restaurant Deletion was Successful", Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(getActivity().getApplicationContext(), "Restaurant Deletion was Unsuccessful", Toast.LENGTH_LONG).show();
                }
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

    private boolean matchingName(){
        if(restaurantNameET.getText().toString().equals(restaurantNameStr)){
            return true;
        }else{
            restaurantNameET.setError("The wrong restaurant name was entered");
            return false;
        }
    }
}
