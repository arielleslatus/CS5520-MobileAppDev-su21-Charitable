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
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import edu.neu.charitable.R;
import edu.neu.charitable.models.User;
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

        rvUsers = view.findViewById(R.id.search_users_rv);
        rvUsers.hasFixedSize();
        rvUsers.setLayoutManager(new LinearLayoutManager(view.getContext()));
        adapter = new UsersRecyclerViewAdapter(users);
        rvUsers.setAdapter( adapter);
        rvUsers.setLayoutManager(new LinearLayoutManager(view.getContext()));
        return view;
    }

    public void searchGo(View view) {
        String searchFor = query.getText().toString();
        ArrayList<User> found = new ArrayList<User>();
        if (!searchFor.isEmpty()) {
            users.clear();
            mDB.getReference("Users").orderByChild("name").equalTo(searchFor)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                for (DataSnapshot ds: snapshot.getChildren()) {
                                    found.add(ds.getValue(User.class));
                                }
                                users.addAll(found);
                                adapter.notifyDataSetChanged();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

            mDB.getReference("username_id").child(searchFor).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    //username exists in db
                    if (snapshot.exists()) {
                        String id = snapshot.getValue(String.class);
                        mDB.getReference("Users").child(id).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    users.add(snapshot.getValue(User.class));
                                    adapter.notifyDataSetChanged();
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

        } else {
            query.setError("Need to Search Something");
            query.requestFocus();
        }
    }

}