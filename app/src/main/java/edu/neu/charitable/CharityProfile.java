package edu.neu.charitable;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.util.Log;


import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;

public class CharityProfile  extends AppCompatActivity {

    String charityName;
    String charityLocation;
    String charityMission;
    String uidLoggedIn;
    int logoId;

    String TAG = "CharityProfile";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.charity_profile);

        // Get name of the Charity and user
        retrieveCharityData();
        uidLoggedIn = getIntent().getStringExtra("uid");

        TextView charityNameTextView = findViewById(R.id.charityName);
        charityNameTextView.setText(charityName);

        TextView charityLocationTextView = findViewById(R.id.charityLocation);
        charityLocationTextView.setText(charityLocation);

        TextView charityMissionTextView = findViewById(R.id.charityMission);
        charityMissionTextView.setText(charityMission);

        ImageView imageView = (ImageView) findViewById(R.id.imageView);
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), logoId);
        RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(getResources(), bitmap);
        roundedBitmapDrawable.setCircular(true);
        imageView.setImageDrawable(roundedBitmapDrawable);

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

        charityName = "ASPCA";
        charityLocation = "Boston, MA, USA";
        charityMission = "Saving the lives of puppies and kittens since 1975.";
        logoId = R.drawable.aspca_logo;

    }




}
