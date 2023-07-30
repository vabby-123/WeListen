package com.example.welisten;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.welisten.Models.Users;
import com.example.welisten.databinding.ActivityRegistrationBinding;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.FirebaseDatabase;

public class RegistrationActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseDatabase database;
    private ActivityRegistrationBinding binding;
    private GoogleSignInClient mGoogleSignInClient;
//  private String name, phone, email, password;
//    private Boolean validateUsername() {
//
//
//        name = binding.Name.getText().toString();
//        String noWhiteSpace = "\\A\\w{4,20}\\z";
//        if (name.isEmpty()) {
//            binding.Name.setError("Field cannot be empty");
//            return false;
//        } else if (name.length() >= 15) {
//            binding.Name.setError("Username too long");
//            return false;
//        } else if (name.length() < 4) {
//            binding.Name.setError("Username too short");
//            return false;
//        }
//       else if (!name.matches(noWhiteSpace)) {
//            binding.Name.setError("White Spaces are not allowed");
//            return false;
//        } else {
//            return true;
//        }
//    }
//
//
//    private Boolean validateEmail() {
//
//        email = binding.Email.getText().toString();
//        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
//        if (email.isEmpty()) {
//            binding.Email.setError("Field cannot be empty");
//            return false;
//        } else if (!email.matches(emailPattern)) {
//            binding.Email.setError("Invalid email address");
//            return false;
//        } else {
//            return true;
//        }
//    }
//
//
//    private Boolean validatePhoneNo() {
//
//
//        phone = binding.Phone.getText().toString();
//        if (phone.isEmpty()) {
//            binding.Phone.setError("Field cannot be empty");
//            return false;
//        }
//        else if(phone.length()!=10){
//            binding.Phone.setError("Invalid Phone no.");
//            return false;
//        }
//        else {
//            return true;
//        }
//    }
//
//
//    private Boolean validatePassword() {
//
//
//        password = binding.Password.getText().toString();
//        if (password.isEmpty()) {
//            binding.Password.setError("Field cannot be empty");
//            return false;
//        }
//        else if (!password.matches(".{4,}")) {
//            binding.Password.setError("at least 4 characters");
//            return false;
//        }
//        else if (!password.matches("^(?=.*[0-9]).*$")) {
//            binding.Password.setError("at least 1 digit");
//            return false;
//        }
//        else if (!password.matches("^(?=.*[a-zA-Z]).*$")) {
//            binding.Password.setError("any letter");
//            return false;
//        }
//        else if (!password.matches("^(?=.*[a-z]).*$")) {
//            binding.Password.setError("at least 1 lower case letter");
//            return false;
//        }
//        else if (!password.matches("^(?=.*[A-Z]).*$")) {
//            binding.Password.setError("at least 1 upper case letter");
//            return false;
//        }
//        else if (!password.matches("^(?=.*[@#$%^&+=]).*$")) {
//            binding.Password.setError("at least 1 special character");
//            return false;
//        }
//        else if (!password.matches("^(?=\\S+$).*$")) {
//            binding.Password.setError("no white spaces");
//            return false;
//        }
//        else {
//            return true;
//        }
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegistrationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        );

        getSupportActionBar().hide();

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();


        binding.registerbtn.setOnClickListener((View view)-> {


            emailSignup();

        });


        binding.googlebtn.setOnClickListener((View view) -> {

            googleSignup();
            Intent homeIntent=new Intent(RegistrationActivity.this , HomeActivity.class);
            startActivity(homeIntent);

        });
        
        binding.facebookbtn.setOnClickListener((View view) -> {
            facebookSignin();
        });

        binding.login.setOnClickListener((View v) -> {

                Intent intent = new Intent(RegistrationActivity.this , SignInActivity.class);
                startActivity(intent);

            }
            );
    }

    private void facebookSignin() {
    }

    private void emailSignup() {

        //              if (validateUsername() && validateEmail() && validatePhoneNo() && validatePassword()){
        auth.createUserWithEmailAndPassword(
                binding.Email.getText().toString() , binding.Password.getText().toString()
        ).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful()){
                    //replace binding with String
                    Users users= new Users(binding.Name.getText().toString(), binding.Email.getText().toString(),
                            binding.Password.getText().toString());

                    String id = task.getResult().getUser().getUid();
                    database.getReference().child("Users").child(id).setValue(users);
                    Toast.makeText(RegistrationActivity.this, "Registration Successful",Toast.LENGTH_SHORT).show();
                    Intent intent=new Intent(RegistrationActivity.this , HomeActivity.class);
                    startActivity(intent);

                }
                else{
                    Toast.makeText(RegistrationActivity.this,task.getException().getMessage() ,Toast.LENGTH_SHORT).show();
                }
            }
        });
        //               }

    }

    private void googleSignup() {

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        Intent intent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(intent, 2);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch ( requestCode){
            case 2:
            try {
                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                GoogleSignInAccount account = task.getResult(ApiException.class);
                String idToken = account.getIdToken();
                AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);

                auth.signInWithCredential(credential)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {

                                if(task.isSuccessful()){
                                    FirebaseUser user = auth.getCurrentUser();
                                    Users users = new Users();
                                    users.setUserId(user.getUid());
                                    users.setUserName(user.getDisplayName());
                                    users.setEmail(user.getEmail());
                                    users.setProfilepic(user.getPhotoUrl().toString());
                                    database.getReference().child("Users").child(users.getUserId()).setValue(users);

                                    Toast.makeText(RegistrationActivity.this, "Registration Successful",Toast.LENGTH_SHORT).show();

                                }
                            }
                        });
            } catch (ApiException e) {
                throw new RuntimeException(e);
            }

            break;
        }

    }

}