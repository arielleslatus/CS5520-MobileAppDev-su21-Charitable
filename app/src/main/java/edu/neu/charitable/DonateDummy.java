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
import edu.neu.charitable.models.Post;

public class DonateDummy extends AppCompatActivity {

    private EditText editTextCharity, editTextAmount;
    private ProgressBar progressBar;
    private FirebaseDatabase mDB;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donate_dummy);

        mDB = FirebaseDatabase.getInstance();
        editTextCharity = (EditText) findViewById(R.id.donate_charity);
        editTextAmount = (EditText) findViewById(R.id.donate_amount);
        progressBar = (ProgressBar) findViewById(R.id.donate_progressBar);

        String donate_to = getIntent().getStringExtra("AUTOFILL_CHARITY");
        if (donate_to != null && !donate_to.isEmpty()) {
            editTextCharity.setText(donate_to);
        }

        String donate_amount = getIntent().getStringExtra("AUTOFILL_AMOUNT");
        if (donate_amount != null && !donate_amount.isEmpty()) {
            editTextAmount.setText(donate_amount);
        }
    }

    public void back(View view) {
        startActivity(new Intent(this, Home.class));
    }

    public void donate(View view) {
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
        }



        progressBar.setVisibility(View.VISIBLE);
        mDB.getReference("Charities").orderByChild("name").equalTo(charity)
        .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                        String username = FirebaseAuth.getInstance()
                                .getCurrentUser()
                                .getUid();

                    Donation don = new Donation(charity, username, amount);
                    makeDonation(don, username, charity, amount);
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

    private void makeDonation(Donation don, String username, String charity, float amount) {
        Toast.makeText(DonateDummy.this, "making donation", Toast.LENGTH_LONG).show();
        mDB.getReference().child("user_donations").child(username).push().setValue(don).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(DonateDummy.this, "Donation Received", Toast.LENGTH_LONG).show();

                    Post post = new Post(don.timestamp, "donation", username, charity, "", amount, "", 0);
                    updatePosts(post, username);
                    updateGoal(don, username);

                } else {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(DonateDummy.this, "Donation Failed", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void updatePosts(Post post, String username) {
        mDB.getReference().child("user_posts").child(username).push().setValue(post).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(DonateDummy.this, "Shared", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(DonateDummy.this, "Failed to Share", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void updateGoal(Donation don, String username) {
        mDB.getReference("user_goal").child(username).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Goal goal =  snapshot.getValue(Goal.class);
                    float newAmount = goal.amoundDonated + don.amount;

                    if (!goal.complete) {
                        if (newAmount >= goal.amountSet - .000001) {
                            goal.amoundDonated = goal.amountSet;
                            goal.complete = true;
                            goalComplete(goal, username);
                        } else {
                            goal.amoundDonated = newAmount;
                        }
                    }
                    mDB.getReference("user_goal").child(username).setValue(goal).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(DonateDummy.this, "Goal Updated" ,Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(DonateDummy.this, error.toString(), Toast.LENGTH_LONG).show();

            }
        });
    }

    private void goalComplete(Goal goal, String username) {
        Post post = new Post("goal_complete",username,goal.charity,"",goal.amountSet,"",0);
        updatePosts(post, username);
    }
}