package com.example.open_library;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;

public class HomeActivity extends AppCompatActivity {

    private TextView mTextMessage;

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

}
