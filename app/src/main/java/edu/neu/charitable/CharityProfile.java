package edu.neu.charitable;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class CharityProfile  extends AppCompatActivity {

    String charityName;
    String charityLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.charity_profile);

        // Get name of the Charity
        retrieveCharityData();

        TextView charityNameTextView = findViewById(R.id.charityName);
        charityNameTextView.setText(charityName);



    }

    public void onButtonClick(View v) {

        switch (v.getId()) {
            case R.id.donateButton:
                Intent letterActivityIntent = new Intent(this, CharityProfile.class);
                startActivity(letterActivityIntent);
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




    }




}
