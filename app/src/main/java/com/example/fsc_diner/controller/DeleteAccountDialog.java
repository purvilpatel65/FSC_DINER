package com.example.fsc_diner.controller;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.fsc_diner.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class DeleteAccountDialog extends DialogFragment {

    private Button confirmDeleteAccountButton;
    private TextView closeFragment;
    private TextView enterEmailTV;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_delete_account_dialog, container, false);
        confirmDeleteAccountButton = view.findViewById(R.id.confirmDeleteAccountButton);
        closeFragment = view.findViewById(R.id.closeDAFragmentTV);
        enterEmailTV = view.findViewById(R.id.enterEmailDeleteAccountTV);

        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        final StorageReference storageReference = firebaseStorage.getReference("profilePictures").child(user.getUid());
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getUid());

        confirmDeleteAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(user != null && confirmEmail()){
                    user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful())
                            {
                                FirebaseAuth.getInstance().signOut();
                                Intent i = new Intent(getActivity(), LoginActivity.class);
                                startActivity(i);
                                getActivity().finish();
                                databaseReference.removeValue();
                                storageReference.delete();
                                //getDialog().dismiss();
                                Toast.makeText(getActivity().getApplicationContext(), R.string.account_delete_success, Toast.LENGTH_SHORT).show();
                            }
                            else
                            {
                                Toast.makeText(getActivity().getApplicationContext(), R.string.account_delete_unsuccessful, Toast.LENGTH_SHORT).show();
                                getDialog().dismiss();
                            }
                        }
                    });
                }
            }
        });

        closeFragment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });
        return view;
    }

    @Override
    public void onActivityCreated(Bundle arg0) {
        super.onActivityCreated(arg0);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        getDialog().getWindow().setGravity(Gravity.CENTER);
    }

    public boolean confirmEmail() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userEmail = user.getEmail();
        if(userEmail.equals(enterEmailTV.getText().toString()))
        {
            return true;
        }
        else
        {
            return false;
        }
    }
}
