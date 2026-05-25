package com.malist.app.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.malist.app.data.prefs.PreferencesManager;

public class ReminderReceiver extends BroadcastReceiver {
    private static final String TAG = "MAList-Notification";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "=== ReminderReceiver.onReceive() TRIGGERED ===");
        try {
            PreferencesManager prefs = PreferencesManager.getInstance(context);
            if (prefs.isNotificationsEnabled()) {
                Log.d(TAG, "Notifications enabled, showing notification...");
                NotificationHelper.showReminderNotification(context);
                NotificationHelper.scheduleDaily(context);
            } else {
                Log.d(TAG, "Notifications disabled in prefs, skipping.");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error in ReminderReceiver!", e);
        }
    }
}
