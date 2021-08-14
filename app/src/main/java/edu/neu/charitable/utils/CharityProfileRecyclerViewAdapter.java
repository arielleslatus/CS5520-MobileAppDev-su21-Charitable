package edu.neu.charitable.utils;


import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

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
        this.posts = posts;
        current_user = FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_donation, parent, false);
        return new DonationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        // Sending text/content to the holder.
        if (holder instanceof DonationViewHolder) {

            if (posts.size() != 0){
                bindDonationView(holder, position);
            }

        }
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }


    @Override
    public int getItemViewType(int position) {
        return VIEW_TYPE_DONATION;
    }


    // Adds onclick listener to applaud buttons generated in recyclerView
    private void applaudListener(RecyclerView.ViewHolder holder, Button buttonApplaud, Post post) {
        Log.d(TAG, "Adding applaud listener....");
        buttonApplaud.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "In applaudListener, clicked applaud. This is the post:");
                Log.d(TAG, post.toString());

//                String username = post.user;
//
//                // Get the user ID from the username
//                FirebaseDatabase.getInstance().getReference("user_posts")
//                        .addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        if (snapshot.exists()) {
//
//
//                        }
//
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//
//                    }
//                });



                FirebaseDatabase.getInstance().getReference("user_posts")
                        .child(post.user)
                        .orderByChild("timestamp")
                        .equalTo(post.timestamp)
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                Log.d(TAG, "In applaudListener, onDataChange");

                                if (snapshot.exists()) {
                                    String postKey = "";
                                    for (DataSnapshot ds : snapshot.getChildren()) {
                                        postKey = ds.getKey();
                                        Log.d(TAG, "In applaudListener, postKey:" + postKey);
                                    }

                                    Post newPost = new Post(post.timestamp, post.type, post.user,
                                            post.charity, post.matchedUser, post.amount, post.text,
                                            post.numApplauds + 1);
                                    Log.d(TAG, "In applaudListener, user_posts, snapshot exists.");

                                    Log.d(TAG, "Updating post " + postKey + " with +1 applaud");
                                    FirebaseDatabase.getInstance().getReference("user_posts")
                                            .child(post.user)
                                            .child(postKey)
                                            .setValue(newPost).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
//                                            Log.d(TAG, "You applauded! Refresh dataset.");
                                            notifyDataSetChanged();
                                        }
                                    });
                                }

                                else {
                                    Log.d(TAG, "snapshot.exists() does not exist");
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

    private void bindDonationView(@NonNull RecyclerView.ViewHolder holder, int position) {
        Post post = posts.get(position);
        Log.d(TAG, "Making donation post: " + post.toString());

        Button buttonMatch = ((DonationViewHolder) holder).buttonMatch;
        Button buttonShare = ((DonationViewHolder) holder).buttonShare;
        Button buttonApplaud = ((DonationViewHolder) holder).buttonApplaud;
        TextView postTextView = ((DonationViewHolder) holder).postText;
        TextView timeText = ((DonationViewHolder) holder).timeText;
        CardView donationPostCard = ((DonationViewHolder) holder).donationPostCard;


        if (!post.user.equals(current_user)) {
            FirebaseDatabase.getInstance().getReference("Users").child(post.user)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        User retrievedUser = snapshot.getValue(User.class);
                        String postUsername = retrievedUser.username;
                        String userText = "@" + postUsername;
                        String postTextContent = userText + " donated to " + post.charity + "!";
                        postTextView.setText(postTextContent);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

        else {
            String userText = "You";
            String postTextContent = userText + " donated to " + post.charity + "!";
            postTextView.setText(postTextContent);
        }


        LocalDateTime dt = LocalDateTime.ofInstant(Instant.ofEpochMilli(post.timestamp), TimeZone.getDefault().toZoneId());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mm a ' ' MM.dd");
        timeText.setText(formatter.format(dt) + "  -  " + Integer.toString(post.numApplauds) + " Claps");

        applaudListener(holder, buttonApplaud, post);
        matchOnClickListener(holder, buttonMatch, post);


    }


}
