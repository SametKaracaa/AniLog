package com.malist.app.data.prefs;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Kullanici ayarlarini SharedPreferences uzerinde tutan singleton yardimci sinif.
 */
public class PreferencesManager {

    private static final String PREF_NAME = "malist_prefs";
    private static final String KEY_NOTIFICATIONS = "notifications_enabled";
    private static final String KEY_REMINDER_HOUR = "reminder_hour";
    private static final String KEY_REMINDER_MINUTE = "reminder_minute";
    private static final String KEY_CONFIRM_DELETE = "confirm_delete";
    private static final String KEY_DEFAULT_TAB = "default_library_tab";
    private static final String KEY_FIRST_LAUNCH = "first_launch";
    private static final String KEY_RADAR_TUTORIAL = "radar_tutorial";

    private static volatile PreferencesManager INSTANCE;
    private final SharedPreferences prefs;

    private PreferencesManager(Context context) {
        prefs = context.getApplicationContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public static PreferencesManager getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (PreferencesManager.class) {
                if (INSTANCE == null) {
                    INSTANCE = new PreferencesManager(context);
                }
            }
        }
        return INSTANCE;
    }

    public boolean isNotificationsEnabled() {
        return prefs.getBoolean(KEY_NOTIFICATIONS, true);
    }
    public void setNotificationsEnabled(boolean value) {
        prefs.edit().putBoolean(KEY_NOTIFICATIONS, value).apply();
    }

    public int getReminderHour() {
        return prefs.getInt(KEY_REMINDER_HOUR, 20);
    }
    public void setReminderHour(int value) {
        prefs.edit().putInt(KEY_REMINDER_HOUR, value).apply();
    }

    public int getReminderMinute() {
        return prefs.getInt(KEY_REMINDER_MINUTE, 0);
    }
    public void setReminderMinute(int value) {
        prefs.edit().putInt(KEY_REMINDER_MINUTE, value).apply();
    }

    public boolean isConfirmDelete() {
        return prefs.getBoolean(KEY_CONFIRM_DELETE, true);
    }
    public void setConfirmDelete(boolean value) {
        prefs.edit().putBoolean(KEY_CONFIRM_DELETE, value).apply();
    }

    public int getDefaultLibraryTab() {
        return prefs.getInt(KEY_DEFAULT_TAB, 0);
    }
    public void setDefaultLibraryTab(int value) {
        prefs.edit().putInt(KEY_DEFAULT_TAB, value).apply();
    }

    public boolean isFirstLaunch() {
        return prefs.getBoolean(KEY_FIRST_LAUNCH, true);
    }
    public void setFirstLaunch(boolean value) {
        prefs.edit().putBoolean(KEY_FIRST_LAUNCH, value).apply();
    }

    public boolean isShowRadarTutorial() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = (user != null) ? user.getUid() : "default";
        return prefs.getBoolean(KEY_RADAR_TUTORIAL + "_" + uid, true);
    }
    public void setShowRadarTutorial(boolean value) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = (user != null) ? user.getUid() : "default";
        prefs.edit().putBoolean(KEY_RADAR_TUTORIAL + "_" + uid, value).apply();
    }

    public void setReminderTime(int hour, int minute) {
        prefs.edit()
                .putInt(KEY_REMINDER_HOUR, hour)
                .putInt(KEY_REMINDER_MINUTE, minute)
                .apply();
    }
}
