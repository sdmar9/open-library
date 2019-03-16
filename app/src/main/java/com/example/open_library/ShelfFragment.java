package com.example.open_library;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.open_library.Adapters.ShelfAdapter;
import com.example.open_library.Fragments.SearchAllFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class ShelfFragment extends Fragment {

    FragmentManager fm;

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    ShelfAdapter adapter;

    private Button addBookButton;
    private SearchAllFragment searchFragment;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ShelfFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static ShelfFragment newInstance(String param1, String param2) {
        ShelfFragment fragment = new ShelfFragment();
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
        adapter = new ShelfAdapter(getContext(), fm);

        searchFragment = new SearchAllFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_shelf, container, false);
        addBookButton = view.findViewById(R.id.addBookButton);
        Button getStuff = view.findViewById(R.id.getStuffButton);

        recyclerView = view.findViewById(R.id.recyclerView);
        layoutManager = new LinearLayoutManager(getContext());  //A RecyclerView.LayoutManager implementation which provides similar functionality to ListView.
        recyclerView.setLayoutManager(layoutManager);   // Also StaggeredGridLayoutManager and GridLayoutManager or a custom Layout manager
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        ((HomeActivity)getActivity()).read(user.getUid());

        addBookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.base_frame, searchFragment, "searchFragment").addToBackStack("searchFragment").commit();
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        ((HomeActivity)getActivity()).read(user.getUid());
    }


}
