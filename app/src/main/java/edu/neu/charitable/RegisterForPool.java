package edu.neu.charitable;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;

import edu.neu.charitable.models.User;

public class RegisterForPool extends AppCompatActivity implements View.OnClickListener {

    private TextView banner, registerUser;
    private EditText editTextFullName, editTextCity, editTextEmail, editTextPassword, editTextRepeat, editTextUsername, venmoId;
    private ProgressBar progressBar;

    private FirebaseAuth mAuth;
    private FirebaseDatabase mDb;
    private boolean usernameVerified;

    private String link_code;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_for_pool);

        mAuth = FirebaseAuth.getInstance();
        mDb = FirebaseDatabase.getInstance();

        banner = (TextView) findViewById(R.id.rfp_banner);
        banner.setOnClickListener(this);

        registerUser = (Button) findViewById(R.id.rfp_registerUser);
        registerUser.setOnClickListener(this);

        editTextFullName = (EditText) findViewById(R.id.rfp_fullName);
        editTextCity = (EditText) findViewById(R.id.rfp_city);
        editTextEmail = (EditText) findViewById(R.id.rfp_email);
        editTextPassword = (EditText) findViewById(R.id.rfp_password);
        editTextRepeat = (EditText) findViewById(R.id.rfp_password_match);
        editTextUsername = (EditText) findViewById(R.id.rfp_username);
        usernameVerified = false;
        venmoId = (EditText) findViewById(R.id.venmoID);


        progressBar = (ProgressBar) findViewById(R.id.rfp_progressBar);

        Intent intent = getIntent();
        link_code = intent.getStringExtra("LINK_INFO");

        checkLinkData(intent);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent != null) {
            //checkLinkData(intent, "called from onNewIntent");
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (getIntent() != null) {
            //checkLinkData(getIntent(), "called from onNewIntent");
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rfp_banner:
                startActivity(new Intent(this, MainActivity.class));
                break;
            case R.id.rfp_registerUser:
                this.registerUser();
                break;
        }

    }

    private void checkLinkData(Intent intent) {
        FirebaseDynamicLinks.getInstance()
                .getDynamicLink(getIntent())
                .addOnSuccessListener(this, new OnSuccessListener<PendingDynamicLinkData>() {
                    @Override
                    public void onSuccess(PendingDynamicLinkData pendingDynamicLinkData) {
                        // Get deep link from result (may be null if no link is found)
                        Uri deepLink = null;
                        if (pendingDynamicLinkData != null) {
                            deepLink = pendingDynamicLinkData.getLink();
                            if (deepLink != null) {
                                link_code = deepLink.getQueryParameter("newUser");
                            }
                        }
                    }
                });
    }

    private void registerUser() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String fullName = editTextFullName.getText().toString().trim();
        String city = editTextCity.getText().toString().trim();
        String password_match = editTextRepeat.getText().toString().trim();
        String username = editTextUsername.getText().toString().trim();
        String venmo = venmoId.getText().toString().trim();

        if (fullName.isEmpty()) {
            editTextFullName.setError("Full name is required!");
            editTextFullName.requestFocus();
            return;
        }

        if (city.isEmpty()) {
            editTextCity.setError("City is required!");
            editTextCity.requestFocus();
            return;
        }

        if (email.isEmpty()) {
            editTextEmail.setError("Email is required!");
            editTextEmail.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextEmail.setError("Please provide valid email!");
            editTextEmail.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            editTextPassword.setError("Password is required!");
            editTextPassword.requestFocus();
            return;
        }

        if (password.length() < 6) {
            editTextPassword.setError("Password length should be at least 6 characters!");
            editTextPassword.requestFocus();
            return;
        }

        if (!password.equals(password_match)) {
            editTextRepeat.setError("Passwords must match");
            editTextRepeat.requestFocus();
            return;
        }

        if (username.isEmpty()) {
            editTextUsername.setError("username Unverified");
            editTextUsername.requestFocus();
            return;
        }

        if (venmo.isEmpty()) {
            venmoId.setError("Need a venmo id");
            venmoId.requestFocus();
            return;
        }

        if (!usernameVerified) {
            editTextUsername.requestFocus();
            return;
        }

        if (link_code == null) {
            Toast.makeText(RegisterForPool.this, "link code is not found", Toast.LENGTH_LONG).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        //check that code is valid
        mDb.getReference("code_valid").child(link_code).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Boolean is_valid = snapshot.getValue(Boolean.class);
                    if (is_valid != null && is_valid) {
                        //create Auth Registration
                        mAuth.createUserWithEmailAndPassword(email, password)
                                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {

                                        if (task.isSuccessful()) {

                                            User user = new User(fullName, city, email, username);

                                            //create a user
                                            mDb.getReference("Users")
                                                    .child(mAuth.getCurrentUser().getUid())
                                                    .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {

                                                        //adding username to searchable table (verify unique/find friends)
                                                        mDb.getReference("username_id")
                                                                .child(username)
                                                                .setValue(
                                                                        mAuth.getCurrentUser()
                                                                                .getUid())
                                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                        //send email
                                                                        mAuth.getCurrentUser().sendEmailVerification();

                                                                        //mark user as a pool member
                                                                        mDb.getReference("user_pool").child(mAuth.getCurrentUser().getUid()).setValue(true);

                                                                        //add to pool of int to user so that random can select one without pulling all
                                                                        mDb.getReference("num_pool_users").addListenerForSingleValueEvent(new ValueEventListener() {
                                                                            @Override
                                                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                                if (snapshot.exists()) {
                                                                                    Integer num_users;
                                                                                    num_users = snapshot.getValue(Integer.class);
                                                                                    if (num_users != null) {
                                                                                        mDb.getReference("pool").child(Integer.toString(num_users + 1)).setValue(mAuth.getCurrentUser().getUid()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                            @Override
                                                                                            public void onSuccess(@NonNull Void unused) {
                                                                                                mDb.getReference("num_pool_users").setValue(Integer.toString(num_users + 1));
                                                                                                Toast.makeText(RegisterForPool.this, "added to pool", Toast.LENGTH_LONG).show();
                                                                                            }
                                                                                        });
                                                                                    }
                                                                                }
                                                                            }

                                                                            @Override
                                                                            public void onCancelled(@NonNull DatabaseError error) {

                                                                            }
                                                                        });

                                                                        //set code as used
                                                                        mDb.getReference("code_valid").child(link_code).setValue(false).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                            @Override
                                                                            public void onSuccess(@NonNull Void unused) {
                                                                                Toast.makeText(RegisterForPool.this, "You has been " +
                                                                                        "registered successfully!", Toast.LENGTH_LONG).show();

                                                                                progressBar.setVisibility(View.GONE);
                                                                            }
                                                                        });

                                                                        //add venmo id
                                                                        mDb.getReference("username_venmo").child(mAuth.getCurrentUser().getUid()).setValue(venmo);

                                                                    }});
                                                    } else {
                                                        Toast.makeText(RegisterForPool.this, "Failed to register!" +
                                                                "Try again!", Toast.LENGTH_LONG).show();
                                                        progressBar.setVisibility(View.GONE);

                                                    }
                                                }
                                            });

                                        } else {
                                            Toast.makeText(RegisterForPool.this, "Failed to register!" +
                                                    "Try again!", Toast.LENGTH_LONG).show();
                                            progressBar.setVisibility(View.GONE);
                                        }
                                    }


                                });

                    }
                } else {
                    Toast.makeText(RegisterForPool.this, "Registration code not found", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(RegisterForPool.this, "Unable to verify registration code", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void rfp_verifyUsername(View view) {
        String username = editTextUsername.getText().toString().trim();
        mDb.getReference("username_id").child(username).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //username exists in db
                if (snapshot.exists()) {
                    editTextUsername.setError("username must be unique");
                    editTextUsername.requestFocus();
                } else {
                    usernameVerified = true;
                    editTextUsername.setBackgroundColor(Color.GREEN);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}