package com.malist.app.ui.settings;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.malist.app.data.model.UserProfile;
import com.malist.app.data.prefs.PreferencesManager;
import com.malist.app.data.repository.FirebaseRepository;
import com.malist.app.util.NotificationHelper;

public class SettingsViewModel extends AndroidViewModel {

    private final PreferencesManager prefs;
    private final FirebaseRepository firebaseRepo = new FirebaseRepository();

    private final MutableLiveData<Boolean> _notificationsEnabled;
    public LiveData<Boolean> getNotificationsEnabled() { return _notificationsEnabled; }

    private final MutableLiveData<Boolean> _confirmDelete;
    public LiveData<Boolean> getConfirmDelete() { return _confirmDelete; }

    private final MutableLiveData<Integer> _reminderHour;
    public LiveData<Integer> getReminderHour() { return _reminderHour; }

    private final MutableLiveData<Integer> _reminderMinute;
    public LiveData<Integer> getReminderMinute() { return _reminderMinute; }

    private final LiveData<UserProfile> _userProfile;
    public LiveData<UserProfile> getUserProfile() { return _userProfile; }

    public SettingsViewModel(@NonNull Application application) {
        super(application);
        prefs = PreferencesManager.getInstance(application);

        _notificationsEnabled = new MutableLiveData<>(prefs.isNotificationsEnabled());
        _confirmDelete = new MutableLiveData<>(prefs.isConfirmDelete());
        _reminderHour = new MutableLiveData<>(prefs.getReminderHour());
        _reminderMinute = new MutableLiveData<>(prefs.getReminderMinute());
        _userProfile = firebaseRepo.getUserProfile();
    }

    public void updateProfile(String username, String avatarUrl) {
        UserProfile newProfile = new UserProfile(username, avatarUrl);
        firebaseRepo.saveUserProfile(newProfile, null);
    }

    public void setNotificationsEnabled(boolean enabled) {
        prefs.setNotificationsEnabled(enabled);
        _notificationsEnabled.setValue(enabled);
        if (enabled) {
            NotificationHelper.scheduleDaily(getApplication());
        } else {
            NotificationHelper.cancelDaily(getApplication());
        }
    }

    public void setConfirmDelete(boolean enabled) {
        prefs.setConfirmDelete(enabled);
        _confirmDelete.setValue(enabled);
    }

    public void setReminderTime(int hour, int minute) {
        prefs.setReminderTime(hour, minute);
        _reminderHour.setValue(hour);
        _reminderMinute.setValue(minute);
        NotificationHelper.cancelDaily(getApplication());
        NotificationHelper.scheduleDaily(getApplication());
    }

    public void triggerTestNotification() {
        android.util.Log.d("MAList-Notification", "triggerTestNotification() called from SettingsViewModel");
        NotificationHelper.showReminderNotification(getApplication());
    }
}
