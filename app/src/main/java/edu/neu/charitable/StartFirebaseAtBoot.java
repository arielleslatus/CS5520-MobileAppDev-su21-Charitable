package edu.neu.charitable;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.View;

import edu.neu.charitable.utils.NotificationListener;

public class StartFirebaseAtBoot extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        //Look for stored info
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String email = preferences.getString("email", null);
        String password = preferences.getString("password", null);
        if (email != null && !email.equals("") && password != null && !password.equals("")) {
            FireBaseBackgroundService.startActionBaz(context, email, password);
            NotificationListener.startActionFoo(context, email, password);
        }
        //context.startService(new Intent(context, FireBaseBackgroundService.class));
    }
}