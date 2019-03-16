package com.example.open_library;

import android.content.Context;
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
import com.example.open_library.Adapters.RequestsAdapter;
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


public class RequestFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    String TAG = "RequestFragment";
    private FirebaseFirestore mDB = FirebaseFirestore.getInstance();
    public ArrayList<Book> books = new ArrayList<>();
    FragmentManager fm;

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    RequestsAdapter adapter;
    int count = 0;

    private EditText searchTextBox;
    String searchString;


    LatLng latLng = new LatLng(-37.814, 144.96332);
    private Button goButton;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public RequestFragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static RequestFragment newInstance(String param1, String param2) {
        RequestFragment fragment = new RequestFragment();
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
        adapter = new RequestsAdapter(getContext(), fm);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pile, container, false);
        recyclerView = view.findViewById(R.id.recyclerView);
        layoutManager = new LinearLayoutManager(getContext());  //A RecyclerView.LayoutManager implementation which provides similar functionality to ListView.
        recyclerView.setLayoutManager(layoutManager);   // Also StaggeredGridLayoutManager and GridLayoutManager or a custom Layout manager
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        goButton = view.findViewById(R.id.searchButton);
        searchTextBox = view.findViewById(R.id.searchEditText);

        goButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchString = searchTextBox.getText().toString();
                //new GetBookDetails().execute(searchString);
                getRequests();
                Log.d("", "onClick: ");
            }
        });

        return view;
    }

    public void getRequests() {
        Log.d(TAG, "getRequests: ");
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        Query query = mDB.collection("user_books").whereEqualTo("uid", user.getUid());
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "isSuccessful: ");
                    for (QueryDocumentSnapshot documentSnapshot: task.getResult()) {
                        Log.d(TAG, documentSnapshot.getId());
                        final String isbn = documentSnapshot.getString("isbn");
                        final String bookId = documentSnapshot.getString("bookId");
                        Query query = mDB.collection("requests").whereEqualTo("bookId", documentSnapshot.getId());
                        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    Log.d(TAG, "isSuccessful2: ");
                                    for (QueryDocumentSnapshot documentSnapshot: task.getResult()) {
                                        Log.d(TAG, documentSnapshot.getId());
                                        HashMap<String, String> request = new HashMap<>();
                                        request.put("id", documentSnapshot.getId());
                                        request.put("bookId", bookId);
                                        request.put("isbn", isbn);
                                        request.put("requester", documentSnapshot.getString("user_id"));
                                        //mRequests.add(request);
                                        new GetBookDetailsIsbn().execute(isbn, documentSnapshot.getId());
                                    }
                                }
                                else {
                                    Log.d(TAG, "Data not found");
                                }
                            }
                        });
                    }
                }
                else {
                    Log.d(TAG, "Data not found");
                }
            }
        });
    }

    public class GetBookDetailsIsbn extends AsyncTask<String, String, ArrayList<String>> {
        @Override
        protected ArrayList<String> doInBackground(String... strings) {
            ArrayList<String> returnStrings = new ArrayList<>();
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String JSONString = null;
            String searchString = strings[0];
            String state = strings[1];
            returnStrings.add(state);
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
            try {
                String s = strings.get(1);
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
                            newBook.setState(strings.get(0));
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
                Log.d(TAG, "onPostExecute: Books" + books.size());
            } catch (Exception e){
                e.printStackTrace();
            }
        }
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
}
