package com.malist.app.ui.swipe;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.malist.app.data.model.AnimeModel;
import com.malist.app.data.repository.JikanRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SwipeViewModel extends ViewModel {

    private final JikanRepository jikanRepo = new JikanRepository();

    private final MutableLiveData<List<AnimeModel>> _animeList = new MutableLiveData<>();
    public LiveData<List<AnimeModel>> getAnimeList() { return _animeList; }

    private final MutableLiveData<Boolean> _isLoading = new MutableLiveData<>();
    public LiveData<Boolean> getIsLoading() { return _isLoading; }

    private final MutableLiveData<String> _error = new MutableLiveData<>();
    public LiveData<String> getError() { return _error; }

    private int currentPage = 1;
    private boolean hasNextPage = true;

    public SwipeViewModel() {
        fetchInitialData();
    }

    private void fetchInitialData() {
        if (_animeList.getValue() != null) return;

        _isLoading.setValue(true);
        jikanRepo.getAiringAnime(currentPage, new JikanRepository.ApiCallback<JikanRepository.AnimeListResult>() {
            @Override
            public void onSuccess(JikanRepository.AnimeListResult result) {
                _animeList.setValue(result.animeList);
                hasNextPage = result.hasNextPage;
                _isLoading.setValue(false);
            }

            @Override
            public void onError(String message) {
                _error.setValue(message);
                _isLoading.setValue(false);
            }
        });
    }

    public void loadMore() {
        if (Boolean.TRUE.equals(_isLoading.getValue()) || !hasNextPage) return;

        _isLoading.setValue(true);
        currentPage++;
        jikanRepo.getAiringAnime(currentPage, new JikanRepository.ApiCallback<JikanRepository.AnimeListResult>() {
            @Override
            public void onSuccess(JikanRepository.AnimeListResult result) {
                List<AnimeModel> currentList = _animeList.getValue();
                List<AnimeModel> updated = new ArrayList<>(currentList != null ? currentList : Collections.emptyList());
                updated.addAll(result.animeList);
                _animeList.setValue(updated);
                hasNextPage = result.hasNextPage;
                _isLoading.setValue(false);
            }

            @Override
            public void onError(String message) {
                currentPage--;
                _error.setValue(message);
                _isLoading.setValue(false);
            }
        });
    }
}
