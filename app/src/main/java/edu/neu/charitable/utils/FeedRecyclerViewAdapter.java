package edu.neu.charitable.utils;

import android.content.Intent;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.security.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.TimeZone;

import edu.neu.charitable.DonateDummy;
import edu.neu.charitable.Home;
import edu.neu.charitable.R;
import edu.neu.charitable.fragments.Timeline;
import edu.neu.charitable.models.Post;
import edu.neu.charitable.models.User;

public class FeedRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int VIEW_TYPE_LOADING = 0;
    private final int VIEW_TYPE_DONATION = 1;
    private final int VIEW_TYPE_MATCH = 2;
    private final int VIEW_TYPE_GOAL_ACHIEVED = 3;

    public List<Post> posts;
    private FirebaseDatabase mDB;

    public FeedRecyclerViewAdapter(List<Post> posts) {
        this.posts = posts;
        mDB = FirebaseDatabase.getInstance();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_LOADING) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_loading, parent, false);
            return new FeedRecyclerViewAdapter.LoadingViewHolder(view);
        } else if (viewType == VIEW_TYPE_DONATION) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_donation, parent, false);
            return new FeedRecyclerViewAdapter.DonationViewHolder(view);
        } else if (viewType == VIEW_TYPE_MATCH) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_match, parent, false);
            return new FeedRecyclerViewAdapter.MatchViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_goal, parent, false);
            return new FeedRecyclerViewAdapter.GoalAcheivedViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof FeedRecyclerViewAdapter.DonationViewHolder) {

            bindDonationView(holder, position);

        } else if (holder instanceof FeedRecyclerViewAdapter.MatchViewHolder) {

            bindMatchView(holder, position);

        } else if (holder instanceof FeedRecyclerViewAdapter.GoalAcheivedViewHolder) {

            bindGoalAchievedView(holder, position);

        } else {

            //this throws error?
            //((FeedRecyclerViewAdapter.LoadingViewHolder) holder).progressBar.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return posts == null ? 0 : posts.size();
    }

    @Override
    public int getItemViewType(int position) {
        Post p = posts.get(position);
        if (p == null) {
            return VIEW_TYPE_LOADING;
        } else if (p.type.equalsIgnoreCase("donation")) {
            return VIEW_TYPE_DONATION;
        } else if (p.type.equalsIgnoreCase("match")) {
            return VIEW_TYPE_MATCH;
        } else {
            return VIEW_TYPE_GOAL_ACHIEVED;
        }
    }

    private class DonationViewHolder extends RecyclerView.ViewHolder {

        public Button buttonMatch;
        public Button buttonShare;
        public Button buttonApplaud;
        public TextView postText;
        public TextView timeText;
        public CardView donationPostCard;

        public DonationViewHolder(@NonNull View itemView) {
            super(itemView);

            postText = (TextView) itemView.findViewById(R.id.donation_text);
            timeText = (TextView) itemView.findViewById(R.id.donation_time_applauds);
            buttonMatch = (Button) itemView.findViewById(R.id.donation_match);
            buttonShare= (Button) itemView.findViewById(R.id.donation_share);
            buttonApplaud = (Button) itemView.findViewById(R.id.donation_applaud);
            donationPostCard = (CardView) itemView.findViewById(R.id.donation_card);
        }
    }

    private class MatchViewHolder extends RecyclerView.ViewHolder {

        public Button buttonShare;
        public Button buttonApplaud;
        public TextView postText;
        public TextView timeText;
        public CardView donationPostCard;

        public MatchViewHolder(@NonNull View itemView) {
            super(itemView);

            postText = (TextView) itemView.findViewById(R.id.match_text);
            timeText = (TextView) itemView.findViewById(R.id.match_time);
            buttonShare= (Button) itemView.findViewById(R.id.match_share);
            buttonApplaud = (Button) itemView.findViewById(R.id.match_applaud);
            donationPostCard = (CardView) itemView.findViewById(R.id.card_match);

        }
    }

    private class GoalAcheivedViewHolder extends RecyclerView.ViewHolder {

        public Button buttonShare;
        public Button buttonApplaud;
        public TextView postText;
        public TextView timeText;
        public CardView donationPostCard;

        public GoalAcheivedViewHolder(@NonNull View itemView) {
            super(itemView);

            postText = (TextView) itemView.findViewById(R.id.goal_text);
            buttonShare= (Button) itemView.findViewById(R.id.goal_share);
            buttonApplaud = (Button) itemView.findViewById(R.id.goal_applaud);
            donationPostCard = (CardView) itemView.findViewById(R.id.card_goal);
            timeText = (TextView) itemView.findViewById(R.id.goal_time);
        }
    }

    private class LoadingViewHolder extends RecyclerView.ViewHolder {

        ProgressBar progressBar;

        public LoadingViewHolder(@NonNull View itemView) {
            super(itemView);
            progressBar = itemView.findViewById(R.id.progressBar_loading);
        }
    }

    private void bindDonationView(@NonNull RecyclerView.ViewHolder holder, int position) {
        Post post = posts.get(position);

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
                    postText.setText("@" + u.username + " donated to " + post.charity + "!");

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

    private void bindGoalAchievedView(@NonNull RecyclerView.ViewHolder holder, int position) {
        Post post = posts.get(position);

        Button buttonShare = ((GoalAcheivedViewHolder) holder).buttonShare;
        Button buttonApplaud = ((GoalAcheivedViewHolder) holder).buttonApplaud;
        TextView postText = ((GoalAcheivedViewHolder) holder).postText;
        TextView timeText = ((GoalAcheivedViewHolder) holder).timeText;
        CardView donationPostCard = ((GoalAcheivedViewHolder) holder).donationPostCard;


        FirebaseDatabase.getInstance().getReference("Users").child(post.user).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    User u = snapshot.getValue(User.class);
                    postText.setText("@" + u.username + " acheived their goal for " + post.charity + "!");

                    LocalDateTime dt = LocalDateTime.ofInstant(Instant.ofEpochMilli(post.timestamp), TimeZone.getDefault().toZoneId());
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mm a ' ' MM.dd");
                    timeText.setText(formatter.format(dt) + "  -  " + Integer.toString(post.numApplauds) + " Claps");

                    applaudListener(holder, buttonApplaud, post);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void bindMatchView(@NonNull RecyclerView.ViewHolder holder, int position) {
        Post post = posts.get(position);

        Button buttonShare = ((MatchViewHolder) holder).buttonShare;
        Button buttonApplaud = ((MatchViewHolder) holder).buttonApplaud;
        TextView postText = ((MatchViewHolder) holder).postText;
        TextView timeText = ((MatchViewHolder) holder).timeText;
        CardView donationPostCard = ((MatchViewHolder) holder).donationPostCard;



        FirebaseDatabase.getInstance().getReference("Users").child(post.user).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    User u = snapshot.getValue(User.class);
                    postText.setText("@" + u.username + " matched a dontation to " + post.charity + "!");

                    LocalDateTime dt = LocalDateTime.ofInstant(Instant.ofEpochMilli(post.timestamp), TimeZone.getDefault().toZoneId());
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mm a ' ' MM.dd");
                    timeText.setText(formatter.format(dt) + "  -  " + Integer.toString(post.numApplauds) + " Claps");

                    applaudListener(holder, buttonApplaud, post);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
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

}
