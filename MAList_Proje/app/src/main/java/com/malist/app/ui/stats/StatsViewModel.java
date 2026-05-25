package com.malist.app.ui.stats;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.malist.app.data.model.AnimeModel;
import com.malist.app.data.model.WatchStatus;
import com.malist.app.data.repository.FirebaseRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StatsViewModel extends ViewModel {

    private final FirebaseRepository firebaseRepo = new FirebaseRepository();

    private final MutableLiveData<UserStats> _stats = new MutableLiveData<>();
    public LiveData<UserStats> getStats() { return _stats; }

    private final MutableLiveData<Boolean> _isLoading = new MutableLiveData<>();
    public LiveData<Boolean> getIsLoading() { return _isLoading; }

    private final LiveData<List<AnimeModel>> libraryData;

    public StatsViewModel() {
        _isLoading.setValue(true);
        libraryData = firebaseRepo.getLibrary();
    }

    public void observeLibrary(androidx.lifecycle.LifecycleOwner owner) {
        libraryData.observe(owner, list -> {
            calculateStats(list);
            _isLoading.setValue(false);
        });
    }

    private void calculateStats(List<AnimeModel> list) {
        if (list == null || list.isEmpty()) {
            _stats.setValue(new UserStats(0, 0, 0, 0, 0.0, new ArrayList<>()));
            return;
        }

        int totalAnime = list.size();
        int totalEpisodes = 0;
        int totalMinutes = 0;

        Map<String, Integer> genreCount = new HashMap<>();
        List<Double> userScores = new ArrayList<>();

        for (AnimeModel anime : list) {
            WatchStatus status = WatchStatus.fromString(anime.getWatchStatus());

            if (status == WatchStatus.COMPLETED && anime.getUserRating() > 0f) {
                userScores.add((double) anime.getUserRating());
            }

            if (status != WatchStatus.PLANNED) {
                if (anime.getIsManga()) {
                    totalEpisodes += anime.getChapters();
                    totalMinutes += anime.getChapters() * 10;
                } else {
                    int eps;
                    if (status == WatchStatus.WATCHING && anime.getWatchedEpisodes() > 0) {
                        eps = anime.getWatchedEpisodes();
                    } else {
                        eps = anime.getEpisodes();
                    }
                    totalEpisodes += eps;
                    totalMinutes += eps * 24;
                }
            }

            if (anime.getGenres() != null && !anime.getGenres().isEmpty()) {
                String[] split = anime.getGenres().split(", ");
                for (String g : split) {
                    String genre = g.trim();
                    if (!genre.isEmpty()) {
                        genreCount.put(genre, genreCount.getOrDefault(genre, 0) + 1);
                    }
                }
            }
        }

        int daysWasted = totalMinutes / 1440;
        int hoursWasted = (totalMinutes % 1440) / 60;

        double meanScore = 0.0;
        if (!userScores.isEmpty()) {
            double sum = 0;
            for (double s : userScores) sum += s;
            meanScore = sum / userScores.size();
        }

        List<Map.Entry<String, Integer>> sortedGenres = new ArrayList<>(genreCount.entrySet());
        sortedGenres.sort((a, b) -> b.getValue() - a.getValue());

        List<UserStats.GenreCount> topGenres = new ArrayList<>();
        for (int i = 0; i < Math.min(3, sortedGenres.size()); i++) {
            Map.Entry<String, Integer> entry = sortedGenres.get(i);
            topGenres.add(new UserStats.GenreCount(entry.getKey(), entry.getValue()));
        }

        _stats.setValue(new UserStats(totalAnime, totalEpisodes, daysWasted, hoursWasted, meanScore, topGenres));
    }
}
