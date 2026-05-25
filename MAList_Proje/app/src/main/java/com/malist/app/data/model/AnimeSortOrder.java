package com.malist.app.data.model;

public enum AnimeSortOrder {
    DESC("desc", "Descending"),
    ASC("asc", "Ascending");

    private final String value;
    private final String displayName;

    AnimeSortOrder(String value, String displayName) {
        this.value = value;
        this.displayName = displayName;
    }

    public String getValue() { return value; }
    public String getDisplayName() { return displayName; }
}
