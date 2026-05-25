package com.malist.app;

import android.app.Application;
import android.util.Log;

import com.google.firebase.database.FirebaseDatabase;
import com.malist.app.util.NotificationHelper;

public class MAListApp extends Application {
    private static final String TAG = "MAList-Notification";

    @Override
    public void onCreate() {
        super.onCreate();

        // Enable Firebase Offline Persistence
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

        Log.d(TAG, "MAListApp.onCreate() - creating notification channel");
        NotificationHelper.createChannel(this);

        Log.d(TAG, "MAListApp.onCreate() - scheduling daily reminder");
        NotificationHelper.scheduleDaily(this);
    }
}
