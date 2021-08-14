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


    //Adds onclick listener to applaud buttons generated in recyclerView
    private void applaudListener(RecyclerView.ViewHolder holder, Button buttonApplaud, Post post) {
        buttonApplaud.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseDatabase.getInstance().getReference("user_posts")
                        .child(post.user)
                        .orderByChild("timestamp")
                        .equalTo(post.timestamp)
                        .addValueEventListener(new ValueEventListener() {
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

    private void bindDonationView(@NonNull RecyclerView.ViewHolder holder, int position) {
        Post post = posts.get(position);
        Log.d(TAG, "Making donation post: " + post.toString());

        Button buttonMatch = ((DonationViewHolder) holder).buttonMatch;
        Button buttonShare = ((DonationViewHolder) holder).buttonShare;
        Button buttonApplaud = ((DonationViewHolder) holder).buttonApplaud;
        TextView postTextView = ((DonationViewHolder) holder).postText;
        TextView timeText = ((DonationViewHolder) holder).timeText;
        CardView donationPostCard = ((DonationViewHolder) holder).donationPostCard;


        // Get currently logged in user from db, and fill in information in the post's Views.
        FirebaseDatabase.getInstance().getReference("Users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                Log.d(TAG, "onDataChange in getReference(\"Users\")");

                if (snapshot.exists()) {
                    Log.d(TAG, "Users Snapshot does exist");

                    // Figure out if we should use 1st or 3rd person based on logged in user.
                    String userText = "You";
                    User u = snapshot.getValue(User.class);
                    if (!post.user.equals(current_user)) {
                        Log.d(TAG, "Use 3rd person.");
                        String postUsername = post.user;
                        userText = "@" + postUsername;
                    }

                    Log.d(TAG, "... userText:" + userText);
                    String postTextContent = userText + " donated to " + post.charity + "!";
                    Log.d(TAG, "... postTextContent:" + postTextContent);


//                    // Create a clickable string where only the charity name will be clickable.
//                    SpannableString ss = new SpannableString(postTextContent);
//                    ClickableSpan clickableSpan = new ClickableSpan() {
//                        @Override
//                        public void onClick(View textView) {
//
//                            // On click of the charity name, start a new activity,
//                            // the charity profile of the charity that was clicked on
//                            Intent loadCharityIntent = new Intent(textView.getContext(), CharityProfile.class);
//                            Bundle extras = new Bundle();
//
//                            // Need to make an asynchronous call/not launch the activity
//                            // until we get the ID of the charity to launch it
//                            getCharityID(new MyCallback() {
//                                @Override
//                                public void onCallback(String value) {
//                                    String charityIDtoLaunch = value;
//
//                                    // Load the string into the intent and start the activity!
//                                    extras.putString("charityID", charityIDtoLaunch);
//                                    loadCharityIntent.putExtras(extras);
//                                    textView.getContext().startActivity(loadCharityIntent);
//                                }
//                            }, post.charity);
//
//                        }
//                        @Override
//                        public void updateDrawState(TextPaint ds) {
//                            super.updateDrawState(ds);
//                            ds.setUnderlineText(false);
//                        }
//                    };
//
//                    // Get location of where the charity name is, within the post text content. Make that clickable.
//                    int spanStart = userText.length() + 12;
//                    int spanEnd = post.charity.length() + spanStart;
//                    ss.setSpan(clickableSpan, spanStart, spanEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

                    // Set that as the text content.
//                    postTextView.setText(ss);
//                    postTextView.setMovementMethod(LinkMovementMethod.getInstance());

                    postTextView.setText(postTextContent);

                    LocalDateTime dt = LocalDateTime.ofInstant(Instant.ofEpochMilli(post.timestamp), TimeZone.getDefault().toZoneId());
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mm a ' ' MM.dd");
                    timeText.setText(formatter.format(dt) + "  -  " + Integer.toString(post.numApplauds) + " Claps");

                    applaudListener(holder, buttonApplaud, post);
                    matchOnClickListener(holder, buttonMatch, post);
                }

                else {
                    Log.d(TAG, "Users Snapshot does not exist");
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(((DonationViewHolder) holder).donationPostCard.getContext(), error.toString(), Toast.LENGTH_LONG).show();
            }
        });
    }


//    private void getCharityID(MyCallback myCallback, String charityName) {
//        Log.d(TAG, "Trying to find the ID for charity with name: " + charityName);
//        DatabaseReference referenceCharitiesDB = FirebaseDatabase.getInstance().getReference("Charities");
//        referenceCharitiesDB.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                Object charityDataSnapshot = dataSnapshot.getValue(Object.class);
//                if (charityDataSnapshot != null) {
//                    HashMap<String,Object> charityDB = (HashMap<String,Object>) charityDataSnapshot;
//
//                    Iterator it = charityDB.entrySet().iterator();
//                    while (it.hasNext()) {
//                        Map.Entry pair = (Map.Entry)it.next();
//                        Log.d(TAG, "Checking if this key-value pair is the charity chosen: " + pair.getKey().toString());
//                        String charityIDFromDB = pair.getKey().toString();
//                        HashMap<String,Object> charityInfoFromDB = (HashMap<String, Object>) pair.getValue();
//                        String charityNameFromDB = (String) charityInfoFromDB.get("name");
//
//                        if (charityNameFromDB.equals(charityName)) {
//                            Log.d(TAG, "Charity has been found in DB: " + charityName);
//                            String charityIDtoLaunch = pair.getKey().toString();
//                            myCallback.onCallback(charityIDtoLaunch);
//                            Log.d(TAG, "\tcharityIDtoLaunch: " + charityIDtoLaunch);
//                            break;
//                        }
//
//                        it.remove();
//                    }
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//                System.out.println("The read failed: " + databaseError.getCode());
//            }
//
//        });
//    }

}
