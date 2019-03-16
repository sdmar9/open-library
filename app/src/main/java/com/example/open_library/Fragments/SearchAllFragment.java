package com.example.open_library.Fragments;

import android.content.Context;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
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

import com.example.open_library.Book;
import com.example.open_library.R;
import com.example.open_library.RecyclerAdapter;
import com.example.open_library.dialogs.BookDialogFragment;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class SearchAllFragment extends Fragment  {

    FragmentManager fm;

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    RecyclerAdapter adapter;

    private String LOG_TAG = "FragLog";
    private EditText searchTextBox;
    String searchString;
    private ArrayList<Book> books = new ArrayList<>();

    LatLng latLng = new LatLng(-37.814, 144.96332);

    private Button goButton;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    public SearchAllFragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static SearchAllFragment newInstance(String param1, String param2) {
        SearchAllFragment fragment = new SearchAllFragment();
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
        adapter = new RecyclerAdapter(getContext(), fm);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search_all, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        layoutManager = new LinearLayoutManager(getContext());  //A RecyclerView.LayoutManager implementation which provides similar functionality to ListView.
        recyclerView.setLayoutManager(layoutManager);   // Also StaggeredGridLayoutManager and GridLayoutManager or a custom Layout manager
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();


        goButton = view.findViewById(R.id.goButton);
        searchTextBox = view.findViewById(R.id.searchEditText);

        goButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchString = searchTextBox.getText().toString();
                new GetBookDetails().execute(searchString);
            }
        });


        return view;
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
                        if (currLocation.distanceTo(bookLocation) < 50000) {
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
