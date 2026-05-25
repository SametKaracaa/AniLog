package com.malist.app.data.model;

public enum WatchStatus {
    WATCHING("Watching"),
    PLANNED("Plan to Watch"),
    COMPLETED("Completed");

    private final String displayName;

    WatchStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static WatchStatus fromString(String value) {
        if (value == null) return null;
        for (WatchStatus status : values()) {
            if (status.name().equals(value)) {
                return status;
            }
        }
        return null;
    }
}
