package com.example.fsc_diner.controller;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.example.fsc_diner.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Pattern;

import static java.util.Locale.*;

public class UserRegistration extends AppCompatActivity {
    TextView firstNameTV, lastNameTV, emailTV, passwordTV, confirmPasswordTV;
    FirebaseAuth mFirebaseAuth;
    OnCompleteListener onCompleteListener;
    // Password pattern is created using Regular Expressions to be used in password validation method
    public static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^" +
                    "(?=.*[0-9])" +         //1 digit minimum
                    "(?=.*[a-z])" +         //1 lowercase letter minimum
                    "(?=.*[A-Z])" +         //1 lowercase letter maximum
                    "(?=.*[@#$%^&+=])" +    //1 special character minimum
                    "(?=\\S+$)" +           //no spaces allowed
                    ".{7,}" );              //7 character minimum
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_registration);
        mFirebaseAuth = FirebaseAuth.getInstance();
        firstNameTV = findViewById(R.id.firstNameRegistration);
        lastNameTV = findViewById(R.id.lastNameRegistration);
        emailTV = findViewById(R.id.emailRegistration);
        passwordTV = findViewById(R.id.passwordRegistration);
        confirmPasswordTV = findViewById(R.id.confirmPasswordRegistration);
        onCompleteListener = new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if (!task.isSuccessful()) {
                    Toast.makeText(UserRegistration.this, "SignUp Unsuccessful, Please Try Again", Toast.LENGTH_SHORT).show();
                } else {
                    String email = emailTV.getText().toString();
                    String firstName = firstNameTV.getText().toString();
                    String lastName = lastNameTV.getText().toString();
                    String currentDate = new SimpleDateFormat("MM/dd/yyyy", getDefault()).format(new Date());
                    UserInformation user = new UserInformation(email, firstName, lastName, "Customer", currentDate);
                    FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(user);
                    Intent i = new Intent(UserRegistration.this, LoginActivity.class);
                    startActivity(i);
                    Animatoo.animateFade(UserRegistration.this);
                    finish();
                }
            }
        };
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(this, LoginActivity.class);
        startActivity(i);
        // Animation effect is used
        Animatoo.animateFade(this);
        finish();
    }

    public void completeRegistration(View view) {
        final String email = emailTV.getText().toString();
        final String password = confirmPasswordTV.getText().toString();

        if (allConditionsMet() == true) {
            mFirebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, onCompleteListener);
        }

    }

    //****************************************************
    // Method: validateFirstName
    //
    // Purpose: Method used to validate the first name
    // input provided by the user. The user's first name
    // must meet a few requirements to return a true
    // value.
    //****************************************************
    public boolean validateFirstName() {
        if (firstNameTV.getText().toString().equals("")) {
            firstNameTV.setError("Missing first name field");
            return false;
        } else if (firstNameTV.getText().toString().length() <= 3 && firstNameTV.getText().toString().length() > 0) {
            firstNameTV.setError("First name must be at least 3 characters long");
            return false;
        } else if (firstNameTV.getText().toString().length() > 30) {
            firstNameTV.setError("First name must be no longer than 30 characters");
            return false;
        } else {
            firstNameTV.setError(null);
            return true;
        }
    }

    //****************************************************
    // Method: validateLastName
    //
    // Purpose: Method used to validate the last name
    // input provided by the user. The user's last name
    // must meet requirement to return a true
    // value.
    //****************************************************
    public boolean validateLastName() {
        if (lastNameTV.getText().toString().equals("")) {
            lastNameTV.setError("Missing last name field");
            return false;
        } else {
            lastNameTV.setError(null);
            return true;
        }
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
        String confirmedPassword = confirmPasswordTV.getText().toString().trim();

        if (password.isEmpty() && confirmedPassword.isEmpty()) {
            passwordTV.setError("Missing password field");
            confirmPasswordTV.setError("Missing confirmed password field");
            return false;
        } else if (password.isEmpty() && confirmedPassword.isEmpty() == false) {
            passwordTV.setError("Missing password field");
            confirmPasswordTV.setError(null);
            return false;
        } else if (confirmedPassword.isEmpty() && password.isEmpty() == false) {
            confirmPasswordTV.setError("Missing password field");
            passwordTV.setError(null);
            return false;
        } else if (!PASSWORD_PATTERN.matcher(password).matches()) {
            passwordTV.setError("Password must: " +
                    "\n- Be at least seven characters long" +
                    "\n- Have at least one capital letter" +
                    "\n- Have at least one lowercase letter" +
                    "\n- Have at least one number" +
                    "\n- Have at least one special character");
            return false; //change this later on
        } else if (!password.equals(confirmedPassword)) {
            confirmPasswordTV.setError("Passwords do not match");
            return false;
        } else {
            passwordTV.setError(null);
            confirmPasswordTV.setError(null);
            return true;
        }
    }

    //****************************************************
    // Method: allConditionsMet
    //
    // Purpose: Method used to validate that all validation
    // methods are returned true to ensure that the user
    // inputs proper credentials and information.
    //****************************************************
    public boolean allConditionsMet() {
        validateFirstName();
        validateLastName();
        validateEmail();
        validatePassword();

        if (validateFirstName() == true && validateLastName() == true
                && validateEmail() == true && validatePassword() == true) {
            return true;
        } else {
            return false;
        }
    }

    public void employeeRegistration(View view){
        Intent i = new Intent(UserRegistration.this, EmployeeRegistration.class);
        startActivity(i);
        Animatoo.animateSlideUp(this);
        finish();
    }
}
