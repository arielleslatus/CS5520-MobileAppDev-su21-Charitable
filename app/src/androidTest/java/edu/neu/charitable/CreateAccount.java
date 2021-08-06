package edu.neu.charitable;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;

public class CreateAccount extends AppCompatActivity {

    private static final String TAG = CreateAccount.class.getSimpleName();

    private DatabaseReference database;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_account);

        this.auth = FirebaseAuth.getInstance();
        this.database = FirebaseDatabase.getInstance().getReference();

        //Utils.writeUsername(CreateAccount.this, "");
        //Intent intent = getIntent();
        //startActivity(intent);
    }

    /*
    Creates a new user account.
     */
    public void createNewAccount(View view) {
        EditText username = findViewById(R.id.newUsername);
        EditText password = findViewById(R.id.newPassword);
        String usernameText = username.getText().toString();
        String passwordText = password.getText().toString();

        this.auth.createUserWithEmailAndPassword(usernameText, passwordText).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    task.getResult().getUser();
                    launchLoggedInHomepage(view);
                }
            }
        });
    }

    public void launchLoggedInHomepage(View view) {
        startActivity(new Intent(this, LoggedInHomepage.class));
    }










    // If we need this to let users alter their username and password?
    private void onChangeUserInfo(DatabaseReference postRef, String user) {
        postRef.child("users").child(user).runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {

                User user = mutableData.getValue(User.class);
                if (user == null) {
                    return Transaction.success(mutableData);
                }

                user.resetUsername(String.valueOf(Integer.valueOf(user.getUsername())));
                user.resetPassword(String.valueOf(Integer.valueOf(user.getPassword())));

                mutableData.setValue(user);

                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b,
                                   DataSnapshot dataSnapshot) {
                // Transaction completed
                Log.d(TAG, "postTransaction:onComplete:" + databaseError);
                Toast.makeText(getApplicationContext()
                        , "DBError: " + databaseError, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
