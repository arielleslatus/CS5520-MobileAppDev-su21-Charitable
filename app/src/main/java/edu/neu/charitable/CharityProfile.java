package edu.neu.charitable;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class CharityProfile  extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.charity_profile);
    }

    public void onButtonClick(View v) {

        switch (v.getId()) {
            case R.id.donateButton:
                Intent letterActivityIntent = new Intent(this, CharityProfile.class);
                startActivity(letterActivityIntent);
                break;

//            case R.id.linkCollectorBtn:
//                Intent linkCollectorIntent = new Intent(this, LinkCollectorActivity.class);
//                startActivity(linkCollectorIntent);
//                break;
//
//            case R.id.LocationActivityBtn:
//                Intent locationActivityIntent = new Intent(this, LocationActivity.class);
//                startActivity(locationActivityIntent);
//                break;
//
//            case R.id.APIButton:
//                Intent apiActivityIntent = new Intent(this, APIActivity.class);
//                startActivity(apiActivityIntent);
//                break;

        }
    }




}
