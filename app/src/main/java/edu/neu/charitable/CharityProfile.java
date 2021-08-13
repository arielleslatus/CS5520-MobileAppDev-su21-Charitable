package edu.neu.charitable;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class CharityProfile  extends AppCompatActivity {

    String charityName;
    String charityLocation;
    String charityMission;
    String uidLoggedIn;
    String charityID;
    int logoId;

    private DatabaseReference reference;

    String TAG = "CharityProfile";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.charity_profile);

        uidLoggedIn = getIntent().getExtras().getString("uid");
        charityID = getIntent().getExtras().getString("charityID");

        // Get the path from firebase.
        this.reference = FirebaseDatabase.getInstance().getReference("Charities");

        // Get info about charity then populate the layout
        retrieveCharityData();
        updateFields();

    }

    public void onButtonClick(View v) {

        switch (v.getId()) {
            case R.id.donateButton:
                Intent donationActivity = new Intent(this, DonationActivity.class);
                Bundle extras = new Bundle();
                extras.putString("CHARITY_NAME", charityName);
                extras.putString("uid", uidLoggedIn);
                donationActivity.putExtras(extras);
                startActivity(donationActivity);
                break;

//            case R.id.__:
//                ...
//                break;
//


        }
    }


    private void retrieveCharityData(){

        this.reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "OnDataChange invoked....");
                Object charityDataSnapshot = dataSnapshot.getValue(Object.class);
                if (charityDataSnapshot != null) {

                    Log.d(TAG, "charityDataSnapshot: " + charityDataSnapshot.toString());

                    HashMap<String,Object> charityDB = (HashMap<String,Object>) charityDataSnapshot;
                    Log.d(TAG, "charityDB: " + charityDB.toString());
                    Log.d(TAG, "charityDB.type: " + charityDB.getClass());

                    HashMap<String,String> curCharity = (HashMap<String,String>) charityDB.get("-"+charityID);
                    Log.d(TAG, "curCharity: " + curCharity.toString());
                    Log.d(TAG, "curCharity.type: " + curCharity.getClass());

                    charityName = curCharity.get("name");
                    charityLocation = curCharity.get("city") + ", " + curCharity.get("state") + ", "
                            + curCharity.get("country");
                    charityMission = curCharity.get("mission");

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });


//        charityName = "ASPCA";
//        charityLocation = "Boston, MA, USA";
//        charityMission = "Saving the lives of puppies and kittens since 1975.";
        logoId = R.drawable.aspca_logo;

    }



    private void updateFields(){
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




}
