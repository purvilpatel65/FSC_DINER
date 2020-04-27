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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Pattern;

public class EmployeeRegistration extends AppCompatActivity {
    TextView firstNameTV, lastNameTV, emailTV, passwordTV, confirmPasswordTV, registrationCodeTV;
    FirebaseAuth mFirebaseAuth;
    String empPW, manPW;
    OnCompleteListener onCompleteListener;
    // Password pattern is created using Regular Expressions to be used in password validation method
    public static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^" +
                    "(?=.*[0-9])" +         //1 digit minimum
                    "(?=.*[a-z])" +         //1 lowercase letter minimum
                    "(?=.*[A-Z])" +         //1 lowercase letter maximum
                    "(?=.*[@#$%^&+=])" +    //1 special character minimum
                    "(?=\\S+$)" +           //no spaces allowed
                    ".{7,}" +               //7 character minimum
                    "$");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_registration);
        mFirebaseAuth = FirebaseAuth.getInstance();
        firstNameTV = findViewById(R.id.employeeRegFirstName);
        lastNameTV = findViewById(R.id.employeeRegLastName);
        emailTV = findViewById(R.id.employeeRegEmail);
        passwordTV = findViewById(R.id.employeeRegPassword);
        confirmPasswordTV = findViewById(R.id.employeeRegConfirmPassword);
        registrationCodeTV = findViewById(R.id.employeeRegCode);

        onCompleteListener = new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if (!task.isSuccessful()) {
                    Toast.makeText(EmployeeRegistration.this, "SignUp Unsuccessful, Please Try Again", Toast.LENGTH_SHORT).show();
                } else if(task.isSuccessful() && validateCode().equals("emp")){
                    String email = emailTV.getText().toString();
                    String firstName = firstNameTV.getText().toString();
                    String lastName = lastNameTV.getText().toString();
                    String currentDate = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault()).format(new Date());
                    UserInformation employee = new UserInformation(email, firstName, lastName, "Employee", currentDate);
                    FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(employee);
                    Intent i = new Intent(EmployeeRegistration.this, LoginActivity.class);
                    startActivity(i);
                    Animatoo.animateFade(EmployeeRegistration.this);
                    finish();
                } else if(task.isSuccessful() && validateCode().equals("man")){
                    String email = emailTV.getText().toString();
                    String firstName = firstNameTV.getText().toString();
                    String lastName = lastNameTV.getText().toString();
                    UserInformation employee = new UserInformation(email, firstName, lastName, "Manager");
                    FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(employee);
                    Intent i = new Intent(EmployeeRegistration.this, LoginActivity.class);
                    startActivity(i);
                    Animatoo.animateFade(EmployeeRegistration.this);
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

        if (allConditionsMet()) {
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
            return false;
        } else if (!password.equals(confirmedPassword)) {
            confirmPasswordTV.setError("Passwords do not match");
            return false;
        } else {
            passwordTV.setError(null);
            confirmPasswordTV.setError(null);
            return true;
        }
    }

    public String validateCode() {
        String code = registrationCodeTV.getText().toString();
        if(code.equals(empPW)){
            registrationCodeTV.setError(null);
            return "emp";
        }else if(code.equals(manPW)){
            registrationCodeTV.setError(null);
            return "man";
        }else{
            registrationCodeTV.setError("Incorrect Registration Code");
            return "";
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
        validateCode();

        if (validateFirstName() && validateLastName()
                && validateEmail()  && validatePassword()  &&
                (validateCode().equals("emp") || validateCode().equals("man"))) {
            return true;
        } else {
            return false;
        }
    }

    public void returnToLogin(View view){
        Intent i = new Intent(EmployeeRegistration.this, LoginActivity.class);
        startActivity(i);
        Animatoo.animateFade(this);
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Registration");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                empPW = dataSnapshot.child("Employee").getValue().toString();
                manPW = dataSnapshot.child("Manager").getValue().toString();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });
    }
}
