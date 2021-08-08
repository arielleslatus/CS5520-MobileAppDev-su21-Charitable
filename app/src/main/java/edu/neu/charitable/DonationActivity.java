package edu.neu.charitable;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class DonationActivity extends AppCompatActivity {

    EditText donationAmountView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_donation);


    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.donateNowButton:Button:
                donationAmountView = (EditText) findViewById(R.id.donationAmount);
                int paymentValue = Integer.parseInt(donationAmountView.getText().toString());
                submitPayment(paymentValue);
                break;
        }

    }



    private boolean submitPayment(int paymentValue){

        return false;
    }

}
