package edu.neu.charitable;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;

public class RegisterForPool extends AppCompatActivity {

    private TextView linkInfo;
    private String intentData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_for_pool);

        linkInfo = findViewById(R.id.linkInfo);

        Intent intent = getIntent();
        intentData = intent.getStringExtra("LINK_INFO");
        if (intentData == null) {
            intentData = "";
        }
        linkInfo.setText(intentData);

        /*
        Intent intent = getIntent();
        if (intent != null) {
            intentData = "";
            checkLinkData(intent, "called from onNewIntent");
        }*/
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

    private void checkLinkData(Intent intent, String calledFrom) {
        Toast.makeText(this, calledFrom, Toast.LENGTH_LONG).show();
        FirebaseDynamicLinks.getInstance()
                .getDynamicLink(getIntent())
                .addOnSuccessListener(this, new OnSuccessListener<PendingDynamicLinkData>() {
                    @Override
                    public void onSuccess(PendingDynamicLinkData pendingDynamicLinkData) {
                        // Get deep link from result (may be null if no link is found)
                        Uri deepLink = null;
                        if (pendingDynamicLinkData != null) {
                            deepLink = pendingDynamicLinkData.getLink();
                            linkInfo.setText(deepLink.toString() + " " + calledFrom);
                        } else {
                        }

                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        linkInfo.setText("failure" + " " + calledFrom);
                    }
                });
    }
}