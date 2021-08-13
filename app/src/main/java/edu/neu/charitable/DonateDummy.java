package edu.neu.charitable;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
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

import edu.neu.charitable.models.Charity;
import edu.neu.charitable.models.Donation;
import edu.neu.charitable.models.Goal;
import edu.neu.charitable.models.Post;
import edu.neu.charitable.models.User;

import java.util.concurrent.ThreadLocalRandom;

public class DonateDummy extends AppCompatActivity {

    private EditText editTextCharity, editTextAmount;
    private ProgressBar progressBar;
    private FirebaseDatabase mDB;
    private boolean isMatch;
    private boolean isPool;
    private String matchTo;
    private String userDonate;
    private boolean directToUser;


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
            if (donate_to.equals("Charitable Pool Direct")) {
                isPool = true;
            }
            editTextCharity.setEnabled(false);
        }

        String donate_amount = getIntent().getStringExtra("AUTOFILL_AMOUNT");
        if (donate_amount != null && !donate_amount.isEmpty()) {
            editTextAmount.setText(donate_amount);
        }

        String match = getIntent().getStringExtra("MATCH");
        if (match != null) {
            isMatch = true;
            matchTo = match;
        } else {
            isMatch = false;
        }

        String dtu = getIntent().getStringExtra("DIRECT_TO_USER");
        if (dtu != null) {
            directToUser = true;
            userDonate = dtu;
        } else {
            directToUser = false;
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
            editTextCharity.requestFocus();
        }


        if (directToUser) {
            progressBar.setVisibility(View.VISIBLE);

            mDB.getReference("username_venmo").child(userDonate).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String selected_venmo = snapshot.getValue(String.class);
                        String username = FirebaseAuth.getInstance().getCurrentUser().getUid();
                        Donation don = new Donation(charity, username, amount);
                        makeDonation(don, username, charity, amount, userDonate);
                        linkToVenmoDirect(don, selected_venmo);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        } else if (charity.equals("Charitable Pool Direct")) {
            progressBar.setVisibility(View.VISIBLE);

            //get num users
            mDB.getReference("num_pool_users").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        Integer num_in_pool = snapshot.getValue(Integer.class);
                        if (num_in_pool != null) {
                            int random = ThreadLocalRandom.current().nextInt(1, num_in_pool + 1);

                            //get the randomly selected recipient
                            mDB.getReference("pool").child(Integer.toString(random)).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.exists()) {
                                        String pool_selected = snapshot.getValue(String.class);
                                        if (pool_selected != null) {

                                            //get the user's venmo id
                                            mDB.getReference("username_venmo").child(pool_selected).addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    if (snapshot.exists()) {
                                                        String selected_venmo = snapshot.getValue(String.class);
                                                        String username = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                                        Donation don = new Donation(charity, username, amount);
                                                        makeDonation(don, username, charity, amount, pool_selected);
                                                        linkToVenmoUser(don, selected_venmo);


                                                        mDB.getReference("pool_received").child(pool_selected).push().setValue(don);

                                                    } else {
                                                        progressBar.setVisibility(View.GONE);
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {

                                                }
                                            });
                                        } else {
                                            progressBar.setVisibility(View.GONE);
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                        } else {
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        } else {
            progressBar.setVisibility(View.VISIBLE);

            //check if charity exists
            mDB.getReference("Charities").orderByChild("name").equalTo(charity)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                String charId = "";
                                for (DataSnapshot ds : snapshot.getChildren()) {
                                    charId = ds.getKey();
                                }
                                String username = FirebaseAuth.getInstance()
                                        .getCurrentUser()
                                        .getUid();

                                Donation don = new Donation(charity, username, amount);
                                makeDonation(don, username, charity, amount, "");
                                linkToVenmo(don, charId);
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
    }

    private void makeDonation(Donation don, String username, String charity, float amount, String pool_user) {
        //Toast.makeText(DonateDummy.this, "making donation", Toast.LENGTH_LONG).show();
        mDB.getReference().child("user_donations").child(username).push().setValue(don).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    progressBar.setVisibility(View.GONE);
                    //Toast.makeText(DonateDummy.this, "Donation Received", Toast.LENGTH_LONG).show();
                    Post post;
                    if (directToUser) {
                        post = new Post(don.timestamp, "pool", username, charity, "", amount, pool_user, 0);
                    } else if (isMatch && isPool) {
                        post = new Post(don.timestamp, "match_pool", username, charity, matchTo, amount, pool_user, 0);
                    } else if (isMatch) {
                        post = new Post(don.timestamp, "match", username, charity, matchTo, amount, "", 0);
                    } else if (isPool) {
                        post = new Post(don.timestamp, "pool", username, charity, "", amount, pool_user, 0);
                    } else {
                        post = new Post(don.timestamp, "donation", username, charity, "", amount, "", 0);
                    }
                    updatePosts(post, username);
                    updateGoal(don, username, charity);
                    startActivity(new Intent(DonateDummy.this, Home.class));
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
                    //Toast.makeText(DonateDummy.this, "Shared", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(DonateDummy.this, "Failed to Share", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void updateGoal(Donation don, String username, String chrName) {
        mDB.getReference("user_goal").child(username).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Goal goal = snapshot.getValue(Goal.class);

                    mDB.getReference("Charities").child(goal.charity).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                Charity goal_charity = snapshot.getValue(Charity.class);
                                if (goal_charity != null && goal_charity.name.equals(chrName)) {
                                    float newAmount = goal.amoundDonated + don.amount;

                                    if (!goal.complete) {
                                        if (newAmount >= goal.amountSet - .001) {
                                            goal.amoundDonated = goal.amountSet;
                                            goal.complete = true;
                                            goalComplete(goal, username, chrName);
                                        } else {
                                            goal.amoundDonated = newAmount;
                                        }
                                    }
                                    mDB.getReference("user_goal").child(username).setValue(goal).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            //Toast.makeText(DonateDummy.this, "Goal Updated" ,Toast.LENGTH_LONG).show();
                                        }
                                    });

                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void goalComplete(Goal goal, String username, String chrName) {
        Post post = new Post("goal_complete", username, chrName, "", goal.amountSet, "", 0);
        Toast.makeText(DonateDummy.this, "You've completed your goal!!!", Toast.LENGTH_LONG).show();
        updatePosts(post, username);
    }

    //this is venmo's deeplink system
    //if charity has a venmo, and user has venmo installed,
    private void linkToVenmo(Donation don, String charId) {
        //Toast.makeText(DonateDummy.this, charId, Toast.LENGTH_LONG).show();
        mDB.getReference("charity_venmo").child(charId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String venmoId = "";
                if (snapshot.exists()) {
                    venmoId = snapshot.getValue(String.class);
                    //Toast.makeText(DonateDummy.this, venmoId, Toast.LENGTH_LONG).show();
                }

                if (venmoId != null && !venmoId.isEmpty()) {
                    String note = "A donation to " + don.charity + "!";
                    String url = "https://venmo.com/" + venmoId + "?txn=pay&note=" + note + "&amount=" + don.amount;
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(browserIntent);
                } else {
                    String note = "Charity with id " + charId + " has not set up venmo. All funds received by Charitable will be donated in bulk once a month. *Data transfer rates may apply.";
                    String url = "https://venmo.com/GladDev?txn=pay&note=" + note + "&amount=" + don.amount;
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(browserIntent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    //For pool donation
    private void linkToVenmoUser(Donation don, String venmoId) {
        mDB.getReference("Users").child(don.user).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    User user = snapshot.getValue(User.class);
                    if (user != null) {
                        String note = "You have been selected for generous gift from @" + user.username + " as a member of Charitable Pool!";
                        String url = "https://venmo.com/" + venmoId + "?txn=pay&note=" + note + "&amount=" + don.amount;
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        startActivity(browserIntent);
                    }
                } else {
                    String note = "You have been selected for generous gift as a member of Charitable Pool!";
                    String url = "https://venmo.com/" + venmoId + "?txn=pay&note=" + note + "&amount=" + don.amount;
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(browserIntent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void linkToVenmoDirect(Donation don, String venmoId) {
        mDB.getReference("Users").child(don.user).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    User user = snapshot.getValue(User.class);
                    if (user != null) {
                        String note = "You have received a direct donation from @" + user.username + "!";
                        String url = "https://venmo.com/" + venmoId + "?txn=pay&note=" + note + "&amount=" + don.amount;
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        startActivity(browserIntent);
                    }
                } else {
                    String note = "You have been received a direct donation from a user!";
                    String url = "https://venmo.com/" + venmoId + "?txn=pay&note=" + note + "&amount=" + don.amount;
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(browserIntent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}