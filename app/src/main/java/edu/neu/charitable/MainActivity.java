package edu.neu.charitable;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.neu.charitable.models.Charity;
import edu.neu.charitable.models.CharityString;
import edu.neu.charitable.models.User;
import edu.neu.charitable.utils.NotificationListener;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView register, forgotPassword;
    private EditText editTextEmail, editTextPassword;
    private Button signIn;
    private Button debug;

    private FirebaseAuth mAuth;
    private ProgressBar progressBar;

    private static final String TAG = "MainActivity";

    private void createNotificationChannelReminder(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel notificationChannel = new NotificationChannel("reminder", "Reminder", NotificationManager.IMPORTANCE_DEFAULT );
            notificationChannel.setDescription("Donation Reminder");
            notificationChannel.setShowBadge(true);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        createNotificationChannelReminder();

        //theming doesn't seem to turn off night mode (ugly colors persist)
        //later figure out how to set second color scheme
        //this is a bad solution as it recreation of main activity
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        setContentView(R.layout.activity_main);

        // this is my hacky solution to populate free version of Firebase with included csv
        // to be removed after all charities are populated
        // !!! all charities in free version of database cause unexpected behavior in browser view
        // and possibly app behavior (latter unverified)
        //addCharities();

        //this code is to check if app is opened from deep link
        FirebaseDynamicLinks.getInstance()
                .getDynamicLink(getIntent())
                .addOnSuccessListener(this, new OnSuccessListener<PendingDynamicLinkData>() {
                    @Override
                    public void onSuccess(PendingDynamicLinkData pendingDynamicLinkData) {
                        // Get deep link from result (may be null if no link is found)
                        Uri deepLink = null;
                        if (pendingDynamicLinkData != null) {
                            deepLink = pendingDynamicLinkData.getLink();

                            String newUser = deepLink.getQueryParameter("newUser");
                            if (newUser != null) {
                                Intent intent = new Intent(getApplicationContext(), RegisterForPool.class);
                                intent.putExtra("LINK_INFO", newUser);
                                startActivity(intent);
                            }
                        }

                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainActivity.this, "failed to get link", Toast.LENGTH_LONG).show();
                    }
                });

        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        //Look for stored info
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        String email = preferences.getString("email", null);
        String password = preferences.getString("password", null);
        if (email != null && !email.equals("") && password != null && !password.equals("")) {
            progressBar.setVisibility(View.VISIBLE);
            //FireBaseBackgroundService.startActionFoo(MainActivity.this, email, password);
            FireBaseBackgroundService.startActionBaz(MainActivity.this, email, password);
            NotificationListener.startActionFoo(MainActivity.this, email, password);
            autoLogin(email, password);
        }

        register = (TextView) findViewById(R.id.register);
        register.setOnClickListener(this);

        signIn = (Button) findViewById(R.id.signIn);
        signIn.setOnClickListener(this);

        editTextEmail = (EditText) findViewById(R.id.email);
        editTextPassword = (EditText) findViewById(R.id.password);

        mAuth = FirebaseAuth.getInstance();

        forgotPassword = (TextView) findViewById(R.id.forgotPassword);
        forgotPassword.setOnClickListener(this);

        debug = (Button) findViewById(R.id.debugButton);
        debug.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.register:
                startActivity(new Intent(this, RegisterUser.class));
                break;

            case R.id.signIn:
                userLogin();
                break;

            case R.id.forgotPassword:
                startActivity(new Intent(this, ForgotPassword.class));
                break;

            case R.id.debugButton:
                Log.d(TAG, "Clicked debug button");
                Intent debugIntent = new Intent(this, CharityProfile.class);
                Bundle extras = new Bundle();
                extras.putString("uid", "AoIjpYofuxVFzgL1JpcwOx7P7hv2");
                extras.putString("charityID", "MgWVicf0bKkl4Frwt59");
                debugIntent.putExtras(extras);
                startActivity(debugIntent);

                break;
        }

    }

    private void autoLogin(String email, String password) {
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()) {
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    if (user.isEmailVerified()) {
                        //Check if this user is normal or part of donation pool
                        FirebaseDatabase.getInstance().getReference("user_pool").child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    Boolean u = snapshot.getValue(Boolean.class);
                                    if (u != null) {
                                        startActivity(new Intent(MainActivity.this, DirectDonationHome.class));
                                    } else {
                                        startActivity(new Intent(MainActivity.this, Home.class));
                                    }
                                } else {
                                    startActivity(new Intent(MainActivity.this, Home.class));
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                startActivity(new Intent(MainActivity.this, Home.class));
                            }
                        });
                    } else {
                        user.sendEmailVerification();
                        Toast.makeText(MainActivity.this, "Please check your email to " +
                                "verify your account!", Toast.LENGTH_LONG).show();
                        progressBar.setVisibility(View.GONE);
                    }

                } else {
                    Toast.makeText(MainActivity.this, "Failed to login, please check your" +
                            "credentials", Toast.LENGTH_LONG).show();
                    progressBar.setVisibility(View.GONE);
                }

            }
        });
    }

    private void userLogin() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if (email.isEmpty()) {
            editTextEmail.setError("Please type your email!");
            editTextEmail.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextEmail.setError("Please enter a valid email!");
            editTextEmail.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            editTextPassword.setError("Please type your password!");
            editTextPassword.requestFocus();
            return;
        }

        if (password.length() < 6) {
            editTextPassword.setError("Min password length is 6 characters! Please try again!");
            editTextPassword.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(View.GONE);
            }
        }, 2000);


        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()) {
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    if (user.isEmailVerified()) {

                        //Save login
                        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
                        SharedPreferences.Editor ed = preferences.edit();
                        ed.putString("email", email);
                        ed.putString("password", password);
                        ed.apply();

                        NotificationListener.startActionFoo(MainActivity.this, email, password);


                        //Check if this user is normal or part of donation pool
                        FirebaseDatabase.getInstance().getReference("user_pool").child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    Boolean u = snapshot.getValue(Boolean.class);
                                    if (u != null) {
                                        startActivity(new Intent(MainActivity.this, DirectDonationHome.class));
                                    } else {
                                        startActivity(new Intent(MainActivity.this, Home.class));
                                    }
                                } else {
                                    startActivity(new Intent(MainActivity.this, Home.class));
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                startActivity(new Intent(MainActivity.this, Home.class));
                            }
                        });
                    } else {
                        user.sendEmailVerification();
                        Toast.makeText(MainActivity.this, "Please check your email to " +
                                "verify your account!", Toast.LENGTH_LONG).show();
                        progressBar.setVisibility(View.GONE);
                    }

                } else {
                    Toast.makeText(MainActivity.this, "Failed to login, please check your" +
                            "credentials", Toast.LENGTH_LONG).show();
                    progressBar.setVisibility(View.GONE);
                }

            }
        });
    }

    public void navigateToShake(View view) {
        startActivity(new Intent(this, Shake.class));
    }


    /**
     * Populates charities to database. Too many for in csv, so only populating 10 to test.
     */
    /*
    public void addCharities() {
        try {
            InputStream inputStream = getResources().openRawResource(R.raw.verified_charity_list);
            MappingIterator<CharityString> charIter = new CsvMapper().readerWithTypedSchemaFor(CharityString.class).readValues(inputStream);
            List<CharityString> charities = charIter.readAll();

            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Charities");
            int num_actual = 10;
            for (CharityString c : charities) {
                Charity c2 = new Charity(c);
                ref.push().setValue(c2);
                num_actual -= 1;
                if (num_actual <= 0) {
                    break;
                }
            }
        } catch (Exception e) {
            Toast.makeText(MainActivity.this, e.toString(), Toast.LENGTH_LONG).show();
        }
    }
    *
     */
}