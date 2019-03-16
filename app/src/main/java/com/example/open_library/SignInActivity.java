package com.example.open_library;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.common.api.GoogleApi;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class SignInActivity extends AppCompatActivity {

    private final String TAG = "SIGN_IN_DEBUG";
    private FirebaseAuth mAuth;

    private EditText emailEditExt;
    private EditText passwordEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        setTitle("Sign In");

        emailEditExt = findViewById(R.id.emailEditText);
        passwordEdit = findViewById(R.id.passwordEditText);

        mAuth = FirebaseAuth.getInstance();
        signOut();
    }

    public void onSignIn(View view) {
        String email = emailEditExt.getText().toString();
        String password = passwordEdit.getText().toString();
        signIn(email, password);
    }

    public void goToSignUpIntent(View view) {
        Intent intent = new Intent(this, SignUpActivity.class);
        startActivity(intent);
    }

    private void currentUser() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            Log.d(TAG, user.getDisplayName() + " logged in");
            //signOut();
        }
        else {
            Log.d(TAG, "No user logged in");
        }
    }

    private void signUp(String email, String password, final String displayName) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "Sign Up Success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null) {
                                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                        .setDisplayName(displayName).build();

                                user.updateProfile(profileUpdates);
                            }
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "Sign Up Failed", task.getException());
                        }
                    }
                });
    }

    private void signIn(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "Sign In Success");
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "Sign In Failed", task.getException());
                        }
                    }
                });
    }

    private void signOut() {
        FirebaseAuth.getInstance().signOut();
        Log.d(TAG, "Signed out");
    }
}
