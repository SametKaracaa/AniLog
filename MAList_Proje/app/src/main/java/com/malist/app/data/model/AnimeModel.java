package com.malist.app.data.model;

/**
 * Uygulama genelinde kullanilan anime modeli.
 * Hem Jikan API verilerini hem de Firebase'deki kullanici verilerini temsil eder.
 * Firebase deserialization icin no-arg constructor ve default degerler zorunlu.
 */
public class AnimeModel {
    private int malId;
    private String title;
    private String titleEnglish;
    private String imageUrl;
    private String largeImageUrl;
    private double score;
    private int episodes;
    private int chapters;
    private String synopsis;
    private String status;
    private String type;
    private String genres;
    private String watchStatus;
    private boolean isManga;
    private float userRating;
    private String userReview;
    private int watchedEpisodes;

    public AnimeModel() {
        this.malId = 0;
        this.title = "";
        this.titleEnglish = null;
        this.imageUrl = "";
        this.largeImageUrl = "";
        this.score = 0.0;
        this.episodes = 0;
        this.chapters = 0;
        this.synopsis = "";
        this.status = "";
        this.type = "";
        this.genres = "";
        this.watchStatus = "";
        this.isManga = false;
        this.userRating = 0f;
        this.userReview = "";
        this.watchedEpisodes = 0;
    }

    public AnimeModel(int malId, String title, String titleEnglish, String imageUrl,
                      String largeImageUrl, double score, int episodes, int chapters,
                      String synopsis, String status, String type, String genres,
                      String watchStatus, boolean isManga, float userRating,
                      String userReview, int watchedEpisodes) {
        this.malId = malId;
        this.title = title;
        this.titleEnglish = titleEnglish;
        this.imageUrl = imageUrl;
        this.largeImageUrl = largeImageUrl;
        this.score = score;
        this.episodes = episodes;
        this.chapters = chapters;
        this.synopsis = synopsis;
        this.status = status;
        this.type = type;
        this.genres = genres;
        this.watchStatus = watchStatus;
        this.isManga = isManga;
        this.userRating = userRating;
        this.userReview = userReview;
        this.watchedEpisodes = watchedEpisodes;
    }

    // Getters and Setters
    public int getMalId() { return malId; }
    public void setMalId(int malId) { this.malId = malId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getTitleEnglish() { return titleEnglish; }
    public void setTitleEnglish(String titleEnglish) { this.titleEnglish = titleEnglish; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getLargeImageUrl() { return largeImageUrl; }
    public void setLargeImageUrl(String largeImageUrl) { this.largeImageUrl = largeImageUrl; }

    public double getScore() { return score; }
    public void setScore(double score) { this.score = score; }

    public int getEpisodes() { return episodes; }
    public void setEpisodes(int episodes) { this.episodes = episodes; }

    public int getChapters() { return chapters; }
    public void setChapters(int chapters) { this.chapters = chapters; }

    public String getSynopsis() { return synopsis; }
    public void setSynopsis(String synopsis) { this.synopsis = synopsis; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getGenres() { return genres; }
    public void setGenres(String genres) { this.genres = genres; }

    public String getWatchStatus() { return watchStatus; }
    public void setWatchStatus(String watchStatus) { this.watchStatus = watchStatus; }

    public boolean getIsManga() { return isManga; }
    public void setIsManga(boolean isManga) { this.isManga = isManga; }

    public float getUserRating() { return userRating; }
    public void setUserRating(float userRating) { this.userRating = userRating; }

    public String getUserReview() { return userReview; }
    public void setUserReview(String userReview) { this.userReview = userReview; }

    public int getWatchedEpisodes() { return watchedEpisodes; }
    public void setWatchedEpisodes(int watchedEpisodes) { this.watchedEpisodes = watchedEpisodes; }

    /** Creates a copy with modified fields (similar to Kotlin data class copy) */
    public AnimeModel copy() {
        return new AnimeModel(malId, title, titleEnglish, imageUrl, largeImageUrl,
                score, episodes, chapters, synopsis, status, type, genres,
                watchStatus, isManga, userRating, userReview, watchedEpisodes);
    }

    public AnimeModel copyWithWatchStatus(String watchStatus) {
        AnimeModel copy = copy();
        copy.setWatchStatus(watchStatus);
        return copy;
    }

    public AnimeModel copyWithRatingAndReview(float userRating, String userReview) {
        AnimeModel copy = copy();
        copy.setUserRating(userRating);
        copy.setUserReview(userReview);
        return copy;
    }

    public AnimeModel copyWithWatchedEpisodes(int watchedEpisodes) {
        AnimeModel copy = copy();
        copy.setWatchedEpisodes(watchedEpisodes);
        return copy;
    }
}
