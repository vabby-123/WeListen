package com.example.welisten;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.welisten.Models.Users;
import com.example.welisten.databinding.ActivitySigninBinding;
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

public class SignInActivity extends AppCompatActivity {

    ActivitySigninBinding binding;
    FirebaseAuth auth;
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseDatabase database;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySigninBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        auth = FirebaseAuth.getInstance();

        binding.signinbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                auth.signInWithEmailAndPassword(binding.Email.getText().toString() , binding.Password.getText().toString())
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()){
                                    Intent intent = new Intent(SignInActivity.this , HomeActivity.class);
                                    startActivity(intent);
                                }
                                else {
                                    Toast.makeText(SignInActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });

        //if user is already login then we go directly to the home activity
        if(auth.getCurrentUser()!=null){
            Intent intent = new Intent(SignInActivity.this , HomeActivity.class);
            startActivity(intent);
        }

        binding.googlebtn.setOnClickListener((View view) -> {

            googleSignup();
            Intent homeIntent=new Intent(SignInActivity.this , HomeActivity.class);
            startActivity(homeIntent);

        });

        binding.facebookbtn.setOnClickListener((View view) -> {
            facebookSignin();
        });
        binding.register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignInActivity.this, RegistrationActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }

    private void facebookSignin() {
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

                                        Toast.makeText(SignInActivity.this, "Registration Successful",Toast.LENGTH_SHORT).show();

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