package com.malist.app.util;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.malist.app.MainActivity;
import com.malist.app.R;
import com.malist.app.data.prefs.PreferencesManager;

import java.util.Calendar;

/**
 * Gunluk hatirlatma bildirimleri icin yardimci sinif.
 */
public final class NotificationHelper {

    private static final String TAG = "MAList-Notification";
    public static final String CHANNEL_ID = "malist_reminder_channel_high";
    public static final int NOTIFICATION_ID = 1001;
    public static final int REQUEST_CODE = 2001;

    private NotificationHelper() {}

    public static void createChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            try {
                String name = context.getString(R.string.notification_channel_name);
                String description = context.getString(R.string.notification_channel_desc);
                NotificationChannel channel = new NotificationChannel(
                        CHANNEL_ID, name, NotificationManager.IMPORTANCE_HIGH);
                channel.setDescription(description);
                channel.enableVibration(true);
                channel.enableLights(true);
                NotificationManager manager = (NotificationManager)
                        context.getSystemService(Context.NOTIFICATION_SERVICE);
                if (manager != null) {
                    manager.createNotificationChannel(channel);
                    Log.d(TAG, "Notification channel created: " + CHANNEL_ID);
                } else {
                    Log.e(TAG, "NotificationManager is null, cannot create channel!");
                }
            } catch (Exception e) {
                Log.e(TAG, "Error creating notification channel", e);
            }
        }
    }

    public static void showReminderNotification(Context context) {
        try {
            Log.d(TAG, "showReminderNotification() called");

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (ContextCompat.checkSelfPermission(context,
                        android.Manifest.permission.POST_NOTIFICATIONS)
                        != android.content.pm.PackageManager.PERMISSION_GRANTED) {
                    Log.w(TAG, "POST_NOTIFICATIONS permission NOT granted! Cannot show notification.");
                    return;
                }
            }

            createChannel(context);

            Intent intent = new Intent(context, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            PendingIntent pendingIntent = PendingIntent.getActivity(
                    context, 0, intent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

            android.app.Notification notification = new NotificationCompat.Builder(context, CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_notification)
                    .setContentTitle(context.getString(R.string.notification_title))
                    .setContentText(context.getString(R.string.notification_text))
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setDefaults(NotificationCompat.DEFAULT_ALL)
                    .setCategory(NotificationCompat.CATEGORY_REMINDER)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)
                    .build();

            NotificationManagerCompat managerCompat = NotificationManagerCompat.from(context);
            managerCompat.notify(NOTIFICATION_ID, notification);
            Log.d(TAG, "Notification posted successfully with ID: " + NOTIFICATION_ID);

        } catch (Exception e) {
            Log.e(TAG, "FAILED to show notification!", e);
        }
    }

    public static void scheduleDaily(Context context) {
        try {
            Log.d(TAG, "scheduleDaily() called");
            PreferencesManager prefs = PreferencesManager.getInstance(context);
            if (!prefs.isNotificationsEnabled()) {
                Log.d(TAG, "Notifications are disabled in prefs, not scheduling.");
                return;
            }

            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            if (alarmManager == null) {
                Log.e(TAG, "AlarmManager is null!");
                return;
            }

            Intent intent = new Intent(context, ReminderReceiver.class);
            PendingIntent pending = PendingIntent.getBroadcast(
                    context, REQUEST_CODE, intent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            calendar.set(Calendar.HOUR_OF_DAY, prefs.getReminderHour());
            calendar.set(Calendar.MINUTE, prefs.getReminderMinute());
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);

            if (calendar.getTimeInMillis() <= System.currentTimeMillis()) {
                calendar.add(Calendar.DAY_OF_YEAR, 1);
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (!alarmManager.canScheduleExactAlarms()) {
                    Log.w(TAG, "Cannot schedule exact alarms, using inexact. (Android 12+)");
                    alarmManager.setAndAllowWhileIdle(
                            AlarmManager.RTC_WAKEUP,
                            calendar.getTimeInMillis(),
                            pending);
                    Log.d(TAG, "Inexact alarm set for: " + calendar.getTime());
                    return;
                }
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        calendar.getTimeInMillis(),
                        pending);
            } else {
                alarmManager.setExact(
                        AlarmManager.RTC_WAKEUP,
                        calendar.getTimeInMillis(),
                        pending);
            }

            Log.d(TAG, "Daily reminder scheduled for: " + calendar.getTime());

        } catch (Exception e) {
            Log.e(TAG, "FAILED to schedule daily reminder!", e);
        }
    }

    public static void cancelDaily(Context context) {
        try {
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            if (alarmManager == null) return;
            Intent intent = new Intent(context, ReminderReceiver.class);
            PendingIntent pending = PendingIntent.getBroadcast(
                    context, REQUEST_CODE, intent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
            alarmManager.cancel(pending);
            Log.d(TAG, "Daily reminder cancelled.");
        } catch (Exception e) {
            Log.e(TAG, "FAILED to cancel daily reminder!", e);
        }
    }
}
