package io.github.serhiikorniiuk.mobile.config;

public enum Platform {
    ANDROID,
    IOS,
    API;

    public static Platform from(String value) {
        try {
            return Platform.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Unknown platform '" + value + "'. Use: android, ios or api");
        }
    }
}
