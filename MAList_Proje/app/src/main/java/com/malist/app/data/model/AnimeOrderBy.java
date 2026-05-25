package com.malist.app.data.model;

public enum AnimeOrderBy {
    POPULARITY("members", "Popularity"),
    SCORE("score", "Score"),
    DATE("start_date", "Start Date");

    private final String value;
    private final String displayName;

    AnimeOrderBy(String value, String displayName) {
        this.value = value;
        this.displayName = displayName;
    }

    public String getValue() { return value; }
    public String getDisplayName() { return displayName; }
}
