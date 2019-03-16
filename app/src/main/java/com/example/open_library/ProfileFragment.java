package com.example.open_library;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;


public class ProfileFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private FirebaseAuth mAuth;
    private ImageView profilePic;
    private TextView screenNameView;
    private TextView emailView;
    private TextView contactInfoView;

    private String screenName;
    private String email;
    private String contactInfo;

    public ProfileFragment() {
        // Required empty public constructor
    }

    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
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

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        screenName = user.getDisplayName();
        email = user.getEmail();
        contactInfo = user.getPhoneNumber();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        profilePic = view.findViewById(R.id.profilePic);
        Picasso.get().load("https://i1.rgstatic.net/ii/profile.image/280055327543301-1443781801998_Q512/Md_Ahmed15.jpg")
                .resize(300, 300)
                .transform(new CropCircleTransformation())
                .into(profilePic);

        screenNameView = view.findViewById(R.id.screenNameView);
        emailView = view.findViewById(R.id.emailView);
        contactInfoView = view.findViewById(R.id.contactInfoView);

        screenNameView.setText(screenName);
        emailView.setText(email);
        contactInfoView.setText(contactInfo);

        return view;
    }
}
