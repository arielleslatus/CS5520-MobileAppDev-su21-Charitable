package edu.neu.charitable;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

import edu.neu.charitable.models.User;

public class UpDateProfile extends AppCompatActivity {

    public static final String TAG = "TAG";
    EditText profileFullName, profileCity, profileEmail, profileUsername;
    Button saveBtn;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    FirebaseUser user;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_up_date_profile);

        this.reference = FirebaseDatabase.getInstance().getReference("Users");

        Intent data = getIntent();
        String fullName = data.getStringExtra("fullName");
        String city = data.getStringExtra("city");
        String email = data.getStringExtra("email");
        String username = data.getStringExtra("username");

        this.fAuth = FirebaseAuth.getInstance();
        this.fStore = FirebaseFirestore.getInstance();
        this.user = this.fAuth.getCurrentUser();

        this.profileFullName = findViewById(R.id.fullName);
        this.profileCity = findViewById(R.id.city);
        this.profileEmail = findViewById(R.id.email);
        this.profileUsername = findViewById(R.id.usernameUpdate);
        this.saveBtn = findViewById(R.id.saveProfile);

        this.saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(profileCity.getText().toString().isEmpty() || profileFullName.getText().toString().isEmpty()
                || profileEmail.getText().toString().isEmpty() || profileUsername.getText().toString().isEmpty()){
                    Toast.makeText(UpDateProfile.this, "One or many fields are empty!", Toast.LENGTH_LONG).show();
                    return;
                }// this part is working

                final String email = profileEmail.getText().toString();

                user.updateEmail(email).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        User u = new User(profileFullName.getText().toString(), profileCity.getText().toString(), email, profileUsername.getText().toString());

                        reference.child(user.getUid()).setValue(u).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(UpDateProfile.this, "Profile Updated",
                                        Toast.LENGTH_LONG).show();
                                startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                                finish();

                            }
                        });

                        //Toast.makeText(UpDateProfile.this, "Email is changed", Toast.LENGTH_LONG).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(UpDateProfile.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });

            }
        });

        this.profileCity.setText(city);
        this.profileFullName.setText(fullName);
        this.profileEmail.setText(email);
        this.profileUsername.setText(username);

    }
}


