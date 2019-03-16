package com.example.open_library;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private final String TAG = "MAIN_DEBUG";
    private String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        FirebaseAuth.getInstance().signOut();
        Intent intent;
        if (currentUser()) {
            intent = new Intent(this, HomeActivity.class);
            intent.putExtra("uid", uid);
        }  else {
            intent = new Intent(this, SignInActivity.class);
        }

        startActivity(intent);

    }


    private boolean currentUser() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            Log.d(TAG, user.getDisplayName() + " logged in");
            uid = user.getUid();
            //signOut();
            return true;
        }
        else {
            Log.d(TAG, "No user logged in");
            return false;
        }
    }
}
