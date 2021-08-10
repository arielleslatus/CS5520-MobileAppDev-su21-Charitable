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
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import edu.neu.charitable.DonateDummy;
import edu.neu.charitable.R;
import edu.neu.charitable.SetGoal;
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
    private TextView goalPercent;
    private ProgressBar goalProgress;
    private Button goalSet;
    private Button donateOne;
    private Button donateTwo;

    private ArrayList<Post> posts;
    private RecyclerView rvPosts;
    private FirebaseDatabase mDb;
    private FeedRecyclerViewAdapter adapter;

    private Goal goal;

    private String username;

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

        username = FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_timeline_ats, container, false);
        charityImage = view.findViewById(R.id.charityImage);
        goalText = view.findViewById(R.id.goal_name);
        goalPercent = view.findViewById(R.id.goal_percent);
        goalProgress = view.findViewById(R.id.goal_bar);
        goalSet = view.findViewById(R.id.new_goal);

        donateOne = view.findViewById(R.id.donate_goal_one);
        donateTwo = view.findViewById(R.id.donate_goal_two);

        rvPosts = view.findViewById(R.id.feed_rv);
        rvPosts.hasFixedSize();
        rvPosts.setLayoutManager(new LinearLayoutManager(view.getContext()));
        adapter = new FeedRecyclerViewAdapter(posts);
        rvPosts.setAdapter(adapter);

        addListeners(view);

        load(view);

        return view;
    }

    private void addListeners(View view) {
        goalSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), SetGoal.class));
            }
        });
    }

    private void load(View view) {

        //Toast.makeText(getActivity(), username, Toast.LENGTH_LONG).show();

        //get friends and get all posts for each
        ArrayList<String> friends = new ArrayList<>();
        mDb.getReference("user_friends").child(username).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        friends.add(ds.getValue(String.class));
                        //Toast.makeText(getActivity(), "found friends", Toast.LENGTH_LONG).show();
                    }
                }

                for (String f : friends) {
                    mDb.getReference("user_posts").child(f).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot ds : snapshot.getChildren()) {
                                posts.add(ds.getValue(Post.class));
                            }

                            posts.sort(new Comparator<Post>() {
                                @Override
                                public int compare(Post o1, Post o2) {
                                    return Long.compare(o2.timestamp, o1.timestamp);
                                }
                            });
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
                    goal = snapshot.getValue(Goal.class);
                    if (goal != null) {
                        mDb.getReference("Charities").child(goal.charity).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    Charity ch = snapshot.getValue(Charity.class);
                                    goalText.setText("Donate $" + goal.amountSet + " to " + ch.name);
                                    goalPercent.setText(Integer.toString(Math.round( (goal.amoundDonated / goal.amountSet) * 100)) + "%");
                                    Picasso.get().load(ch.logoUrl).into(charityImage);
                                    goalProgress.setProgress( Math.round( (goal.amoundDonated / goal.amountSet) * 100));
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

        donateOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (goal != null) {

                    mDb.getReference("Charities").child(goal.charity).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                Charity c = snapshot.getValue(Charity.class);
                                Intent intent = new Intent(v.getContext(), DonateDummy.class);
                                intent.putExtra("AUTOFILL_CHARITY", c.name);
                                intent.putExtra("AUTOFILL_AMOUNT", Double.toString(5.0));
                                v.getContext().startActivity(intent);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                }
            }
        });

        donateTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (goal != null) {

                    mDb.getReference("Charities").child(goal.charity).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                Charity c = snapshot.getValue(Charity.class);
                                Intent intent = new Intent(v.getContext(), DonateDummy.class);
                                intent.putExtra("AUTOFILL_CHARITY", c.name);
                                intent.putExtra("AUTOFILL_AMOUNT", Double.toString(10.0));
                                v.getContext().startActivity(intent);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                }
            }
        });





    }



    //Picasso.get().load("http://i.imgur.com/DvpvklR.png").into(imageView);
}