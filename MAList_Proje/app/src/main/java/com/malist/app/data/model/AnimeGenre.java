package com.malist.app.data.model;

public enum AnimeGenre {
    ACTION("1", "Action"),
    ADVENTURE("2", "Adventure"),
    COMEDY("4", "Comedy"),
    DRAMA("8", "Drama"),
    FANTASY("10", "Fantasy"),
    SCIFI("24", "Sci-Fi"),
    AWARD_WINNING("46", "Award Winning"),
    SLICE_OF_LIFE("36", "Slice of Life"),
    MYSTERY("7", "Mystery"),
    HORROR("14", "Horror"),
    SPORTS("30", "Sports"),
    SUPERNATURAL("37", "Supernatural");

    private final String id;
    private final String displayName;

    AnimeGenre(String id, String displayName) {
        this.id = id;
        this.displayName = displayName;
    }

    public String getId() { return id; }
    public String getDisplayName() { return displayName; }
}
