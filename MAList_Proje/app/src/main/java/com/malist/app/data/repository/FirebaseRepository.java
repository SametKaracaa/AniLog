package com.malist.app.data.repository;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.malist.app.data.model.AnimeModel;
import com.malist.app.data.model.UserProfile;
import com.malist.app.data.model.WatchStatus;

import java.util.ArrayList;
import java.util.List;

/**
 * Firebase Realtime Database ile iletisim kuran repository.
 * Kullanicinin anime kutuphanesini yonetir (CRUD islemleri).
 */
public class FirebaseRepository {

    private final FirebaseDatabase database = FirebaseDatabase.getInstance();

    private DatabaseReference getLibraryRef() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userId = (user != null) ? user.getUid() : "anonymous";
        return database.getReference("users").child(userId).child("library");
    }

    private DatabaseReference getProfileRef() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userId = (user != null) ? user.getUid() : "anonymous";
        return database.getReference("users").child(userId).child("profile");
    }

    public interface OnCompleteCallback {
        void onSuccess();
        void onError(Exception e);
    }

    public interface OnCheckCallback {
        void onResult(boolean exists);
        void onError(Exception e);
    }

    /**
     * Animeyi kutuphanesine belirtilen status ile kaydeder.
     */
    public void saveAnime(AnimeModel anime, WatchStatus status, OnCompleteCallback callback) {
        anime.setWatchStatus(status.name());
        getLibraryRef().child(String.valueOf(anime.getMalId())).setValue(anime)
                .addOnSuccessListener(aVoid -> {
                    if (callback != null) callback.onSuccess();
                })
                .addOnFailureListener(e -> {
                    if (callback != null) callback.onError(e);
                });
    }

    /**
     * Anime'nin izleme durumunu gunceller.
     */
    public void updateStatus(int malId, WatchStatus newStatus, OnCompleteCallback callback) {
        getLibraryRef().child(String.valueOf(malId)).child("watchStatus")
                .setValue(newStatus.name())
                .addOnSuccessListener(aVoid -> {
                    if (callback != null) callback.onSuccess();
                })
                .addOnFailureListener(e -> {
                    if (callback != null) callback.onError(e);
                });
    }

    /**
     * Animeyi kutuphaneden siler.
     */
    public void removeAnime(int malId, OnCompleteCallback callback) {
        getLibraryRef().child(String.valueOf(malId)).removeValue()
                .addOnSuccessListener(aVoid -> {
                    if (callback != null) callback.onSuccess();
                })
                .addOnFailureListener(e -> {
                    if (callback != null) callback.onError(e);
                });
    }

    /**
     * Tum kutuphanesini gercek zamanli olarak dinler.
     */
    public LiveData<List<AnimeModel>> getLibrary() {
        MutableLiveData<List<AnimeModel>> liveData = new MutableLiveData<>();
        getLibraryRef().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<AnimeModel> list = new ArrayList<>();
                for (DataSnapshot child : snapshot.getChildren()) {
                    AnimeModel anime = child.getValue(AnimeModel.class);
                    if (anime != null) {
                        list.add(anime);
                    }
                }
                liveData.setValue(list);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                liveData.setValue(new ArrayList<>());
            }
        });
        return liveData;
    }

    /**
     * Belirli bir anime'nin kutuphanede olup olmadigini kontrol eder.
     */
    public void isInLibrary(int malId, OnCheckCallback callback) {
        getLibraryRef().child(String.valueOf(malId)).get()
                .addOnSuccessListener(snapshot -> {
                    if (callback != null) callback.onResult(snapshot.exists());
                })
                .addOnFailureListener(e -> {
                    if (callback != null) callback.onError(e);
                });
    }

    public void saveUserProfile(UserProfile profile, OnCompleteCallback callback) {
        getProfileRef().setValue(profile)
                .addOnSuccessListener(aVoid -> {
                    if (callback != null) callback.onSuccess();
                })
                .addOnFailureListener(e -> {
                    if (callback != null) callback.onError(e);
                });
    }

    public LiveData<UserProfile> getUserProfile() {
        MutableLiveData<UserProfile> liveData = new MutableLiveData<>();
        getProfileRef().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserProfile profile = snapshot.getValue(UserProfile.class);
                if (profile == null) {
                    profile = new UserProfile();
                }
                liveData.setValue(profile);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                liveData.setValue(new UserProfile());
            }
        });
        return liveData;
    }
}
