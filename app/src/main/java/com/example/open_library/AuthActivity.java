package com.example.open_library;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class AuthActivity extends AppCompatActivity {

    private final String TAG = "AUTH_DEBUG";
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        setTitle("Auth");

        mAuth = FirebaseAuth.getInstance();
        SignUp("ahmed.nadim59@gmail.com", "password", "nadim");
        //CurrentUser();
        //SignIn("ahmed.nadim59@gmail.com", "password");
    }

    private void CurrentUser() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            Log.d(TAG, user.getDisplayName() + " logged in");
            //SignOut();
        }
        else {
            Log.d(TAG, "No user logged in");
        }
    }

    private void SignUp(String email, String password, final String displayName) {
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

    private void SignIn(String email, String password) {
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

    private void SignOut() {
        FirebaseAuth.getInstance().signOut();
        Log.d(TAG, "Signed out");
    }
}
