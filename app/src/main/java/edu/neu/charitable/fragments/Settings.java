package edu.neu.charitable.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import edu.neu.charitable.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Settings#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Settings extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private FirebaseUser user;
    private DatabaseReference reference;
    private DatabaseReference displayName;
    private DatabaseReference displayFavoriteCharities;
    private DatabaseReference displayDonations;
    private String userID;
    private FirebaseAuth mAuth;
    private Switch displayNameSwitch;
    private Switch displayFavoriteCharitiesSwitch;
    private Switch displayDonationsSwitch;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Settings() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Settings.
     */
    // TODO: Rename and change types and number of parameters
    public static Settings newInstance(String param1, String param2) {
        Settings fragment = new Settings();
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

        this.user = FirebaseAuth.getInstance().getCurrentUser();
        this.reference = FirebaseDatabase.getInstance().getReference("Preferences");
        this.userID = user.getUid();
        this.mAuth = FirebaseAuth.getInstance();


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view =  inflater.inflate(R.layout.fragment_settings, container, false);

        // Get the paths from firebase.
        this.displayName = FirebaseDatabase.getInstance().getReference("Preferences/" + userID + "/displayName");
        this.displayFavoriteCharities = FirebaseDatabase.getInstance().getReference("Preferences/" + userID + "/displayFavoriteCharities");
        this.displayDonations = FirebaseDatabase.getInstance().getReference("Preferences/" + userID + "/displayDonations");

        // Get the Switches from the layout.
        this.displayNameSwitch = view.findViewById(R.id.displayNameSwitch);
        this.displayFavoriteCharitiesSwitch = view.findViewById(R.id.displayFavoriteCharitiesSwitch);
        this.displayDonationsSwitch = view.findViewById(R.id.displayDonationsSwitch);

        addListener(view);


        return view;
    }


    private void addListener(View view) {
        // Listener for displayName in Firebase. If true, display as checked.
        this.displayName.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Object displayNamePreference = dataSnapshot.getValue(Object.class);
                if (displayNamePreference != null) {
                    if ((Boolean) displayNamePreference == true) {
                        displayNameSwitch.setChecked(true);
                    } else {
                        displayNameSwitch.setChecked(false);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });

        // Listener for displayFavoriteCharities in Firebase. If true, display as checked.
        this.displayFavoriteCharities.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Object displayFavoriteCharitiesPreference = dataSnapshot.getValue(Object.class);
                if (displayFavoriteCharitiesPreference != null) {
                    if ((Boolean) displayFavoriteCharitiesPreference == true) {
                        displayFavoriteCharitiesSwitch.setChecked(true);
                    } else {
                        displayFavoriteCharitiesSwitch.setChecked(false);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });

        // Listener for displayDonations in Firebase. If true, display as checked.
        this.displayDonations.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Object displayDonationsPreference = dataSnapshot.getValue(Object.class);
                if (displayDonationsPreference != null) {
                    if ((Boolean) displayDonationsPreference == true) {
                        displayDonationsSwitch.setChecked(true);
                    } else {
                        displayDonationsSwitch.setChecked(false);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });

        // Listener for changes to Display Name Switch.
        this.displayNameSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    reference.child(userID).child("displayName").setValue(true).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                //Toast.makeText(getActivity(), "Display Name has been enabled.", Toast.LENGTH_LONG).show();
                            } else {
                                //Toast.makeText(getActivity(), "Could not enable Display Name.", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                } else {
                    reference.child(userID).child("displayName").setValue(false).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                //Toast.makeText(getActivity(), "Display Name has been disabled.", Toast.LENGTH_LONG).show();
                            } else {
                               // Toast.makeText(getActivity(), "Could not disable Display Name.", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            }
        });

        // Listener for changes to Display Favorite Charities Switch.
        this.displayFavoriteCharitiesSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    reference.child(userID).child("displayFavoriteCharities").setValue(true).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                //Toast.makeText(getActivity(), "Display Favorite Charities has been enabled.", Toast.LENGTH_LONG).show();
                            } else {
                                //Toast.makeText(getActivity(), "Can not enable Display Favorite Charities at this time.", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                } else {
                    reference.child(userID).child("displayFavoriteCharities").setValue(false).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                //Toast.makeText(getActivity(), "Display Favorite Charities has been disabled.", Toast.LENGTH_LONG).show();
                            } else {
                                //Toast.makeText(getActivity(), "Can not disable Display Favorite Charities at this time.", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            }
        });

        // Listener for changes to Display Donations Switch.
        this.displayDonationsSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    reference.child(userID).child("displayDonations").setValue(true).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                //Toast.makeText(getActivity(), "Display Donations has been enabled.", Toast.LENGTH_LONG).show();
                            } else {
                                //Toast.makeText(getActivity(), "Can not enable Display Donations at this time.", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                } else {
                    reference.child(userID).child("displayDonations").setValue(false).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                               // Toast.makeText(getActivity(), "Display Donations has been disabled.", Toast.LENGTH_LONG).show();
                            } else {
                               // Toast.makeText(getActivity(), "Can not disable Display Donations at this time.", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            }
        });
    }
}