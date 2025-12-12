package com.cbs.model.enums;

public enum ErrorLevel {
    INFO("Info"),
    WARN("Warning"),
    ERROR("Error"),
    FATAL("Fatal");

    private final String displayName;

    ErrorLevel(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}