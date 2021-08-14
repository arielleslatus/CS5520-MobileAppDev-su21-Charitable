package edu.neu.charitable.utils;

import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import edu.neu.charitable.FireBaseBackgroundService;
import edu.neu.charitable.Home;
import edu.neu.charitable.R;
import edu.neu.charitable.models.Donation;
import edu.neu.charitable.models.Post;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class NotificationListener extends IntentService {

    private FirebaseDatabase mDb;
    private String me;

    // TODO: Rename actions, choose action names that describe tasks that this
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    private static final String ACTION_FOO = "edu.neu.charitable.utils.action.FOO";
    private static final String ACTION_BAZ = "edu.neu.charitable.utils.action.BAZ";

    // TODO: Rename parameters
    private static final String EXTRA_PARAM1 = "edu.neu.charitable.utils.extra.PARAM1";
    private static final String EXTRA_PARAM2 = "edu.neu.charitable.utils.extra.PARAM2";

    public NotificationListener() {
        super("NotificationListener");
    }

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionFoo(Context context, String param1, String param2) {
        Intent intent = new Intent(context, NotificationListener.class);
        intent.setAction(ACTION_FOO);
        intent.putExtra(EXTRA_PARAM1, param1);
        intent.putExtra(EXTRA_PARAM2, param2);
        context.startService(intent);
    }

    /**
     * Starts this service to perform action Baz with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionBaz(Context context, String param1, String param2) {
        Intent intent = new Intent(context, NotificationListener.class);
        intent.setAction(ACTION_BAZ);
        intent.putExtra(EXTRA_PARAM1, param1);
        intent.putExtra(EXTRA_PARAM2, param2);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_FOO.equals(action)) {
                final String param1 = intent.getStringExtra(EXTRA_PARAM1);
                final String param2 = intent.getStringExtra(EXTRA_PARAM2);
                handleActionFoo(param1, param2);
            } else if (ACTION_BAZ.equals(action)) {
                final String param1 = intent.getStringExtra(EXTRA_PARAM1);
                final String param2 = intent.getStringExtra(EXTRA_PARAM2);
                handleActionBaz(param1, param2);
            }
        }
    }

    /**
     * THis is for listening for notifications of the normal user
     */
    private void handleActionFoo(String param1, String param2) {
        FirebaseApp.initializeApp(NotificationListener.this);
        mDb = FirebaseDatabase.getInstance();
        FirebaseAuth.getInstance().signInWithEmailAndPassword(param1, param2).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    if (user.isEmailVerified()) {

                        mDb.getReference("user_pool").child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    mDb.getReference("pool_received").child(user.getUid()).addChildEventListener(new ChildEventListener() {
                                        @Override
                                        public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                                            if (snapshot.exists()) {
                                                Donation newPost = snapshot.getValue(Donation.class);
                                                if (newPost != null) {
                                                    Intent intent = new Intent(NotificationListener.this, Home.class);
                                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                    PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(),0, intent, 0);

                                                    NotificationCompat.Builder builder = new NotificationCompat.Builder(NotificationListener.this, "reminder")
                                                            .setSmallIcon(R.drawable.ic_vines)
                                                            .setContentTitle("Charitable")
                                                            .setContentText("New Donation of " + newPost.amount)
                                                            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                                                            .setContentIntent(pendingIntent)
                                                            .setChannelId("reminder")
                                                            .setAutoCancel(true);

                                                    NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(NotificationListener.this);
                                                    notificationManagerCompat.notify(3, builder.build());
                                                }
                                            }
                                        }

                                        @Override
                                        public void onChildChanged(@NonNull DataSnapshot snapshot, String previousChildName) {

                                        }

                                        @Override
                                        public void onChildRemoved(@NonNull DataSnapshot snapshot) {

                                        }

                                        @Override
                                        public void onChildMoved(@NonNull DataSnapshot snapshot, String previousChildName) {

                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
                                } else {

                                    mDb.getReference("user_posts").child(user.getUid()).addChildEventListener(new ChildEventListener() {
                                        @Override
                                        public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                                            if (snapshot.exists()) {
                                                Post newPost = snapshot.getValue(Post.class);
                                                if (newPost != null) {
                                                    Intent intent = new Intent(NotificationListener.this, Home.class);
                                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                    PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(),0, intent, 0);



                                                        NotificationCompat.Builder builder = new NotificationCompat.Builder(NotificationListener.this, "reminder")
                                                                .setSmallIcon(R.drawable.ic_vines)
                                                                .setContentTitle("Charitable")
                                                                .setContentText("New " + newPost.type)
                                                                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                                                                .setContentIntent(pendingIntent)
                                                                .setChannelId("reminder")
                                                                .setAutoCancel(true);
                                                    

                                                    NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(NotificationListener.this);
                                                    notificationManagerCompat.notify(3, builder.build());
                                                }
                                            }
                                        }

                                        @Override
                                        public void onChildChanged(@NonNull DataSnapshot snapshot, String previousChildName) {

                                        }

                                        @Override
                                        public void onChildRemoved(@NonNull DataSnapshot snapshot) {

                                        }

                                        @Override
                                        public void onChildMoved(@NonNull DataSnapshot snapshot, String previousChildName) {

                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });

                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                }
            }
        });
    }

    /**
     * Handle action Baz in the provided background thread with the provided
     * parameters.
     */
    private void handleActionBaz(String param1, String param2) {
        // TODO: Handle action Baz
        throw new UnsupportedOperationException("Not yet implemented");
    }
}