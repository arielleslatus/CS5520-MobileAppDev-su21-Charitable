package edu.neu.charitable;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

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

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
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
    String charityLogoURL;
    Drawable drawableCharityLogo;
    Bitmap logoBitmap;
//    int logoId;

    // Firebase references to retrieve data about the charity, and donations to it
    private DatabaseReference referenceCharitiesDB;
    private DatabaseReference referenceDonationsDB;
    private DatabaseReference referenceUsersDB;
    private DatabaseReference referenceCharityImg;

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

        this.referenceCharitiesDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "Retrieving Charity Data to Populate CharityProfile for: " + charityID);
                Object charityDataSnapshot = dataSnapshot.getValue(Object.class);
                if (charityDataSnapshot != null) {

                    // Examine snapshot & retrieve this charity's info via the ID
                    HashMap<String,Object> charityDB = (HashMap<String,Object>) charityDataSnapshot;
                    HashMap<String,String> curCharity = (HashMap<String,String>) charityDB.get(charityID);

                    charityName = curCharity.get("name");
                    charityLocation = curCharity.get("city") + ", " + curCharity.get("state") + ", "
                            + curCharity.get("country");
                    charityMission = curCharity.get("mission");
                    charityMission = charityMission.replace(".MISSION STATEMENT:",
                            "").replace("\n", " ");

                    charityLogoURL = curCharity.get("logoUrl");
                    Log.d(TAG, "Found this charityLogoURL: " + charityLogoURL);

                    new DownloadsImage().execute(charityLogoURL);



                    if (charityMission.length() > 275) {
                        charityMission = charityMission.substring(0, 275) + "...";
                    }


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

                    Log.d(TAG, "donationsDataSnapshot isn't null! Finding donations.... ");

                    HashMap<String,Object> donationsDataSnapshotHashMap = (HashMap<String,Object>) donationsDataSnapshot;

                    // For each user in the user_donation database,
                    for(String userID : donationsDataSnapshotHashMap.keySet() ) {

//                        Log.d(TAG, "Looking at transactions of user ID: " + userID);

                        // For each donation they've ever given,
                        HashMap<String, Object> userDonations = (HashMap<String, Object>) donationsDataSnapshotHashMap.get(userID);
                        for(String donationID : userDonations.keySet()) {

                            // If the charity ID matches this charity's ID, make a post
                            HashMap<String, Object> donation = (HashMap<String, Object>) userDonations.get(donationID);
                            String userWhoDonatedID = (String) donation.get("user");

                            if (donation.get("charity").equals(charityName) ) {

                                Log.d(TAG, "Donation found for  " + charityName + " with donationID: " + donationID);

                                // Get the timestamp from the found post.
                                long timestamp = (long) donation.get("timestamp");

                                // Get the amount of the donation from the found post.
                                float amount = 0;

                                try { // Sometimes it is a double
                                    amount = ((Double)donation.get("amount")).floatValue();
                                } catch (ClassCastException e) { // Other times it is a float
                                    amount = (float) Math.round((long)(donation.get("amount")));
                                }

                                // Get the number of donation from the post; if none, then it's 0
                                int numApplauds = 0;
                                if (donation.get("numApplauds") != null) {
                                    numApplauds = (int) donation.get("numApplauds");
                                }

                                // Create the post based on the information
                                Post newPost = new Post(timestamp, "donation", userWhoDonatedID, charityName,
                                        null, amount, "", numApplauds);
                                posts.add(newPost);
                                charityAdapter.notifyItemInserted(posts.size() - 1);

//                                // Need to find the user of the username who donated
//                                referenceUsersDB.addValueEventListener(new ValueEventListener() {
//                                    @Override
//                                    public void onDataChange(DataSnapshot dataSnapshotUsers) {
//                                        Object userDataSnapshot = dataSnapshotUsers.getValue(Object.class);
//
//                                        if (userDataSnapshot != null) {
//
////                                            // Examine snapshot & retrieve this user's username via the ID
////                                            HashMap<String, Object> userDB = (HashMap<String, Object>) userDataSnapshot;
////
////
////                                            String userWhoDonatedUsername;
////                                            try {
////                                                HashMap<String, String> curUser = (HashMap<String, String>) userDB.get(userWhoDonated);
//////                                            Log.d(TAG, "Looking at Users database .... curUser " + curUser.toString());
//////                                            Log.d(TAG, "Looking at Users database .... hoping to get their username " + curUser.toString());
////
////                                                userWhoDonatedUsername = curUser.get("username");
////                                                Log.d(TAG, "User who donated: " + userWhoDonatedUsername);
////                                            }
////                                            catch (NullPointerException e) {
////                                                userWhoDonatedUsername = "Unknown";
////                                                Log.d(TAG, "Does this donation not have a username? " +
////                                                        "" + donation.toString());
////                                            }
//
//
//
//                                        }
//                                    }
//
//                                    @Override
//                                    public void onCancelled(@NonNull DatabaseError error) {
//
//                                    }
//                                });

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


    class DownloadsImage extends AsyncTask<String, Void,Void>{

        @Override
        protected Void doInBackground(String... strings) {
            URL inputAsyncUrl = null;
            try {
                Log.d(TAG, "Input to Async function: " + strings[0]);
                inputAsyncUrl = new URL(strings[0]);
            } catch (MalformedURLException e) {
                Log.d(TAG, "Error (Malformed URL), " + e.toString());
            }
            Bitmap bm = null;
            try {
                Log.d(TAG, "Creating BM Factory from " + inputAsyncUrl.toString());
                logoBitmap = BitmapFactory.decodeStream(inputAsyncUrl.openConnection().getInputStream());

            } catch (IOException e) {
                Log.d(TAG, "Error (IOException), " + e.toString());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            ImageView imageView = (ImageView) findViewById(R.id.imageView);

            Log.d(TAG, "Creating a RoundedBitmapDrawableFactory...");
            RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(getResources(), logoBitmap);

            Log.d(TAG, "Setting the  image as the logo...");
            imageView.setImageDrawable(roundedBitmapDrawable);

            Log.d(TAG, "Image Put in ImageView");
        }
    }

}
