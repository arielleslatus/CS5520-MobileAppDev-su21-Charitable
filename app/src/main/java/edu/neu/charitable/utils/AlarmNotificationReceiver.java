package edu.neu.charitable.utils;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import edu.neu.charitable.FireBaseBackgroundService;
import edu.neu.charitable.Home;
import edu.neu.charitable.MainActivity;
import edu.neu.charitable.R;

public class AlarmNotificationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent i) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context,0, intent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "reminder")
                .setSmallIcon(R.drawable.ic_vines)
                .setContentTitle("Charitable")
                .setContentText("Got some extra green to go around?")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setChannelId("reminder")
                .setAutoCancel(true);

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
        notificationManagerCompat.notify(1, builder.build());
    }
}
