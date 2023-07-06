package com.example.welisten;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

public class RegistrationActivity extends AppCompatActivity {

private String name, phone, email, password;
    private Boolean validateUsername() {

        EditText regUsername = findViewById(R.id.editTextName);
        name = regUsername.getText().toString();
        String noWhiteSpace = "\\A\\w{4,20}\\z";
        if (name.isEmpty()) {
            regUsername.setError("Field cannot be empty");
            return false;
        } else if (name.length() >= 15) {
            regUsername.setError("Username too long");
            return false;
        } else if (name.length() < 4) {
            regUsername.setError("Username too short");
            return false;
        }
       else if (!name.matches(noWhiteSpace)) {
            regUsername.setError("White Spaces are not allowed");
            return false;
        } else {
            return true;
        }
    }


    private Boolean validateEmail() {

        EditText regEmail = findViewById(R.id.editTextEmail);
        email = regEmail.getText().toString();
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        if (email.isEmpty()) {
            regEmail.setError("Field cannot be empty");
            return false;
        } else if (!email.matches(emailPattern)) {
            regEmail.setError("Invalid email address");
            return false;
        } else {
            return true;
        }
    }


    private Boolean validatePhoneNo() {

        EditText regPhoneNo = findViewById(R.id.editTextPhone);
        phone = regPhoneNo.getText().toString();
        regPhoneNo.getText().toString();
        if (phone.isEmpty()) {
            regPhoneNo.setError("Field cannot be empty");
            return false;
        } else {
            return true;
        }
    }


    private Boolean validatePassword() {

        EditText regPassword = findViewById(R.id.editTextPassword);
        password = regPassword.getText().toString();
        if (password.isEmpty()) {
            regPassword.setError("Field cannot be empty");
            return false;
        }
        else if (!password.matches(".{4,}")) {
            regPassword.setError("at least 4 characters");
            return false;
        }
        else if (!password.matches("^(?=.*[0-9]).*$")) {
            regPassword.setError("at least 1 digit");
            return false;
        }
        else if (!password.matches("^(?=.*[a-zA-Z]).*$")) {
            regPassword.setError("any letter");
            return false;
        }
        else if (!password.matches("^(?=.*[a-z]).*$")) {
            regPassword.setError("at least 1 lower case letter");
            return false;
        }
        else if (!password.matches("^(?=.*[A-Z]).*$")) {
            regPassword.setError("at least 1 upper case letter");
            return false;
        }
        else if (!password.matches("^(?=.*[@#$%^&+=]).*$")) {
            regPassword.setError("at least 1 special character");
            return false;
        }
        else if (!password.matches("^(?=\\S+$).*$")) {
            regPassword.setError("no white spaces");
            return false;
        }
        else {
            return true;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        );

        getSupportActionBar().hide();


        AppCompatButton registerbtn;

        registerbtn = findViewById(R.id.register);

        registerbtn.setOnClickListener((View view)-> {


                if (validateUsername() && validateEmail() && validatePhoneNo() && validatePassword()){
                    Toast.makeText(RegistrationActivity.this, "Registration Successful" ,Toast.LENGTH_SHORT).show();
                }

        });


        View login = findViewById(R.id.login);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(RegistrationActivity.this, "Login page" ,Toast.LENGTH_SHORT).show();

            }
        });
    }


}