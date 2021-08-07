package edu.neu.charitable;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Homepage extends AppCompatActivity {

    private DatabaseReference database;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);

        //this.auth = FirebaseAuth.getInstance();
        //this.database = FirebaseDatabase.getInstance().getReference();

        Toolbar myToolbar = findViewById(R.id.toolbar);                         // Get the toolbar.
        myToolbar.setTitleTextColor(getResources().getColor(R.color.white));    // Change toolbar text to white.
        setSupportActionBar(myToolbar);                                         // Set support action bar to custom toolbar.
        getSupportActionBar().setTitle("CHARITABLE");                           // Set text (page title) in toolbar.
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_items, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.profile_page:
                startActivity(new Intent(this, ProfileActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


}