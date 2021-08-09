package edu.neu.charitable;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import edu.neu.charitable.models.Donation;
import edu.neu.charitable.models.Goal;

public class SetGoal extends AppCompatActivity {

    private EditText editTextCharity, editTextAmount;
    private ProgressBar progressBar;
    private FirebaseDatabase mDB;
    private String this_user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_goal);

        mDB = FirebaseDatabase.getInstance();
        editTextCharity = (EditText) findViewById(R.id.new_goal_charity);
        editTextAmount = (EditText) findViewById(R.id.new_goal_amount);
        progressBar = (ProgressBar) findViewById(R.id.set_goal_progressBar);

        String donate_to = getIntent().getStringExtra("AUTOFILL_CHARITY");
        if (donate_to != null && !donate_to.isEmpty()) {
            editTextCharity.setText(donate_to);
        }

        String donate_amount = getIntent().getStringExtra("AUTOFILL_AMOUNT");
        if (donate_amount != null && !donate_amount.isEmpty()) {
            editTextAmount.setText(donate_amount);
        }

        this_user = FirebaseAuth.getInstance().getCurrentUser().getUid();

    }

    public void setGoal(View view) {

        float amount;
        String charity;

        try {
            amount = Float.parseFloat(editTextAmount.getText().toString());
        } catch (Exception e) {
            editTextAmount.setError("Need Valid amount");
            editTextAmount.requestFocus();
            return;
        }

        charity = editTextCharity.getText().toString();
        if (charity.isEmpty()) {
            editTextCharity.setError("Need a Charity Name");
            editTextCharity.requestFocus();
        }

        progressBar.setVisibility(View.VISIBLE);
        mDB.getReference("Charities").orderByChild("name").equalTo(charity)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            Goal goal = new Goal(charity,amount,0);
                            uploadGoal(goal);
                        } else {
                            editTextCharity.setError("Charity not found");
                            editTextCharity.requestFocus();
                            progressBar.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        progressBar.setVisibility(View.GONE);
                    }
                });
    }

    private void uploadGoal(Goal goal) {
        mDB.getReference("user_goal").child(this_user).setValue(goal).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(SetGoal.this, "Goal Set!", Toast.LENGTH_LONG);
                    startActivity(new Intent(SetGoal.this, Home.class));
                }
            }
        });
    }
}