package com.example.open_library;

import android.content.Context;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.open_library.Adapters.PileAdapter;
import com.example.open_library.Adapters.ShelfAdapter;
import com.example.open_library.Fragments.SearchAllFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
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
import java.util.Arrays;
import java.util.HashMap;


public class PileFragment extends Fragment {
    private String LOG_TAG = "FragLog";
    FragmentManager fm;
    private FirebaseFirestore mDB = FirebaseFirestore.getInstance();

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    PileAdapter adapter;
    int count = 0;

    private EditText searchTextBox;
    String searchString;
    public ArrayList<Book> books = new ArrayList<>(Arrays.asList(
            new Book("0","Book 1", "Akhila", "http://books.google.com/books/content?id=F1wgqlNi8AMC&printsec=frontcover&img=1&zoom=1&edge=curl&source=gbs_api","",new LatLng(-37.814, 144.96332)),
            new Book("1","Book 2", "Author 2", "http://books.google.com/books/content?id=8Pr_kLFxciYC&printsec=frontcover&img=1&zoom=1&edge=curl&source=gbs_api","",new LatLng(-37.814, 144.96332)),
            new Book("2","Communism is the best", "Nadim", "http://books.google.com/books/content?id=LRlCAAAAYAAJ&printsec=frontcover&img=1&zoom=5&source=gbs_api","",new LatLng(-37.814, 144.96332)),
            new Book("3","Book 4", "Akhila", "http://books.google.com/books/content?id=PDTD2hPNcjAC&printsec=frontcover&img=1&zoom=1&edge=curl&source=gbs_api","",new LatLng(-37.814, 144.96332)),
            new Book("4","Trains! Trains! Trains!", "Surit", "http://books.google.com/books/content?id=Co89jQRg4_oC&printsec=frontcover&img=1&zoom=5&edge=curl&source=gbs_api","",new LatLng(-37.814, 144.96332)),
            new Book("5","Hannibal", "Akhila", "http://books.google.com/books/content?id=jQxIAQAAMAAJ&printsec=frontcover&img=1&zoom=5&source=gbs_api","",new LatLng(-37.814, 144.96332))
    ));

    LatLng latLng = new LatLng(-37.814, 144.96332);
    private Button goButton;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public PileFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static PileFragment newInstance(String param1, String param2) {
        PileFragment fragment = new PileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        fm = getFragmentManager();
        adapter = new PileAdapter(getContext(), fm);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_pile, container, false);
        recyclerView = view.findViewById(R.id.recyclerView);
        layoutManager = new LinearLayoutManager(getContext());  //A RecyclerView.LayoutManager implementation which provides similar functionality to ListView.
        recyclerView.setLayoutManager(layoutManager);   // Also StaggeredGridLayoutManager and GridLayoutManager or a custom Layout manager
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        goButton = view.findViewById(R.id.searchButton);
        searchTextBox = view.findViewById(R.id.searchEditText);

        getClosestUsers();
        goButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchString = searchTextBox.getText().toString();
                //new GetBookDetails().execute(searchString);
                getClosestUsers();
                Log.d(LOG_TAG, "onClick: ");
            }
        });

        return view;
    }

    public void getClosestBooks(ArrayList<String> user_ids) {
        books = new ArrayList<>();
        final int size = user_ids.size();
        count = 0;
        for (String user_id: user_ids) {
            Log.d(LOG_TAG, "getClosestBooks: " + user_id);
            Query query = mDB.collection("user_books").whereEqualTo("uid", user_id).whereEqualTo("state", "available");
            query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot documentSnapshot: task.getResult()) {
                            Log.d(LOG_TAG, "onComplete: " + documentSnapshot.getString("isbn"));
                            HashMap<String, String> book = new HashMap<>();
                            book.put("id", documentSnapshot.getId());
                            book.put("isbn", documentSnapshot.getString("isbn"));
                            book.put("state", documentSnapshot.getString("state"));
                            new GetBookDetailsIsbn().execute(documentSnapshot.getString("isbn"), documentSnapshot.getId());
                        }
                        if (count == (size-1)) {
                            Log.d(LOG_TAG, "onComplete: Done");
                        }
                    }
                    else {
                        Log.d(LOG_TAG, "Data not found");
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
                Log.d(LOG_TAG, "onComplete: READ DATA");
                ArrayList<String> user_ids = new ArrayList<String>();
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot documentSnapshot: task.getResult()) {
                        Log.d(LOG_TAG, "onComplete: " + documentSnapshot.getString("user_id"));
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        if (!documentSnapshot.getString("user_id").equals(user.getUid())) {
                            if (isUserNearby(
                                    Double.valueOf(documentSnapshot.getString("latitude")),
                                    Double.valueOf(documentSnapshot.getString("longitude")),
                                    5000)) {
                                user_ids.add(documentSnapshot.getString("user_id"));
                                Log.d(LOG_TAG, "onComplete: " + documentSnapshot.getString("user_id"));
                                getClosestBooks(user_ids);
                            }
                        }
                    }
                }
                else {
                    Log.d(LOG_TAG, "Data not found");
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
        Log.d(LOG_TAG,String.valueOf(distance));

        if (currLocation.distanceTo(bookLocation) < radius) {
            return true;
        }
        return false;
    }

    private boolean isInBooks(Book book) {
        int N = books.size();
        for (int i = 0; i<N; i++) {
            Book currentBook = books.get(i);
            if (currentBook.getIsbn().equals(book.getIsbn())) {
                return true;
            }
        }
        return false;
    }

    public class GetBookDetailsIsbn extends AsyncTask<String, String, ArrayList<String>> {
        @Override
        protected ArrayList<String> doInBackground(String... strings) {
            ArrayList<String> returnStrings = new ArrayList<>();
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String JSONString = null;
            String searchString = strings[0];
            String bookID = strings[1];
            returnStrings.add(bookID);
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
            returnStrings.add(JSONString);
            return returnStrings;
        }

        @Override
        protected void onPostExecute(ArrayList<String> strings) {


            super.onPostExecute(strings);
            String s = strings.get(1);
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
                        if (!isInBooks(newBook)) {
                            newBook.setBookId(strings.get(0));
                            books.add(newBook);
                            adapter.books = books;
                            adapter.notifyDataSetChanged();
                        }
//                        Log.d(TAG, "onPostExecute: InBooks - " + isInBooks(newBook));

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    // Move to the next item.
                    i++;
                }
                Log.d(LOG_TAG, "onPostExecute: Books" + books.size());
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    private class GetBookDetails extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... strings) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String JSONString = null;
            try {
                // Create URL
                final String BOOK_BASE_URL = "https://www.googleapis.com/books/v1/volumes?";

                final String QUERY_PARAM = "q"; // Parameter for the search string.

                String searchString = strings[0];
                Uri builtURI = Uri.parse(BOOK_BASE_URL).buildUpon()
                        .appendQueryParameter(QUERY_PARAM, searchString)
                        .build();
                URL url = new URL(builtURI.toString());

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
                while (i < 10 || (authors == null && title == null)) {
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

                        Book newBook = new Book(isbn,title,authors,thumbnailUrl,description,latLng);

                        Location currLocation = new Location("Current");
                        currLocation.setLatitude(-37.814);      // Set by User during account setup
                        currLocation.setLongitude(144.96332);

                        Location bookLocation = new Location("BookLocation");
                        bookLocation.setLatitude(latLng.latitude);
                        bookLocation.setLongitude(latLng.longitude);

                        float distance = currLocation.distanceTo(bookLocation);
                        Log.d(LOG_TAG,String.valueOf(distance));
                        if (currLocation.distanceTo(bookLocation) < 5000) {
                            books.add(newBook);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    // Move to the next item.
                    i++;
                }
                adapter.books = books;
                adapter.notifyDataSetChanged();

            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }

}
