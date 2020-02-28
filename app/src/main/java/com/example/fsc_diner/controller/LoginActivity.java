package com.example.fsc_diner.controller;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fsc_diner.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "FACELOG";
    public static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^" +
                    "(?=.*[0-9])" +         //1 digit minimum
                    "(?=.*[a-z])" +         //1 lowercase letter minimum
                    "(?=.*[A-Z])" +         //1 lowercase letter maximum
                    "(?=.*[@#$%^&+=])" +    //1 special character minimum
                    "(?=\\S+$)" +           //no spaces allowed
                    ".{7,}" +               //7 character minimum
                    "$");

    TextView emailTV, passwordTV;
    FirebaseAuth mFireBaseAuth;
    OnCompleteListener onCompleteListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        emailTV = findViewById(R.id.emailLogin);
        passwordTV = findViewById(R.id.passwordLogin);
        mFireBaseAuth = FirebaseAuth.getInstance();

        onCompleteListener = new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if (!task.isSuccessful()) {
                    Toast.makeText(LoginActivity.this, R.string.sign_in_unsuccessful, Toast.LENGTH_SHORT).show();
                } else {
                    FirebaseUser currentUser = mFireBaseAuth.getCurrentUser();
                    Toast.makeText(LoginActivity.this, R.string.sign_in_successful, Toast.LENGTH_SHORT).show();
                    updateUI(currentUser);
                }
            }
        };
    }


    public void verifyLogin(View view) {
        validateEmail();
        validatePassword();

        String email = emailTV.getText().toString();
        String password = passwordTV.getText().toString();

        if (validateEmail() && validatePassword()) {
            mFireBaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(LoginActivity.this, onCompleteListener);
        }
    }

    public void registrationStart(View view) {
        Intent i = new Intent(this, RegistrationActivity.class);
        startActivity(i);
        finish();
    }

    //****************************************************
    // Method: validateEmail
    //
    // Purpose: Method used to validate the email input
    // provided by the user. The email must meet several
    // requirements to return a true value.
    //****************************************************
    public boolean validateEmail() {
        String email = emailTV.getText().toString().trim();

        if (email.isEmpty()) {
            emailTV.setError("Missing email field");
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailTV.setError("Please enter a valid email address");
            return false;
        } else {
            emailTV.setError(null);
            return true;
        }
    }

    //****************************************************
    // Method: validatePassword
    //
    // Purpose: Method used to validate the password input
    // provided by the user. Password must meet several
    // requirements to return a true value.
    //****************************************************
    public boolean validatePassword() {
        String password = passwordTV.getText().toString().trim();

        if (password.isEmpty()) {
            passwordTV.setError("Missing password field");
            return false;
        } else if (!PASSWORD_PATTERN.matcher(password).matches()) {
            passwordTV.setError("Password must: " +
                    "\n- Be at least seven characters long" +
                    "\n- Have at least one capital letter" +
                    "\n- Have at least one lowercase letter" +
                    "\n- Have at least one number" +
                    "\n- Have at least one special character");
            return false;
        } else {
            passwordTV.setError(null);
            return true;
        }
    }

    public void updateUI(FirebaseUser user) {

        if (user != null) {
            Intent i = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(i);
            finish();
        } else {
            recreate();
            finish();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mFireBaseAuth.getCurrentUser();
        if (currentUser != null) {
            updateUI(currentUser);
        }
    }
}
