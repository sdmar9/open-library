package com.example.open_library;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.open_library.dialogs.BookDialogFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class HomeActivity extends AppCompatActivity implements BookDialogFragment.NoticeDialogListener {

    private final String TAG = "HOME_ACTIVITY_DEBUG";
    private FirebaseFirestore mDB = FirebaseFirestore.getInstance();

    private TextView mTextMessage;
    public Book dialogBook;

    private HomeFragment homeFragment;
    private ShelfFragment shelfFragment;
    private PileFragment pileFragment;
    private RequestFragment requestFragment;
    private ProfileFragment profileFragment;


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.nav_home:
                    getSupportFragmentManager().beginTransaction().replace(R.id.base_frame, homeFragment, "homeFragment").addToBackStack("homeFragment").commit();
                    return true;
                case R.id.nav_books:
                    getSupportFragmentManager().beginTransaction().replace(R.id.base_frame, shelfFragment, "shelfFragment").addToBackStack("shelfFragment").commit();
                    return true;
                case R.id.nav_pile:
                    getSupportFragmentManager().beginTransaction().replace(R.id.base_frame, pileFragment, "pileFragment").addToBackStack("pileFragment").commit();
                    return true;
                case R.id.nav_requests:
                    getSupportFragmentManager().beginTransaction().replace(R.id.base_frame, requestFragment, "requestFragment").addToBackStack("requestFragment").commit();
                    return true;
                case R.id.nav_profile:
                    getSupportFragmentManager().beginTransaction().replace(R.id.base_frame, profileFragment, "profileFragment").addToBackStack("profileFragment").commit();
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        homeFragment = new HomeFragment();
        shelfFragment = new ShelfFragment();
        pileFragment = new PileFragment();
        requestFragment = new RequestFragment();
        profileFragment = new ProfileFragment();


        getSupportFragmentManager().beginTransaction()
                .add(R.id.base_frame, homeFragment, "homeFragment")
                .addToBackStack("viewFragment")
                .commit();
    }


    public void addBook(String uid, String isbn, String state) {
        Map<String, Object> book = new HashMap<>();
        book.put("uid", uid);
        book.put("isbn", isbn);
        book.put("state", state);

        mDB.collection("user_books").document().set(book).addOnSuccessListener(new OnSuccessListener<Void>() {
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
    }

    public void read(String uid) {
        // Get all books under this uid
        // return {ISBN, state}
        Query query = mDB.collection("user_books").whereEqualTo("uid", uid);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                Log.d(TAG, "onComplete: READ DATA");
                ArrayList<HashMap> data = new ArrayList<HashMap>();
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot documentSnapshot: task.getResult()) {
                        Log.d(TAG, "onComplete: " + documentSnapshot.getString("isbn"));
                        HashMap<String, String> book = new HashMap<>();
                        book.put("isbn", documentSnapshot.getString("isbn"));
                        book.put("state", documentSnapshot.getString("state"));
                        data.add(book);
                    }
                }
                else {
                    Log.d(TAG, "Data not found");
                }
            }
        });
    }

    @Override
    public void onDialogPositiveClick(String isbn) {
        Log.d("DiagTag", "ISBN: " + isbn);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        addBook(user.getUid(), isbn, "None");
    }
}
