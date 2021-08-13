package edu.neu.charitable.utils;


import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.TimeZone;

import edu.neu.charitable.DonateDummy;
import edu.neu.charitable.Home;
import edu.neu.charitable.R;
import edu.neu.charitable.models.Post;
import edu.neu.charitable.models.User;

public class CharityProfileRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context context;
    public List<Post> posts;
    private String current_user;
    private String TAG = "CharityProfileRecyclerViewAdapter DebugAlice ";
    private final int VIEW_TYPE_DONATION = 1;

    public CharityProfileRecyclerViewAdapter(List<Post> posts) {
        // Store values inside our constructor
        Log.d(TAG, "CharityProfileRecyclerViewAdapter was invoked.");
        this.posts = posts;
        current_user = FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder invoked at with viewType: " + viewType);
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_donation, parent, false);
        return new DonationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Log.d(TAG, "\t class of hold: " + holder.getClass());


        // Sending text/content to the holder.
        if (holder instanceof DonationViewHolder) {
            Log.d(TAG, "X We have a holder but size is 0");
            Log.d(TAG, "Posts is this now:" + posts.toString());



            if (posts.size() != 0){
                Log.v(TAG, "We have a holder, instance of DonationViewHolder:" + ((DonationViewHolder) holder).postText);
                bindDonationView(holder, position);
            }

        }

        else {
            Log.d(TAG, "DebugAlice X holder NOT instanceof DonationViewHolder");
        }

    }

    @Override
    public int getItemCount() {
        return posts.size();
    }


    @Override
    public int getItemViewType(int position) {
        Log.d(TAG, "DebugAlice Getting itemViewType at position " + position);
        return VIEW_TYPE_DONATION;
    }


    //Adds onclick listener to applaud buttons generated in recyclerView
    private void applaudListener(RecyclerView.ViewHolder holder, Button buttonApplaud, Post post) {
        buttonApplaud.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseDatabase.getInstance().getReference("user_posts")
                        .child(post.user)
                        .orderByChild("timestamp")
                        .equalTo(post.timestamp)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    String postKey = "";
                                    for (DataSnapshot ds : snapshot.getChildren()) {
                                        postKey = ds.getKey();
                                    }
                                    Post newPost = new Post(post.timestamp,post.type,post.user,post.charity,post.matchedUser,post.amount,post.text,post.numApplauds + 1);
                                    FirebaseDatabase.getInstance().getReference("user_posts")
                                            .child(post.user)
                                            .child(postKey)
                                            .setValue(newPost).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            Intent intent = new Intent(v.getContext(), Home.class);
                                            v.getContext().startActivity(intent);
                                        }
                                    });
                                }
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                            }
                        });
            }
        });
    }

    private void matchOnClickListener(RecyclerView.ViewHolder holder, Button buttonMatch, Post post) {
        buttonMatch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), DonateDummy.class);
                intent.putExtra("AUTOFILL_CHARITY" ,post.charity);
                intent.putExtra("AUTOFILL_AMOUNT", Float.toString(post.amount));
                intent.putExtra("MATCH", post.user);
                v.getContext().startActivity(intent);
            }
        });
    }

    private void bindDonationView(RecyclerView.ViewHolder holder, int position) {
        Log.d(TAG, "bindDonationView invoked at position: " + position);

        Post post = posts.get(position);

        Log.d(TAG, "Making post with: " + post.toString());


        Button buttonMatch = ((DonationViewHolder) holder).buttonMatch;
        Button buttonShare = ((DonationViewHolder) holder).buttonShare;
        Button buttonApplaud = ((DonationViewHolder) holder).buttonApplaud;
        TextView postText = ((DonationViewHolder) holder).postText;
        TextView timeText = ((DonationViewHolder) holder).timeText;
        CardView donationPostCard = ((DonationViewHolder) holder).donationPostCard;


        //Get user and fill in information this set listeners for the buttons
        FirebaseDatabase.getInstance().getReference("Users").child(post.user).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    User u = snapshot.getValue(User.class);
                    if (post.user.equals(current_user)) {
                        postText.setText("You donated to " + post.charity + "!");

                    } else {
                        postText.setText("@" + u.username + " donated to " + post.charity + "!");
                    }

                    LocalDateTime dt = LocalDateTime.ofInstant(Instant.ofEpochMilli(post.timestamp), TimeZone.getDefault().toZoneId());
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mm a ' ' MM.dd");
                    timeText.setText(formatter.format(dt) + "  -  " + Integer.toString(post.numApplauds) + " Claps");

                    applaudListener(holder, buttonApplaud, post);
                    matchOnClickListener(holder, buttonMatch, post);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(((DonationViewHolder) holder).donationPostCard.getContext(), error.toString(), Toast.LENGTH_LONG).show();
            }
        });
    }


}