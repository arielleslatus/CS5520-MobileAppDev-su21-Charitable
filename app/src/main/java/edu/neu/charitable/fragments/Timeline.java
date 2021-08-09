package edu.neu.charitable.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import edu.neu.charitable.DonateDummy;
import edu.neu.charitable.R;
import edu.neu.charitable.models.Charity;
import edu.neu.charitable.models.Goal;
import edu.neu.charitable.models.Post;
import edu.neu.charitable.utils.FeedRecyclerViewAdapter;
import okhttp3.internal.cache.DiskLruCache;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Timeline#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Timeline extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private ImageView charityImage;
    private TextView goalText;
    private ProgressBar goalProgress;

    private ArrayList<Post> posts;
    private RecyclerView rvPosts;
    private FirebaseDatabase mDb;
    FeedRecyclerViewAdapter adapter;

    public Timeline() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Timeline.
     */
    // TODO: Rename and change types and number of parameters
    public static Timeline newInstance(String param1, String param2) {
        Timeline fragment = new Timeline();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        posts = new ArrayList<>();
        mDb = FirebaseDatabase.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_timeline, container, false);
        charityImage = view.findViewById(R.id.charityImage);
        goalText = view.findViewById(R.id.goal_name);
        goalProgress = view.findViewById(R.id.goal_bar);

        rvPosts = view.findViewById(R.id.feed_rv);
        rvPosts.hasFixedSize();
        rvPosts.setLayoutManager(new LinearLayoutManager(view.getContext()));
        adapter = new FeedRecyclerViewAdapter(posts);
        rvPosts.setAdapter(adapter);

        return view;
    }

    private void load(View view) {
        String username = FirebaseAuth.getInstance().getCurrentUser().getUid();

        //get friends and get all posts for each
        ArrayList<String> friends = new ArrayList<>();
        mDb.getReference("user_friends").child(username).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        friends.add(ds.getValue(String.class));
                    }
                }

                for (String f : friends) {
                    mDb.getReference("user_posts").child(f).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot ds : snapshot.getChildren()) {
                                posts.add(ds.getValue(Post.class));
                            }
                            adapter.notifyDataSetChanged();
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

        mDb.getReference("user_goal").child(username).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Goal goal = snapshot.getValue(Goal.class);
                    if (goal != null) {
                        mDb.getReference("Charities").child(goal.charity).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    Charity ch = snapshot.getValue(Charity.class);
                                    goalText.setText("Donate " + goal.amountSet + " to " + ch.name);
                                    Picasso.get().load(ch.logoUrl).into(charityImage);
                                    goalProgress.setProgress( Math.round( (goal.amountSet / goal.amoundDonated) * 100));
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

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



    //Picasso.get().load("http://i.imgur.com/DvpvklR.png").into(imageView);
}