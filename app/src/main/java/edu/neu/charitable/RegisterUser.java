package edu.neu.charitable;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import edu.neu.charitable.models.User;

public class RegisterUser extends AppCompatActivity implements View.OnClickListener {

    private TextView banner, registerUser;
    private EditText editTextFullName, editTextCity, editTextEmail, editTextPassword, editTextRepeat, editTextUsername;
    private ProgressBar progressBar;

    private FirebaseAuth mAuth;
    private boolean usernameVerified;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);

        mAuth = FirebaseAuth.getInstance();

        banner = (TextView) findViewById(R.id.banner);
        banner.setOnClickListener(this);

        registerUser = (Button) findViewById(R.id.registerUser);
        registerUser.setOnClickListener(this);

        editTextFullName = (EditText) findViewById(R.id.fullName);
        editTextCity = (EditText) findViewById(R.id.city);
        editTextEmail = (EditText) findViewById(R.id.email);
        editTextPassword = (EditText) findViewById(R.id.password);
        editTextRepeat = (EditText) findViewById(R.id.password_match);
        editTextUsername = (EditText) findViewById(R.id.username);
        usernameVerified = false;


        progressBar = (ProgressBar) findViewById(R.id.progressBar);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.banner:
                startActivity(new Intent(this, MainActivity.class));
                break;
            case R.id.registerUser:
                registerUser();
                break;
        }

    }

    private void registerUser() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String fullName = editTextFullName.getText().toString().trim();
        String city = editTextCity.getText().toString().trim();
        String password_match = editTextRepeat.getText().toString().trim();
        String username = editTextUsername.getText().toString().trim();

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

        if (!usernameVerified) {
            editTextUsername.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()) {
                    User user = new User(fullName, city, email, username);

                    FirebaseDatabase.getInstance().getReference("Users")
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()) {
                                //adding username to searchable table (verify unique/find friends)
                                FirebaseDatabase.getInstance()
                                        .getReference("username_id")
                                        .child(username)
                                        .setValue(
                                                FirebaseAuth.getInstance()
                                                        .getCurrentUser()
                                                        .getUid())
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        mAuth.getCurrentUser().sendEmailVerification();
                                        Toast.makeText(RegisterUser.this,
                                                "username updated",
                                                Toast.LENGTH_SHORT).show();
                                    }});


                                Toast.makeText(RegisterUser.this, "User has been " +
                                        "registered successfully!", Toast.LENGTH_LONG).show();
                                progressBar.setVisibility(View.VISIBLE);
                                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        progressBar.setVisibility(View.GONE);
                                    }
                                }, 2000);




                            } else {
                                Toast.makeText(RegisterUser.this, "Failed to register!" +
                                        "Try again!", Toast.LENGTH_LONG).show();
                                progressBar.setVisibility(View.GONE);

                            }
                        }
                    });

                } else {
                    Toast.makeText(RegisterUser.this, "Failed to register!" +
                            "Try again!", Toast.LENGTH_LONG).show();
                    progressBar.setVisibility(View.GONE);
                }
            }


            });


    }

    public void verifyUsername(View view) {
        String username = editTextUsername.getText().toString().trim();
        FirebaseDatabase.getInstance().getReference("username_id").child(username).addListenerForSingleValueEvent(new ValueEventListener() {
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
