package edu.neu.charitable;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.ParseException;

public class DonationActivity extends AppCompatActivity {

    EditText donationAmountView;
    TextView charityNameTextview;
    private DatabaseReference mDatabase;
    String charityName;
    String uidLoggedIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_donation);

        Bundle extras = getIntent().getExtras();
        charityName = extras.getString("CHARITY_NAME");
        uidLoggedIn = extras.getString("uid");

        mDatabase = FirebaseDatabase.getInstance().getReference();

        donationAmountView = (EditText) findViewById(R.id.donationAmount);

        charityNameTextview = (TextView) findViewById(R.id.CharityNameText);
        charityNameTextview.setText("Donating to: " + charityName);


    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.donateNowButton:
                submitPayment();
                break;
        }

    }



    private boolean submitPayment(){

        // Convert to integer value
        int paymentValue = Integer.parseInt(donationAmountView.getText().toString());

        // If it's 0, don't allow that
        if (paymentValue <= 0){
            donationAmountView.setError("Donation amount must be > 0.");
            Toast.makeText(DonationActivity.this, "Donation amount must be more " +
                    "than 0.", Toast.LENGTH_LONG).show();
        }


        Transaction newTransaction = new Transaction(paymentValue, charityName,
                "userID");

        mDatabase.child("transactions").setValue(newTransaction);

        Toast.makeText(DonationActivity.this, "Donation made:" + paymentValue + "from"
                + uidLoggedIn + " to " + charityName, Toast.LENGTH_LONG).show();

        return false;
    }

}
