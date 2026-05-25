package com.malist.app.ui.library;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.malist.app.data.model.AnimeModel;
import com.malist.app.data.model.WatchStatus;
import com.malist.app.data.repository.FirebaseRepository;

import java.util.ArrayList;
import java.util.List;

public class LibraryViewModel extends ViewModel {

    private final FirebaseRepository repository = new FirebaseRepository();

    private final LiveData<List<AnimeModel>> allAnime;

    private final MutableLiveData<List<AnimeModel>> _filteredList = new MutableLiveData<>(new ArrayList<>());
    public LiveData<List<AnimeModel>> getFilteredList() { return _filteredList; }

    private WatchStatus currentFilter = WatchStatus.WATCHING;
    private String currentMediaType = "anime";

    public LibraryViewModel() {
        allAnime = repository.getLibrary();
    }

    public void observeLibrary(androidx.lifecycle.LifecycleOwner owner) {
        allAnime.observe(owner, list -> applyFilter(list));
    }

    public void setMediaType(String type) {
        currentMediaType = type;
        applyFilter(allAnime.getValue());
    }

    public void setFilter(WatchStatus status) {
        currentFilter = status;
        applyFilter(allAnime.getValue());
    }

    private void applyFilter(List<AnimeModel> all) {
        if (all == null) {
            _filteredList.setValue(new ArrayList<>());
            return;
        }
        boolean isMangaFilter = "manga".equals(currentMediaType);
        List<AnimeModel> filtered = new ArrayList<>();
        for (AnimeModel anime : all) {
            if (currentFilter.name().equals(anime.getWatchStatus()) && anime.getIsManga() == isMangaFilter) {
                filtered.add(anime);
            }
        }
        _filteredList.setValue(filtered);
    }

    public void removeAnime(int malId) {
        repository.removeAnime(malId, null);
    }
}
