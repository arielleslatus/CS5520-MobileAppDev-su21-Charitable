package edu.neu.charitable.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Comparator;

import edu.neu.charitable.R;
import edu.neu.charitable.models.Donation;
import edu.neu.charitable.models.Post;
import edu.neu.charitable.utils.DirectDonationRecyclerViewAdapter;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DirectDonationTimeline#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DirectDonationTimeline extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private ArrayList<Donation> donations;
    private RecyclerView rvDonations;
    private FirebaseDatabase mDb;
    private DirectDonationRecyclerViewAdapter adapter;

    private String username;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public DirectDonationTimeline() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DirectDonationTimeline.
     */
    // TODO: Rename and change types and number of parameters
    public static DirectDonationTimeline newInstance(String param1, String param2) {
        DirectDonationTimeline fragment = new DirectDonationTimeline();
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

        donations = new ArrayList<Donation>();
        mDb = FirebaseDatabase.getInstance();
        username = FirebaseAuth.getInstance().getCurrentUser().getUid();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_direct_donation_timeline, container, false);

        rvDonations = view.findViewById(R.id.dd_feed_rv);
        rvDonations.hasFixedSize();
        rvDonations.setLayoutManager(new LinearLayoutManager(view.getContext()));
        adapter = new DirectDonationRecyclerViewAdapter(donations);
        rvDonations.setAdapter(adapter);

        load(view);
        return view;
    }

    private void load(View view) {
        mDb.getReference("pool_received").child(username).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        donations.add(ds.getValue(Donation.class));
                    }
                    donations.sort(new Comparator<Donation>() {
                        @Override
                        public int compare(Donation o1, Donation o2) {
                            return Long.compare(o2.timestamp, o1.timestamp);
                        }
                    });
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}