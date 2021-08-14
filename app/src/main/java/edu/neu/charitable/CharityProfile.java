package edu.neu.charitable;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import edu.neu.charitable.models.Post;
import edu.neu.charitable.utils.CharityProfileRecyclerViewAdapter;


public class CharityProfile extends AppCompatActivity {

    String charityName;
    String charityLocation;
    String charityMission;
    String uidLoggedIn;
    String charityID;
    int logoId;

    // Firebase references to retrieve data about the charity, and donations to it
    private DatabaseReference referenceCharitiesDB;
    private DatabaseReference referenceDonationsDB;
    private DatabaseReference referenceUsersDB;

    String TAG = "CharityProfile DebugAlice ";

    List<Post> posts;

    RecyclerView recyclerView;
    CharityProfileRecyclerViewAdapter charityAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.charity_profile);

        // Get values for the main data - who is viewing this charity,
        // and what charity are they viewing?
        uidLoggedIn = FirebaseAuth.getInstance().getCurrentUser().getUid();
        charityID = getIntent().getExtras().getString("charityID");

        // Get the path from firebase.
        this.referenceCharitiesDB = FirebaseDatabase.getInstance().getReference("Charities");
        this.referenceDonationsDB = FirebaseDatabase.getInstance().getReference("user_donations");
        this.referenceUsersDB = FirebaseDatabase.getInstance().getReference("Users");


        // Get info about charity then populate the layout
        retrieveCharityData();

        // Find the ID for our recyclerview
        recyclerView = findViewById(R.id.charityProfileRecyclerView);

        // Initialize the content of the feed
        posts = new ArrayList<>();
        charityAdapter = new CharityProfileRecyclerViewAdapter(posts);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(charityAdapter);

        Log.d(TAG, "Ok, we initialized recyclerview. About to load transactions.");
        loadTransactions();
        Log.d(TAG, "Done loading transactions.");



    }

    public void onButtonClick(View v) {

        switch (v.getId()) {
            case R.id.donateButton:
                Intent intent = new Intent(v.getContext(), DonateDummy.class);
                intent.putExtra("AUTOFILL_CHARITY", charityName);
                v.getContext().startActivity(intent);
        }
    }


    private void retrieveCharityData(){

        this.referenceCharitiesDB.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "Retrieving Charity Data to Populate CharityProfile for: " + charityID);
                Object charityDataSnapshot = dataSnapshot.getValue(Object.class);
                if (charityDataSnapshot != null) {

                    Log.d(TAG, "snapshot: " + charityDataSnapshot.toString().substring(0,300));

                    // Examine snapshot & retrieve this charity's info via the ID
                    HashMap<String,Object> charityDB = (HashMap<String,Object>) charityDataSnapshot;
                    HashMap<String,String> curCharity = (HashMap<String,String>) charityDB.get(charityID);

                    charityName = curCharity.get("name");
                    charityLocation = curCharity.get("city") + ", " + curCharity.get("state") + ", "
                            + curCharity.get("country");
                    charityMission = curCharity.get("mission");
                    charityMission = charityMission.replace(".MISSION STATEMENT:",
                            "").replace("\n", " ");

                    if (charityMission.length() > 275) {
                        charityMission = charityMission.substring(0, 275) + "...";
                    }


                    logoId = R.drawable.aspca_logo;
                    updateViews();

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });



    }


    // Populates text views after getting info about the charity from Firebase.
    private void updateViews(){
        TextView charityNameTextView = findViewById(R.id.charityName);
        charityNameTextView.setText(charityName);

        TextView charityLocationTextView = findViewById(R.id.charityLocation);
        charityLocationTextView.setText(charityLocation);

        TextView charityMissionTextView = findViewById(R.id.charityMission);
        charityMissionTextView.setText(charityMission);

        ImageView imageView = (ImageView) findViewById(R.id.imageView);
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), logoId);
        RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory
                .create(getResources(), bitmap);
        roundedBitmapDrawable.setCircular(true);
        imageView.setImageDrawable(roundedBitmapDrawable);

    }


    // Loads up the user_donation database and finds donations to this charity.
    // Super inefficient but .... fine for now I guess.
    // Once a donation to this charity is found add it to ArrayList of Posts to update RecyclerView.
    private void loadTransactions(){

        Log.d(TAG, "Getting transactions to this charity: " + charityID);

        this.referenceDonationsDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Object donationsDataSnapshot = dataSnapshot.getValue(Object.class);
                if (donationsDataSnapshot != null) {

                    HashMap<String,Object> donationsDataSnapshotHashMap = (HashMap<String,Object>) donationsDataSnapshot;

                    // For each user in the user_donation database,
                    for(String userID : donationsDataSnapshotHashMap.keySet() ) {

                        Log.d(TAG, "Looking at transactions of user ID: " + userID);

                        // For each donation they've ever given,
                        HashMap<String, Object> userDonations = (HashMap<String, Object>) donationsDataSnapshotHashMap.get(userID);
                        for(String donationID : userDonations.keySet()) {

                            // If the charity ID matches this charity's ID, make a post
                            HashMap<String, Object> donation = (HashMap<String, Object>) userDonations.get(donationID);
                            String userWhoDonated = (String) donation.get("user");
                            if (donation.get("charity").equals(charityName) ) {


                                // Need to find the user of the username who donated
                                referenceUsersDB.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshotUsers) {
                                        Object userDataSnapshot = dataSnapshotUsers.getValue(Object.class);
                                        if (userDataSnapshot != null) {

                                            // Examine snapshot & retrieve this user's username via the ID
                                            HashMap<String, Object> userDB = (HashMap<String, Object>) userDataSnapshot;
                                            HashMap<String, String> curUser = (HashMap<String, String>) userDB.get(userWhoDonated);
                                            String userWhoDonatedUsername = curUser.get("username");
                                            Log.d(TAG, "User who donated: " + userWhoDonatedUsername);

                                            Post newPost = new Post("donation", userWhoDonatedUsername, charityName,
                                                    null, 5, "woo text post", 0);
                                            posts.add(newPost);
                                            charityAdapter.notifyItemInserted(posts.size() - 1);

                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });



                            }


                        }

                    }



                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });

        charityAdapter.notifyDataSetChanged();

    }




}
