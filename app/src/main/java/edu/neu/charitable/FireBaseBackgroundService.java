package edu.neu.charitable;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.HashMap;
import java.util.TimeZone;

import edu.neu.charitable.models.Donation;
import edu.neu.charitable.models.Post;
import edu.neu.charitable.utils.AlarmNotificationReceiver;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class FireBaseBackgroundService extends IntentService {

    private FirebaseDatabase mDb;
    private String me;

    // TODO: Rename actions, choose action names that describe tasks that this
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    private static final String ACTION_FOO = "edu.neu.charitable.action.FOO";
    private static final String ACTION_BAZ = "edu.neu.charitable.action.BAZ";

    // TODO: Rename parameters
    private static final String EXTRA_PARAM1 = "edu.neu.charitable.extra.PARAM1";
    private static final String EXTRA_PARAM2 = "edu.neu.charitable.extra.PARAM2";

    public FireBaseBackgroundService() {
        super("FireBaseBackgroundService");
    }

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionFoo(Context context, String param1, String param2) {
        Intent intent = new Intent(context, FireBaseBackgroundService.class);
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
        Intent intent = new Intent(context, FireBaseBackgroundService.class);
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
     * Handle incoming notifications
     */
    private void handleActionFoo(String param1, String param2) {
        FirebaseApp.initializeApp(FireBaseBackgroundService.this);
        mDb = FirebaseDatabase.getInstance();
        FirebaseAuth.getInstance().signInWithEmailAndPassword(param1, param2).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    if (user.isEmailVerified()) {

                        Intent intent = new Intent(FireBaseBackgroundService.this, Home.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(),0, intent, 0);

                        NotificationCompat.Builder builder = new NotificationCompat.Builder(FireBaseBackgroundService.this, "reminder")
                                .setSmallIcon(R.drawable.ic_vines)
                                .setContentTitle("Charitable Says")
                                .setContentText("Logged in as normal user")
                                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                                .setContentIntent(pendingIntent)
                                .setChannelId("reminder")
                                .setAutoCancel(true);

                        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(FireBaseBackgroundService.this);
                        notificationManagerCompat.notify(1, builder.build());
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
        FirebaseApp.initializeApp(FireBaseBackgroundService.this);
        mDb = FirebaseDatabase.getInstance();
        FirebaseAuth.getInstance().signInWithEmailAndPassword(param1, param2).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    if (user.isEmailVerified()) {
                        mDb.getReference("user_donations").child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    HashMap<Integer, Integer> hours = new HashMap<>();
                                    for (DataSnapshot ds : snapshot.getChildren()) {
                                        if (ds.exists()) {
                                            Donation don = ds.getValue(Donation.class);
                                            LocalDateTime dt = LocalDateTime.ofInstant(Instant.ofEpochMilli(don.timestamp), TimeZone.getDefault().toZoneId());
                                            int hr = dt.getHour();
                                            int count = hours.containsKey(hr) ? hours.get(hr) : 0;
                                            hours.put(hr, count + 1);
                                        }
                                    }

                                    int max = 0;
                                    int hr_selected = 9;
                                    for (Integer hr : hours.keySet()) {
                                        if (hours.get(hr) > max) {
                                            max = hours.get(hr);
                                            hr_selected = hr;
                                        }
                                    }

                                    startAlarmBroadcastReceiver(FireBaseBackgroundService.this, hr_selected);
                                    //notifyHourSet(hr_selected);

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

    public static void startAlarmBroadcastReceiver(Context context, int hr) {
        Intent _intent = new Intent(context, AlarmNotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, _intent, 0);
        AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        //alarmManager.cancel(pendingIntent);
        Calendar calendar = Calendar.getInstance();
        //calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, hr);
        calendar.set(Calendar.MINUTE, 15);
        calendar.set(Calendar.SECOND, 0);
        //alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
        Log.d("alarm set", LocalDateTime.ofInstant(Instant.ofEpochMilli(calendar.getTimeInMillis()),TimeZone.getDefault().toZoneId()).toString());
    }

    private void notifyHourSet(int hr) {
        Intent intent = new Intent(FireBaseBackgroundService.this, Home.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(),0, intent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(FireBaseBackgroundService.this, "reminder")
                .setSmallIcon(R.drawable.ic_vines)
                .setContentTitle("Hour Selected")
                .setContentText(Integer.toString(hr))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setChannelId("reminder")
                .setAutoCancel(true);

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(FireBaseBackgroundService.this);
        notificationManagerCompat.notify(2, builder.build());
    }
}