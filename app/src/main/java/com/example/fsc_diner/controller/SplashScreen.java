package com.example.fsc_diner.controller;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.example.fsc_diner.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SplashScreen extends AppCompatActivity {

    FirebaseAuth mFireBaseAuth;
    private int progressStatus = 0;
    private ProgressBar progressBar;
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mFireBaseAuth = FirebaseAuth.getInstance();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        progressBar = findViewById(R.id.progressBar);
    }

    private void updateUI(FirebaseUser user) {

        if(user != null) {

            DatabaseReference mDatabaseRef = FirebaseDatabase.getInstance().getReference("Users").child(user.getUid());
            mDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    String type = dataSnapshot.child("userType").getValue(String.class);
                    String resKey = dataSnapshot.child("restaurantKey").getValue(String.class);
                    String fullName = dataSnapshot.child("fullName").getValue(String.class);

                    if(type.equals("Customer")){
                        Intent i = new Intent(SplashScreen.this, MainActivity.class);
                        startActivity(i);
                        //Animatoo.animateFade(getApplicationContext());
                        finish();
                    }else if (type.equals("Manager")) {
                        Intent i = new Intent(SplashScreen.this, MainActivityManagerSide.class);
                        startActivity(i);
                        //Animatoo.animateFade(getApplicationContext());
                        finish();
                    } else if (type.equals("Employee")) {
                        Intent i = new Intent(SplashScreen.this, MainActivityEmployee.class);
                        i.putExtra("ResKey", resKey);
                        i.putExtra("EmpName", fullName);
                        startActivity(i);
                        //Animatoo.animateFade(getApplicationContext());
                        finish();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        final FirebaseUser currentUser = mFireBaseAuth.getCurrentUser();
        if (currentUser != null) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    updateUI(currentUser);
                }
            }, 3600);

        }else {
            // Splash Screen intent is performed with a duration of 3 seconds
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(new Intent(SplashScreen.this, LoginActivity.class));
                    Animatoo.animateZoom(SplashScreen.this);
                    finish();
                }
            }, 3600);

        }
    }
}
