package io.github.serhiikorniiuk.mobile.api.models;

public record LoginRequest(String username, String password, int expiresInMins) {

    /** Public demo user of DummyJSON. */
    public static LoginRequest validUser() {
        return new LoginRequest("emilys", "emilyspass", 30);
    }

    public LoginRequest withPassword(String newPassword) {
        return new LoginRequest(username, newPassword, expiresInMins);
    }
}
