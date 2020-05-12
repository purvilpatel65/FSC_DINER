package com.example.fsc_diner.controller;

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

import java.util.regex.Pattern;

public class ResetPasswordDialog extends DialogFragment {

    private TextView enterPasswordChangeTV;
    private TextView confirmPasswordChangeTV;

    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^" +
                    "(?=.*[0-9])" +         //1 digit minimum
                    "(?=.*[a-z])" +         //1 lowercase letter minimum
                    "(?=.*[A-Z])" +         //1 lowercase letter maximum
                    "(?=.*[@#$%^&+=])" +    //1 special character minimum
                    "(?=\\S+$)" +           //no spaces allowed
                    ".{7,}" +               //7 character minimum
                    "$");


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_change_password, container, false);
        Button resetPwButton = view.findViewById(R.id.updatePasswordButton);
        TextView closeFragment = view.findViewById(R.id.closePWFragmentTV);
        enterPasswordChangeTV = view.findViewById(R.id.enterPasswordChangeTV);
        confirmPasswordChangeTV = view.findViewById(R.id.confirmPasswordChangeTV);

        resetPwButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if(user != null && validatePassword()){
                    user.updatePassword(enterPasswordChangeTV.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful())
                            {
                                getDialog().dismiss();
                                Toast.makeText(getActivity().getApplicationContext(), R.string.success_password_change, Toast.LENGTH_SHORT).show();
                            }
                            else
                            {
                                Toast.makeText(getActivity().getApplicationContext(), R.string.unsuccessful_password_change, Toast.LENGTH_SHORT).show();
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

    public boolean validatePassword() {
        String password = enterPasswordChangeTV.getText().toString().trim();
        String confirmedPassword = confirmPasswordChangeTV.getText().toString().trim();

        if (password.isEmpty() && confirmedPassword.isEmpty()) {
            enterPasswordChangeTV.setError("Missing password field");
            confirmPasswordChangeTV.setError("Missing confirmed password field");
            return false;
        } else if (password.isEmpty() && !confirmedPassword.isEmpty()) {
            enterPasswordChangeTV.setError("Missing password field");
            confirmPasswordChangeTV.setError(null);
            return false;
        } else if (confirmedPassword.isEmpty() && !password.isEmpty()) {
            confirmPasswordChangeTV.setError("Missing password field");
            enterPasswordChangeTV.setError(null);
            return false;
        } else if (!PASSWORD_PATTERN.matcher(password).matches()) {
            enterPasswordChangeTV.setError("Password must: " +
                    "\n- Be at least seven characters long" +
                    "\n- Have at least one capital letter" +
                    "\n- Have at least one lowercase letter" +
                    "\n- Have at least one number" +
                    "\n- Have at least one special character");
            return false; //change this later on
        } else if (!password.equals(confirmedPassword)) {
            confirmPasswordChangeTV.setError("Passwords do not match");
            return false;
        } else {
            enterPasswordChangeTV.setError(null);
            confirmPasswordChangeTV.setError(null);
            return true;
        }
    }
}
