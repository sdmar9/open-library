package com.example.open_library;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DbActivity extends AppCompatActivity {

    private final String TAG = "DB_DEBUG";
    private FirebaseFirestore mDB = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_db);

        setTitle("DB");

        SaveDataExample();
        //QueryDataExample();
    }

    private void SaveDataExample() {
        Map<String, Object> test = new HashMap<>();
        test.put("message", "Hello World");

        mDB.collection("tests").document().set(test).addOnSuccessListener(new OnSuccessListener<Void>() {
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

    // More examples for queries at https://firebase.google.com/docs/firestore/query-data/queries
    private void QueryDataExample() {
        Query query = mDB.collection("tests").whereEqualTo("message", "Hello World");
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot documentSnapshot: task.getResult()) {
                        Log.d(TAG, documentSnapshot.getId());
                    }
                }
                else {
                    Log.d(TAG, "Data not found");
                }
            }
        });
    }
}
