package io.github.serhiikorniiuk.mobile.api.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/** Response of /auth/login and /auth/me (tokens are present only in login response). */
@JsonIgnoreProperties(ignoreUnknown = true)
public record AuthUser(int id, String username, String email, String accessToken, String refreshToken) {
}
