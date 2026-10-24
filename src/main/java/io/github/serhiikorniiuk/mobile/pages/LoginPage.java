package io.github.serhiikorniiuk.mobile.pages;

import io.github.serhiikorniiuk.mobile.data.User;
import io.github.serhiikorniiuk.mobile.utils.MobileActions;
import io.qameta.allure.Step;
import org.openqa.selenium.By;

import java.time.Duration;

public class LoginPage extends BasePage {

    private static final By SCREEN = id("login screen");
    private static final By USERNAME = id("Username input field");
    private static final By PASSWORD = id("Password input field");
    private static final By LOGIN_BUTTON = id("Login button");
    private static final By USERNAME_ERROR = id("Username-error-message");
    private static final By PASSWORD_ERROR = id("Password-error-message");
    private static final By GENERIC_ERROR = id("generic-error-message");

    @Override
    protected By screenLocator() {
        return SCREEN;
    }

    @Step("Login as {user.username}")
    public void loginAs(User user) {
        submit(user.username(), user.password());
    }

    @Step("Submit login form: username='{username}'")
    public LoginPage submit(String username, String password) {
        type(USERNAME, username);
        type(PASSWORD, password);
        MobileActions.hideKeyboard();
        tap(LOGIN_BUTTON);
        return this;
    }

    public String usernameError() {
        return textOf(USERNAME_ERROR);
    }

    public String passwordError() {
        return textOf(PASSWORD_ERROR);
    }

    public String genericError() {
        return textOf(GENERIC_ERROR);
    }

    /** Any validation message currently shown on the form. */
    public String anyError() {
        for (By locator : new By[]{USERNAME_ERROR, PASSWORD_ERROR, GENERIC_ERROR}) {
            if (isVisible(locator, Duration.ofSeconds(3))) {
                return textOf(locator);
            }
        }
        return "";
    }
}
