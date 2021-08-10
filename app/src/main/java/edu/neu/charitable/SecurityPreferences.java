package edu.neu.charitable;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SecurityPreferences extends AppCompatActivity {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_security_preferences);

        this.user = FirebaseAuth.getInstance().getCurrentUser();
        this.reference = FirebaseDatabase.getInstance().getReference("Preferences");
        this.userID = user.getUid();
        this.mAuth = FirebaseAuth.getInstance();

        // Get the paths from firebase.
        this.displayName = FirebaseDatabase.getInstance().getReference("Preferences/" + userID + "/displayName");
        this.displayFavoriteCharities = FirebaseDatabase.getInstance().getReference("Preferences/" + userID + "/displayFavoriteCharities");
        this.displayDonations = FirebaseDatabase.getInstance().getReference("Preferences/" + userID + "/displayDonations");

        // Get the Switches from the layout.
        this.displayNameSwitch = findViewById(R.id.displayNameSwitch);
        this.displayFavoriteCharitiesSwitch = findViewById(R.id.displayFavoriteCharitiesSwitch);
        this.displayDonationsSwitch = findViewById(R.id.displayDonationsSwitch);

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
                                Toast.makeText(SecurityPreferences.this, "Display Name has been enabled.", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(SecurityPreferences.this, "Could not enable Display Name.", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                } else {
                    reference.child(userID).child("displayName").setValue(false).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(SecurityPreferences.this, "Display Name has been disabled.", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(SecurityPreferences.this, "Could not disable Display Name.", Toast.LENGTH_LONG).show();
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
                                Toast.makeText(SecurityPreferences.this, "Display Favorite Charities has been enabled.", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(SecurityPreferences.this, "Can not enable Display Favorite Charities at this time.", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                } else {
                    reference.child(userID).child("displayFavoriteCharities").setValue(false).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(SecurityPreferences.this, "Display Favorite Charities has been disabled.", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(SecurityPreferences.this, "Can not disable Display Favorite Charities at this time.", Toast.LENGTH_LONG).show();
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
                                Toast.makeText(SecurityPreferences.this, "Display Donations has been enabled.", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(SecurityPreferences.this, "Can not enable Display Donations at this time.", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                } else {
                    reference.child(userID).child("displayDonations").setValue(false).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(SecurityPreferences.this, "Display Donations has been disabled.", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(SecurityPreferences.this, "Can not disable Display Donations at this time.", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            }
        });


    }

    public void navigateBackToProfile(View view) {
        startActivity(new Intent(this, ProfileActivity.class));
    }

    public void update(View view) {

    }



}
