package edu.neu.charitable;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class PreLoginHomepage extends AppCompatActivity {

    private DatabaseReference database;
    private FirebaseAuth auth;
    // private String loggedInUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.prelogin_homepage);

        this.auth = FirebaseAuth.getInstance();
        this.database = FirebaseDatabase.getInstance().getReference();
    }

    public void LogIn(View view) {
        EditText username = findViewById(R.id.username);
        EditText password = findViewById(R.id.password);
        String usernameText = username.getText().toString();
        String passwordText = password.getText().toString();

        this.auth.signInWithEmailAndPassword(usernameText, passwordText).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    // successful login
                    launchLoggedInHomepage(view);
                } else {
                    // Toast error message
                    Toast.makeText(getApplicationContext(),"Incorrect Username or Password",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void launchLoggedInHomepage(View view) {
        startActivity(new Intent(this, LoggedInHomepage.class));
    }

    public void launchCreateAccount(View view) {
        startActivity(new Intent(this, CreateAccount.class));
    }
}
