package com.example.fsc_diner.ui.account;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.fsc_diner.controller.DeleteAccountDialog;
import com.example.fsc_diner.controller.LogOutDialog;
import com.example.fsc_diner.R;
import com.example.fsc_diner.controller.ResetPasswordDialog;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;
import static androidx.constraintlayout.widget.Constraints.TAG;

public class AccountFragment extends Fragment {

    private final int GALLERY_REQUEST_CODE = 1245;
    private CircleImageView profilePictureIV;
    private Uri imageURI;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_account, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Button logOut = view.findViewById(R.id.logOutButton);
        Button changePasswordButton = view.findViewById(R.id.changePasswordButton);
        final TextView deleteAccountTV = view.findViewById(R.id.deleteAccountTV);
        final TextView emailTV = view.findViewById(R.id.account_settings_email);
        final TextView firstNameTV = view.findViewById(R.id.account_settings_first_name);
        final TextView userTypeTV= view.findViewById(R.id.account_user_type);
        final TextView lastNameTV = view.findViewById(R.id.account_settings_last_name);
        final TextView joinDateTV = view.findViewById(R.id.account_member_since);
        profilePictureIV = view.findViewById(R.id.userProfilePicture);


        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        user.getProviderData();
        if(user != null){
            if(user.getPhotoUrl() != null){
                StorageReference storageReference = FirebaseStorage.getInstance().getReference();
                storageReference.child("profilePictures").child(user.getUid()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.get().load(uri).into(profilePictureIV);
                    }
                });
            }
        }

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try{
                    String email = dataSnapshot.child("email").getValue().toString();
                    String firstName = dataSnapshot.child("firstName").getValue().toString();
                    String lastName = dataSnapshot.child("lastName").getValue().toString();
                    String userType = dataSnapshot.child("userType").getValue().toString();
                    String joinDate = dataSnapshot.child("joinDate").getValue().toString();

                    emailTV.setText(email);
                    firstNameTV.setText(firstName);
                    lastNameTV.setText(lastName);
                    userTypeTV.setText(userType);
                    joinDateTV.setText(joinDate);
                }catch (Exception e){
                    emailTV.setText("");
                    firstNameTV.setText("");
                    lastNameTV.setText("");
                    userTypeTV.setText("");
                    joinDateTV.setText("");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        profilePictureIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickFromGallery();

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

        changePasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                // Create and show the dialog.
                ResetPasswordDialog dialog = new ResetPasswordDialog();
                dialog.show(ft, "dialog");
            }
        });

        deleteAccountTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                // Create and show the dialog.
                DeleteAccountDialog dialog = new DeleteAccountDialog();
                dialog.show(ft, "dialog");
            }
        });
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
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == GALLERY_REQUEST_CODE && data != null){
            switch (resultCode){
                case RESULT_OK:
                    imageURI = data.getData();
                    Picasso.get().load(imageURI).into(profilePictureIV);
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();


                    FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
                    StorageReference storageReference = firebaseStorage.getReference("profilePictures").child(user.getUid());
                    storageReference.putFile(imageURI);

                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder().setPhotoUri(imageURI).build();

                    user.updateProfile(profileUpdates)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Log.d(TAG, "User profile updated.");
                                    }
                                }
                            });
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
    }
}
