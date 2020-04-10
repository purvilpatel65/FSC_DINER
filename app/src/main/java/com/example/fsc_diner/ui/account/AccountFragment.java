package com.example.fsc_diner.ui.account;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.fsc_diner.controller.LogOutDialog;
import com.example.fsc_diner.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AccountFragment extends Fragment {

    private AccountViewModel accountViewModel;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_account, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Button logOut = view.findViewById(R.id.logOutButton);
        final TextView emailTV = view.findViewById(R.id.account_settings_email);
        final TextView firstNameTV = view.findViewById(R.id.account_settings_first_name);
        final TextView joinDateTV = view.findViewById(R.id.account_user_type);
        final TextView lastNameTV = view.findViewById(R.id.account_settings_last_name);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String email = dataSnapshot.child("email").getValue().toString();
                String firstName = dataSnapshot.child("firstName").getValue().toString();
                String lastName = dataSnapshot.child("lastName").getValue().toString();
                String userType = dataSnapshot.child("userType").getValue().toString();

                emailTV.setText(email);
                firstNameTV.setText(firstName);
                lastNameTV.setText(lastName);
                joinDateTV.setText(userType);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                // Create and show the dialog.
                LogOutDialog dialog = new LogOutDialog();
                dialog.show(ft, "dialog");
            }
        });
    }
}
