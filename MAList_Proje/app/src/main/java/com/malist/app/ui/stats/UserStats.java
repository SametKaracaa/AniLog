package com.malist.app.ui.stats;

import java.util.List;

public class UserStats {
    private final int totalAnime;
    private final int totalEpisodes;
    private final int daysWasted;
    private final int hoursWasted;
    private final double meanScore;
    private final List<GenreCount> topGenres;

    public static class GenreCount {
        public final String genre;
        public final int count;
        public GenreCount(String genre, int count) {
            this.genre = genre;
            this.count = count;
        }
    }

    public UserStats(int totalAnime, int totalEpisodes, int daysWasted, int hoursWasted,
                     double meanScore, List<GenreCount> topGenres) {
        this.totalAnime = totalAnime;
        this.totalEpisodes = totalEpisodes;
        this.daysWasted = daysWasted;
        this.hoursWasted = hoursWasted;
        this.meanScore = meanScore;
        this.topGenres = topGenres;
    }

    public int getTotalAnime() { return totalAnime; }
    public int getTotalEpisodes() { return totalEpisodes; }
    public int getDaysWasted() { return daysWasted; }
    public int getHoursWasted() { return hoursWasted; }
    public double getMeanScore() { return meanScore; }
    public List<GenreCount> getTopGenres() { return topGenres; }
}
