package com.malist.app.ui.auth;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;

public class AuthViewModel extends ViewModel {

    public static final int STATE_IDLE = 0;
    public static final int STATE_LOADING = 1;
    public static final int STATE_SUCCESS = 2;
    public static final int STATE_ERROR = 3;

    private final FirebaseAuth auth = FirebaseAuth.getInstance();

    private final MutableLiveData<Integer> _authState = new MutableLiveData<>(STATE_IDLE);
    public LiveData<Integer> getAuthState() { return _authState; }

    private final MutableLiveData<String> _errorMessage = new MutableLiveData<>("");
    public LiveData<String> getErrorMessage() { return _errorMessage; }

    public boolean isUserAuthenticated() {
        return auth.getCurrentUser() != null;
    }

    public void login(String email, String pass) {
        _authState.setValue(STATE_LOADING);
        auth.signInWithEmailAndPassword(email, pass)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        _authState.setValue(STATE_SUCCESS);
                    } else {
                        _errorMessage.setValue(task.getException() != null ? task.getException().getMessage() : "Login failed");
                        _authState.setValue(STATE_ERROR);
                    }
                });
    }

    public void register(String email, String pass) {
        _authState.setValue(STATE_LOADING);
        auth.createUserWithEmailAndPassword(email, pass)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        _authState.setValue(STATE_SUCCESS);
                    } else {
                        _errorMessage.setValue(task.getException() != null ? task.getException().getMessage() : "Registration failed");
                        _authState.setValue(STATE_ERROR);
                    }
                });
    }

    public void logout() {
        auth.signOut();
    }
}
