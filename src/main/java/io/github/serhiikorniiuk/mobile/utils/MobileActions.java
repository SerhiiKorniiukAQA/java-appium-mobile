package io.github.serhiikorniiuk.mobile.utils;

import io.appium.java_client.AppiumDriver;
import io.github.serhiikorniiuk.mobile.config.Config;
import io.github.serhiikorniiuk.mobile.driver.DriverManager;
import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;

import java.util.Map;

/**
 * Platform features through Appium "mobile:" extension commands.
 * They are supported by both UiAutomator2 and XCUITest drivers and do not
 * depend on legacy endpoints, so they keep working across Appium 2/3.
 */
public final class MobileActions {

    private MobileActions() {
    }

    /** App states returned by mobile: queryAppState. */
    public enum AppState {
        NOT_INSTALLED, NOT_RUNNING, RUNNING_IN_BACKGROUND_SUSPENDED, RUNNING_IN_BACKGROUND, RUNNING_IN_FOREGROUND;

        static AppState of(Object raw) {
            return values()[((Number) raw).intValue()];
        }
    }

    private static AppiumDriver driver() {
        return DriverManager.driver();
    }

    private static Map<String, Object> appIdArgs() {
        String key = DriverManager.isIos() ? "bundleId" : "appId";
        return Map.of(key, Config.appId());
    }

    @Step("Put app to background for {seconds} s")
    public static void backgroundApp(int seconds) {
        driver().executeScript("mobile: backgroundApp", Map.of("seconds", seconds));
    }

    @Step("Terminate app")
    public static void terminateApp() {
        driver().executeScript("mobile: terminateApp", appIdArgs());
    }

    @Step("Activate app")
    public static void activateApp() {
        driver().executeScript("mobile: activateApp", appIdArgs());
    }

    public static AppState appState() {
        return AppState.of(driver().executeScript("mobile: queryAppState", appIdArgs()));
    }

    @Step("Open deep link {url}")
    public static void openDeepLink(String url) {
        if (DriverManager.isIos()) {
            driver().executeScript("mobile: deepLink", Map.of("url", url, "bundleId", Config.iosBundleId()));
        } else {
            driver().executeScript("mobile: deepLink", Map.of("url", url, "package", Config.androidAppPackage()));
        }
    }

    public static void hideKeyboard() {
        try {
            if (DriverManager.isIos()) {
                // XCUITest closes the keyboard by tapping one of these keys
                driver().executeScript("mobile: hideKeyboard",
                        Map.of("keys", java.util.List.of("return", "Return", "done", "Done")));
            } else {
                driver().executeScript("mobile: hideKeyboard", Map.of());
            }
        } catch (RuntimeException ignored) {
            // keyboard is not shown or has no such key
        }
        if (DriverManager.isIos() && isIosKeyboardShown()) {
            // Number pad (card number, CVV) has no Return key: tap an empty area above the form
            Dimension size = driver().manage().window().getSize();
            Gestures.tap(driver(), new Point(size.getWidth() / 2, (int) (size.getHeight() * 0.12)));
        }
    }

    private static boolean isIosKeyboardShown() {
        try {
            return !driver().findElements(By.className("XCUIElementTypeKeyboard")).isEmpty();
        } catch (RuntimeException e) {
            return false;
        }
    }

    // ---------- Android only ----------

    @Step("Android: open notification shade")
    public static void openNotifications() {
        requireAndroid();
        driver().executeScript("mobile: openNotifications", Map.of());
    }

    @Step("Android: {action} permissions {permissions}")
    public static void changePermissions(String action, String... permissions) {
        requireAndroid();
        driver().executeScript("mobile: changePermissions", Map.of(
                "permissions", java.util.List.of(permissions),
                "appPackage", Config.androidAppPackage(),
                "action", action));
    }

    @Step("Android: set device location to {latitude}, {longitude}")
    public static void setGeolocation(double latitude, double longitude) {
        requireAndroid();
        driver().executeScript("mobile: setGeolocation", Map.of("latitude", latitude, "longitude", longitude));
    }

    private static void requireAndroid() {
        if (!DriverManager.isAndroid()) {
            throw new UnsupportedOperationException("This action is Android only");
        }
    }
}
