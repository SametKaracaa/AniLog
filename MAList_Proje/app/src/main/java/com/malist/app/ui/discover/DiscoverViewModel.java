package com.malist.app.ui.discover;

import android.os.Handler;
import android.os.Looper;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.malist.app.data.model.AnimeModel;
import com.malist.app.data.repository.JikanRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * ViewModel for the Discover screen.
 * Manages popular anime list from Jikan API, handles pagination, and supports debounced search.
 */
public class DiscoverViewModel extends ViewModel {

    private final JikanRepository repository = new JikanRepository();
    private final Handler debounceHandler = new Handler(Looper.getMainLooper());
    private Runnable pendingSearch;

    private final MutableLiveData<List<AnimeModel>> _animeList = new MutableLiveData<>(new ArrayList<>());
    public LiveData<List<AnimeModel>> getAnimeList() { return _animeList; }

    private final MutableLiveData<Boolean> _isLoading = new MutableLiveData<>(false);
    public LiveData<Boolean> getIsLoading() { return _isLoading; }

    private final MutableLiveData<String> _error = new MutableLiveData<>(null);
    public LiveData<String> getError() { return _error; }

    private int currentPage = 1;
    private boolean hasNextPage = true;
    private boolean isLoadingMore = false;

    private String searchQuery = "";
    private Set<String> selectedGenres = new HashSet<>();
    private String mediaType = "anime";
    private String orderBy = "score";
    private String sortOrder = "desc";

    public Set<String> getCurrentGenres() { return selectedGenres; }
    public String getCurrentMediaType() { return mediaType; }
    public String getCurrentOrderBy() { return orderBy; }
    public String getCurrentSortOrder() { return sortOrder; }

    public DiscoverViewModel() {
        triggerSearch();
    }

    public void setMediaType(String type) {
        if (!mediaType.equals(type)) {
            mediaType = type;
            triggerSearch();
        }
    }

    public void setSearchQuery(String query) {
        searchQuery = query;
        debouncedSearch();
    }

    public void setGenres(Set<String> genreIds) {
        selectedGenres = genreIds;
        triggerSearch();
    }

    public void setSortParams(String orderBy, String sortOrder) {
        this.orderBy = orderBy;
        this.sortOrder = sortOrder;
        triggerSearch();
    }

    public void loadTopAnime() {
        searchQuery = "";
        selectedGenres = new HashSet<>();
        orderBy = "score";
        sortOrder = "desc";
        triggerSearch();
    }

    private void debouncedSearch() {
        if (pendingSearch != null) {
            debounceHandler.removeCallbacks(pendingSearch);
        }
        pendingSearch = this::triggerSearch;
        debounceHandler.postDelayed(pendingSearch, 1000);
    }

    private void triggerSearch() {
        if (Boolean.TRUE.equals(_isLoading.getValue()) && currentPage == 1) return;
        currentPage = 1;
        hasNextPage = true;
        _isLoading.setValue(true);
        _error.setValue(null);

        String genreString = selectedGenres.isEmpty() ? null : String.join(",", selectedGenres);
        boolean isDefault = searchQuery.isEmpty() && genreString == null;

        JikanRepository.ApiCallback<JikanRepository.AnimeListResult> callback = new JikanRepository.ApiCallback<JikanRepository.AnimeListResult>() {
            @Override
            public void onSuccess(JikanRepository.AnimeListResult result) {
                _animeList.setValue(result.animeList);
                hasNextPage = result.hasNextPage;
                currentPage = 2;
                _isLoading.setValue(false);
            }

            @Override
            public void onError(String message) {
                _error.setValue(message);
                _isLoading.setValue(false);
            }
        };

        if ("anime".equals(mediaType)) {
            if (isDefault) {
                repository.getTopAnime(1, callback);
            } else {
                repository.searchAnime(searchQuery, genreString, orderBy, sortOrder, 1, callback);
            }
        } else {
            if (isDefault) {
                repository.getTopManga(1, callback);
            } else {
                repository.searchManga(searchQuery, genreString, orderBy, sortOrder, 1, callback);
            }
        }
    }

    public void loadMore() {
        if (isLoadingMore || !hasNextPage) return;
        isLoadingMore = true;

        String genreString = selectedGenres.isEmpty() ? null : String.join(",", selectedGenres);
        boolean isDefault = searchQuery.isEmpty() && genreString == null;

        JikanRepository.ApiCallback<JikanRepository.AnimeListResult> callback = new JikanRepository.ApiCallback<JikanRepository.AnimeListResult>() {
            @Override
            public void onSuccess(JikanRepository.AnimeListResult result) {
                List<AnimeModel> current = _animeList.getValue();
                List<AnimeModel> updated = new ArrayList<>(current != null ? current : Collections.emptyList());
                updated.addAll(result.animeList);
                _animeList.setValue(updated);
                hasNextPage = result.hasNextPage;
                currentPage++;
                isLoadingMore = false;
            }

            @Override
            public void onError(String message) {
                isLoadingMore = false;
            }
        };

        if ("anime".equals(mediaType)) {
            if (isDefault) {
                repository.getTopAnime(currentPage, callback);
            } else {
                repository.searchAnime(searchQuery, genreString, orderBy, sortOrder, currentPage, callback);
            }
        } else {
            if (isDefault) {
                repository.getTopManga(currentPage, callback);
            } else {
                repository.searchManga(searchQuery, genreString, orderBy, sortOrder, currentPage, callback);
            }
        }
    }

    public boolean canLoadMore() {
        return hasNextPage && !isLoadingMore;
    }
}
