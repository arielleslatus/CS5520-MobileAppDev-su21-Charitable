package edu.neu.charitable;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private TextView register, forgotPassword;
    private EditText editTextEmail, editTextPassword;
    private Button signIn;
    private Button debug;

    private FirebaseAuth mAuth;
    private ProgressBar progressBar;

    private static final String TAG = "MainActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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

        register = (TextView) findViewById(R.id.register);
        register.setOnClickListener(this);

        signIn = (Button) findViewById(R.id.signIn);
        signIn.setOnClickListener(this);

        editTextEmail = (EditText) findViewById(R.id.email);
        editTextPassword = (EditText) findViewById(R.id.password);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        mAuth = FirebaseAuth.getInstance();

        forgotPassword = (TextView) findViewById(R.id.forgotPassword);
        forgotPassword.setOnClickListener(this);

        debug = (Button) findViewById(R.id.debugButton);
        debug.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.register:
                startActivity(new Intent(this,RegisterUser.class));
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

    private void userLogin() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if(email.isEmpty()) {
            editTextEmail.setError("Please type your email!");
            editTextEmail.requestFocus();
            return;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextEmail.setError("Please enter a valid email!");
            editTextEmail.requestFocus();
            return;
        }

        if(password.isEmpty()) {
            editTextPassword.setError("Please type your password!");
            editTextPassword.requestFocus();
            return;
        }

        if(password.length() < 6) {
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

                if(task.isSuccessful()) {
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    if(user.isEmailVerified()) {
                        startActivity(new Intent(MainActivity.this, Home.class));
                    }else{
                        user.sendEmailVerification();
                        Toast.makeText(MainActivity.this, "Please check your email to " +
                                "verify your account!", Toast.LENGTH_LONG).show();
                        progressBar.setVisibility(View.GONE);
                    }

                }else{
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