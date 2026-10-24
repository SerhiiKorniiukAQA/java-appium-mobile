package io.github.serhiikorniiuk.mobile.api;

import io.github.serhiikorniiuk.mobile.api.models.AuthUser;
import io.github.serhiikorniiuk.mobile.api.models.LoginRequest;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

@Epic("API")
@Feature("Authentication")
public class AuthApiTest {

    @Test(groups = {"smoke", "api"}, description = "TC-API-007: Login returns access and refresh tokens")
    @Severity(SeverityLevel.BLOCKER)
    public void loginReturnsTokens() {
        AuthUser user = StoreApi.login(LoginRequest.validUser())
                .then().statusCode(200)
                .extract().as(AuthUser.class);

        assertThat(user.username()).isEqualTo("emilys");
        assertThat(user.accessToken()).as("JWT access token").isNotBlank().contains(".");
        assertThat(user.refreshToken()).isNotBlank();
    }

    @Test(groups = "api", description = "TC-API-008: Token gives access to current user profile (AuthN -> AuthZ)")
    @Severity(SeverityLevel.CRITICAL)
    public void tokenAuthorizesProfileRequest() {
        AuthUser login = StoreApi.login(LoginRequest.validUser()).as(AuthUser.class);

        AuthUser me = StoreApi.me(login.accessToken())
                .then().statusCode(200)
                .extract().as(AuthUser.class);

        assertThat(me.id()).isEqualTo(login.id());
        assertThat(me.email()).isEqualTo(login.email());
    }

    @Test(groups = "api", description = "TC-API-009: Wrong password is rejected with 400")
    @Severity(SeverityLevel.CRITICAL)
    public void wrongPasswordIsRejected() {
        Response response = StoreApi.login(LoginRequest.validUser().withPassword("wrong"));

        assertThat(response.statusCode()).isEqualTo(400);
        assertThat(response.jsonPath().getString("message")).containsIgnoringCase("invalid");
    }

    @Test(groups = "api", description = "TC-API-010: Protected endpoint without token is rejected")
    @Severity(SeverityLevel.CRITICAL)
    public void profileWithoutTokenIsRejected() {
        assertThat(StoreApi.meAnonymous().statusCode()).isIn(401, 403);
    }
}
