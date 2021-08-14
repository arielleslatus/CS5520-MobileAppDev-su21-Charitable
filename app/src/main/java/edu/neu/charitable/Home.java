package edu.neu.charitable;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import edu.neu.charitable.fragments.Search;
import edu.neu.charitable.fragments.Settings;
import edu.neu.charitable.fragments.Timeline;


/**
 * Parent activity for main fragments
 */
public class Home extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(navListener);

        // as soon as the application opens the first
        // fragment should be shown to the user
        // in this case it is algorithm fragment
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new Timeline()).commit();
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            // By using switch we can easily get
            // the selected fragment
            // by using there id.
            Fragment selectedFragment = null;
            switch (item.getItemId()) {
                case R.id.search:
                    selectedFragment = new Search();
                    break;
                case R.id.timeline:
                    selectedFragment = new Timeline();
                    break;
                case R.id.settings:
                    selectedFragment = new Settings();
                    //startActivity(new Intent(Home.this, SecurityPreferences.class));

                    break;
            }
            // It will help to replace the
            // one fragment to other.
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, selectedFragment)
                    .commit();
            return true;
        }
    };

    public void openProfile(View view) {
        startActivity(new Intent(this , ProfileActivity.class));
    }

    public void openDonation(View view) {
        Intent intent = new Intent(this, DonateDummy.class);
        startActivity(intent);
    }

    public void openPool(View view) {
        Intent intent = new Intent(this, DonateDummy.class);
        intent.putExtra("AUTOFILL_CHARITY", "Charitable Pool Direct");
        intent.putExtra("AUTOFILL_AMOUNT", Double.toString(10.00));
        startActivity(intent);
    }

}