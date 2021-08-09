package edu.neu.charitable.fragments;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import edu.neu.charitable.R;
import edu.neu.charitable.models.Charity;
import edu.neu.charitable.models.User;
import edu.neu.charitable.utils.CharitiesRecyclerViewAdapter;
import edu.neu.charitable.utils.UsersRecyclerViewAdapter;

public class Search extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private ArrayList<User> users;
    private RecyclerView rvUsers;
    private TextView query;
    private Button button;
    private FirebaseDatabase mDB;
    UsersRecyclerViewAdapter adapter;

    private ArrayList<Charity> chars;
    private RecyclerView rvChars;
    CharitiesRecyclerViewAdapter adapterC;

    private ProgressBar progressBar;

    public Search() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static Search newInstance(String param1, String param2) {
        Search fragment = new Search();
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
        users = new ArrayList<>();
        chars = new ArrayList<>();
        mDB = FirebaseDatabase.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_search, container, false);

        query = view.findViewById(R.id.search_query);
        button = view.findViewById(R.id.search_button);
        button.setOnClickListener(this::searchGo);

        progressBar = view.findViewById(R.id.progressBar_search);

        //set up user recycler view
        rvUsers = view.findViewById(R.id.search_users_rv);
        rvUsers.hasFixedSize();
        rvUsers.setLayoutManager(new LinearLayoutManager(view.getContext()));
        adapter = new UsersRecyclerViewAdapter(users);
        rvUsers.setAdapter(adapter);

        //set up charity recycler view
        rvChars = view.findViewById(R.id.search_charities_rv);
        rvChars.hasFixedSize();
        rvChars.setLayoutManager(new LinearLayoutManager(view.getContext()));
        adapterC = new CharitiesRecyclerViewAdapter(chars);
        rvChars.setAdapter(adapterC);

        return view;
    }

    public void searchGo(View view) {
        String searchFor = query.getText().toString();

        if (!searchFor.isEmpty()) {
            users.clear();
            chars.clear();
            progressBar.setVisibility(View.VISIBLE);

            //search for users with full name
            mDB.getReference("Users").orderByChild("fullName").equalTo(searchFor)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {

                                ArrayList<User> found = new ArrayList<User>();
                                for (DataSnapshot ds: snapshot.getChildren()) {
                                    found.add(ds.getValue(User.class));
                                }
                                users.addAll(found);
                                adapter.notifyDataSetChanged();
                                progressBar.setVisibility(View.GONE);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });


            //search for users by username
            mDB.getReference("username_id").child(searchFor).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    //username exists in db
                    if (snapshot.exists()) {
                        String id = snapshot.getValue(String.class);

                        if (!id.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                            mDB.getReference("Users").child(id).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.exists()) {
                                        users.add(snapshot.getValue(User.class));
                                        adapter.notifyDataSetChanged();
                                        progressBar.setVisibility(View.GONE);
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

            //search for users with city
            mDB.getReference("Users").orderByChild("city").equalTo(searchFor)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                ArrayList<User> found = new ArrayList<User>();
                                for (DataSnapshot ds: snapshot.getChildren()) {
                                    found.add(ds.getValue(User.class));
                                }
                                users.addAll(found);
                                adapter.notifyDataSetChanged();
                                progressBar.setVisibility(View.GONE);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

            //search for charities with name
            mDB.getReference("Charities").orderByChild("name").equalTo(searchFor)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                ArrayList<Charity> foundC = new ArrayList<>();
                                for (DataSnapshot ds: snapshot.getChildren()) {
                                    foundC.add(ds.getValue(Charity.class));
                                }
                                chars.addAll(foundC);
                                adapterC.notifyDataSetChanged();
                                progressBar.setVisibility(View.GONE);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

            //search for charities with ein
            mDB.getReference("Charities").orderByChild("ein").equalTo(searchFor)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                ArrayList<Charity> foundC = new ArrayList<>();
                                for (DataSnapshot ds: snapshot.getChildren()) {
                                    foundC.add(ds.getValue(Charity.class));
                                }
                                chars.addAll(foundC);
                                adapterC.notifyDataSetChanged();
                                progressBar.setVisibility(View.GONE);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

            //search for charities with city
            mDB.getReference("Charities").orderByChild("city").equalTo(searchFor)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                ArrayList<Charity> foundC = new ArrayList<>();
                                for (DataSnapshot ds: snapshot.getChildren()) {
                                    foundC.add(ds.getValue(Charity.class));
                                }
                                chars.addAll(foundC);
                                adapterC.notifyDataSetChanged();
                                progressBar.setVisibility(View.GONE);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

            //search for charities with state
            mDB.getReference("Charities").orderByChild("state").equalTo(searchFor)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                ArrayList<Charity> foundC = new ArrayList<>();
                                for (DataSnapshot ds: snapshot.getChildren()) {
                                    foundC.add(ds.getValue(Charity.class));
                                }
                                chars.addAll(foundC);
                                adapterC.notifyDataSetChanged();
                                progressBar.setVisibility(View.GONE);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

            //search for charities with country
            mDB.getReference("Charities").orderByChild("country").equalTo(searchFor)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                ArrayList<Charity> foundC = new ArrayList<>();
                                for (DataSnapshot ds: snapshot.getChildren()) {
                                    foundC.add(ds.getValue(Charity.class));
                                }
                                chars.addAll(foundC);
                                adapterC.notifyDataSetChanged();
                                progressBar.setVisibility(View.GONE);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

            //search for charities with countryCode
            mDB.getReference("Charities").orderByChild("countryCode").equalTo(searchFor)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                ArrayList<Charity> foundC = new ArrayList<>();
                                for (DataSnapshot ds: snapshot.getChildren()) {
                                    foundC.add(ds.getValue(Charity.class));
                                }
                                chars.addAll(foundC);
                                adapterC.notifyDataSetChanged();
                                progressBar.setVisibility(View.GONE);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });



        } else {
            query.setError("Need to Search Something");
            query.requestFocus();
        }
    }

}