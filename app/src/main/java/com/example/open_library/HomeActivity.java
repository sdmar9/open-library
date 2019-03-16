package com.example.open_library;

import android.location.Location;
import android.os.AsyncTask;
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
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class HomeActivity extends AppCompatActivity implements BookDialogFragment.NoticeDialogListener {

    private final String TAG = "HOME_ACTIVITY_DEBUG";
    private FirebaseFirestore mDB = FirebaseFirestore.getInstance();

    private TextView mTextMessage;
    public Book dialogBook;
    private ArrayList<Book> books;
    ArrayList<HashMap> dataRecieved;

    private HomeFragment homeFragment;
    private ShelfFragment shelfFragment;
    private PileFragment pileFragment;
    private RequestFragment requestFragment;
    private ProfileFragment profileFragment;

    public int count;
    private int length;


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

//        getClosestUsers();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        requestBook(user.getUid(), "E77jqepc8nmdUAXxh4Uo");
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
                        new GetBookDetailsIsbn().execute(documentSnapshot.getString("isbn"));
                    }
//                    length = data.size();
                    books = new ArrayList<>();
//                    getBookInfo();
                }
                else {
                    Log.d(TAG, "Data not found");
                }
            }
        });
    }

    public void getClosestBooks(ArrayList<String> user_ids) {
        final ArrayList<HashMap> books = new ArrayList<>();
        final int size = user_ids.size();
        count = 0;
        for (String user_id: user_ids) {
            Log.d(TAG, "getClosestBooks: " + user_id);
            Query query = mDB.collection("user_books").whereEqualTo("uid", user_id);
            query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot documentSnapshot: task.getResult()) {
                            Log.d(TAG, "onComplete: " + documentSnapshot.getString("isbn"));
                            HashMap<String, String> book = new HashMap<>();
                            book.put("id", documentSnapshot.getId());
                            book.put("isbn", documentSnapshot.getString("isbn"));
                            book.put("state", documentSnapshot.getString("state"));
                            books.add(book);
                        }
                        if (count == (size-1)) {
                            Log.d(TAG, "onComplete: Done");
                        }
                    }
                    else {
                        Log.d(TAG, "Data not found");
                    }
                }
            });
            count++;
        }
    }

    public void getClosestUsers() {
        Query query = mDB.collection("users_data");
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                Log.d(TAG, "onComplete: READ DATA");
                ArrayList<String> user_ids = new ArrayList<String>();
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot documentSnapshot: task.getResult()) {
                        Log.d(TAG, "onComplete: " + documentSnapshot.getString("user_id"));
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        if (!documentSnapshot.getString("user_id").equals(user.getUid())) {
                            if (isUserNearby(
                                    Double.valueOf(documentSnapshot.getString("latitude")),
                                    Double.valueOf(documentSnapshot.getString("longitude")),
                                    5000)) {
                                user_ids.add(documentSnapshot.getString("user_id"));
                                Log.d(TAG, "onComplete: " + documentSnapshot.getString("user_id"));
                                getClosestBooks(user_ids);
                            }
                        }
                    }
                }
                else {
                    Log.d(TAG, "Data not found");
                }
            }
        });
    }

    public boolean isUserNearby (double lat, double lng, int radius) {
        double userLat = -37.809665; double userLng = 144.9676664;

        Location currLocation = new Location("Current");
        currLocation.setLatitude(userLat);                          // Set by User during account setup
        currLocation.setLongitude(userLng);

        Location bookLocation = new Location("BookLocation");
        bookLocation.setLatitude(lat);
        bookLocation.setLongitude(lng);

        float distance = currLocation.distanceTo(bookLocation);     // distance in meters
        Log.d(TAG,String.valueOf(distance));

        if (currLocation.distanceTo(bookLocation) < radius) {
            return true;
        }
        return false;
    }

    public void requestBook(String user_id, String bookId) {
        Log.d(TAG, "requestBook: Called");
        Map<String, Object> book = new HashMap<>();
        book.put("state", "requested");

        mDB.collection("user_books").document(bookId).update(book).addOnSuccessListener(new OnSuccessListener<Void>() {
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

        Map<String, Object> request = new HashMap<>();
        request.put("bookId", bookId);
        request.put("user_id", user_id);

        mDB.collection("requests").document().set(request).addOnSuccessListener(new OnSuccessListener<Void>() {
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

    public void shareBook(String bookId) {
        Map<String, Object> book = new HashMap<>();
        book.put("state", "open-to-share");

        mDB.collection("user_books").document(bookId).update(book).addOnSuccessListener(new OnSuccessListener<Void>() {
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

        mDB.collection("request")
                .whereEqualTo("bookId", bookId)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w(TAG, "Listen failed.", e);
                            return;
                        }

                        for (QueryDocumentSnapshot doc : value) {
                            Log.d(TAG, "onEvent: " + doc.getString("user_id"));
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

//    public interface ReadCompleteListener {
//        void onGetBooksk(ArrayList<Book> books);
////        void onDialogNegativeClick(int num);
//    }
//
//    // Use this instance of the interface to deliver action events
//    ReadCompleteListener listener;



    public class GetBookDetailsIsbn extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... strings) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String JSONString = null;
            String searchString = strings[0];
            try {
                // Create URL
                URL url = new URL("https://www.googleapis.com/books/v1/volumes?q=isbn:"+searchString);

                // Create connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();
                InputStream inputStream = urlConnection.getInputStream();

                // Read the response string into a StringBuilder.
                StringBuilder builder = new StringBuilder();

                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    builder.append(line + "\n");
                }

                if (builder.length() == 0) {
                    return null;
                }
                JSONString = builder.toString();

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return JSONString;
        }

        @Override
        protected void onPostExecute(String s) {

            super.onPostExecute(s);
            try {
                JSONObject jsonObject = new JSONObject(s);
                // Get the JSONArray of book items.
                JSONArray itemsArray = jsonObject.getJSONArray("items");

                // Initialize iterator and results fields.
                int i = 0;
                String isbn = null;
                String title = null;
                String authors = null;
                String thumbnailUrl = null;
                String description = null;

                // Look for results in the items array, exiting when both the title and author
                // are found or when all items have been checked.
                while (i < 1 || (authors == null && title == null)) {
                    // Get the current item information.
                    JSONObject book = itemsArray.getJSONObject(i);
                    JSONObject volumeInfo = book.getJSONObject("volumeInfo");
                    JSONObject thumbnailInfo = volumeInfo.getJSONObject("imageLinks");

                    // Try to get the author and title from the current item,
                    // catch if either field is empty and move on.
                    try {
                        JSONArray isbnJSON = volumeInfo.getJSONArray("industryIdentifiers");
                        JSONObject typeObject = isbnJSON.getJSONObject(0); // First key should be isbn 13
                        if (typeObject.getString("type").equals("ISBN_10")) {
                            try {typeObject = isbnJSON.getJSONObject(1);}
                            catch (Exception e) {e.printStackTrace();}
                        }

                        isbn = typeObject.getString("identifier");
                        title = volumeInfo.getString("title");

                        JSONArray authorsJSON = volumeInfo.getJSONArray("authors");
                        authors = authorsJSON.getString(0);

                        thumbnailUrl = thumbnailInfo.getString("thumbnail");
                        description = volumeInfo.getString("description");

                        Book newBook = new Book(isbn,title,authors,thumbnailUrl,description,new LatLng(0,0));
                        books.add(newBook);
                        shelfFragment.adapter.books = books;
                        shelfFragment.adapter.notifyDataSetChanged();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    // Move to the next item.
                    i++;
                }
                Log.d(TAG, "onPostExecute: Books" + books.size());
            } catch (Exception e){
                e.printStackTrace();
            }
        }


    }

}
