package com.example.open_library;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import android.location.LocationListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity //implements LocationListener
{

    private final String TAG = "SIGN_UP_DEBUG";
    private FirebaseAuth mAuth;

    private EditText emailEditText;
    private EditText passwordEditText;
    private EditText contactEditText;
    private EditText displayNameEditText;
//    private LocationManager mLocationManager;
//    private Location mLastLocation;
    private FirebaseFirestore mDB = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        setTitle("Sign Up");

        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        contactEditText = findViewById(R.id.contactEditText);
        displayNameEditText = findViewById(R.id.displayNameEditText);

        mAuth = FirebaseAuth.getInstance();
//        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
//                || ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
//            Log.d(TAG, "onCreate: Location Granted");
//            try{
//                mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//                mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
//            }
//            catch (SecurityException e) {
//                e.printStackTrace();
//            }
//
//
//            if (mLastLocation == null) {
//                Log.d(TAG, "onCreate: Location not shared");
//            }
//            else {
//                Log.d(TAG, "onCreate: Location shared");
//            }
//        }
    }

//    @Override
//    public void onLocationChanged(Location location) {
//        mLastLocation = location;
//    }
//
//    @Override
//    public void onStatusChanged(String provider, int status, Bundle extras) {
//
//    }
//
//    @Override
//    public void onProviderEnabled(String provider) {
//
//    }
//
//    @Override
//    public void onProviderDisabled(String provider) {
//
//    }

    public void onSignUp(View view) {
        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        String contactDetails = contactEditText.getText().toString();
        String displayNameText = displayNameEditText.getText().toString();

        signUp(email, password, displayNameText, contactDetails);
    }

    private void signUp(String email, String password, final String displayName, final String contactDetails) {
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

//                                if (mLastLocation != null) {
                                    Map<String, Object> user_data = new HashMap<>();
                                    user_data.put("user_id", user.getUid());
//                                    user_data.put("latitude", String.valueOf(mLastLocation.getLatitude()));
//                                    user_data.put("longitude", String.valueOf(mLastLocation.getLongitude()));
                                    user_data.put("latitude", "-37.809665");
                                    user_data.put("longitude", "144.9676664");
                                    user_data.put("contactDetails", contactDetails);

                                    mDB.collection("users_data").document().set(user_data).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.d(TAG, "Data saved");
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.d(TAG, "Data not saved");
                                        }
                                    });
//                                }
//                                else {
//                                    Log.d(TAG, "onComplete: Null");
//                                }
                            }
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "Sign Up Failed", task.getException());
                        }
                    }
                });
    }
}
