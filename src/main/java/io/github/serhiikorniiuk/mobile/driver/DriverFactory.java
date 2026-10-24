package io.github.serhiikorniiuk.mobile.driver;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.ios.options.XCUITestOptions;
import io.github.serhiikorniiuk.mobile.config.Config;
import io.github.serhiikorniiuk.mobile.config.Platform;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.time.Duration;

/** Builds a driver with platform specific capabilities. */
public final class DriverFactory {

    private DriverFactory() {
    }

    public static AppiumDriver create(Platform platform) {
        return switch (platform) {
            case ANDROID -> new AndroidDriver(serverUrl(), androidOptions());
            case IOS -> new IOSDriver(serverUrl(), iosOptions());
            case API -> throw new IllegalStateException("API tests do not need a mobile driver");
        };
    }

    static UiAutomator2Options androidOptions() {
        UiAutomator2Options options = new UiAutomator2Options()
                .setDeviceName(Config.androidDeviceName())
                .setApp(Config.androidApp())
                .setAutoGrantPermissions(true)
                .setNewCommandTimeout(Duration.ofSeconds(120));
        Config.androidUdid().ifPresent(options::setUdid);
        // Clean app state for every session -> independent tests
        options.setCapability("appium:noReset", false);
        options.setCapability("appium:fullReset", false);
        options.setCapability("appium:disableWindowAnimation", true);
        options.setCapability("appium:uiautomator2ServerInstallTimeout", 120_000);
        options.setCapability("appium:adbExecTimeout", 60_000);
        options.setCapability("appium:appWaitDuration", 30_000);
        return options;
    }

    static XCUITestOptions iosOptions() {
        XCUITestOptions options = new XCUITestOptions()
                .setDeviceName(Config.iosDeviceName())
                .setApp(Config.iosApp())
                .setNewCommandTimeout(Duration.ofSeconds(120));
        Config.iosPlatformVersion().ifPresent(options::setPlatformVersion);
        Config.iosUdid().ifPresent(options::setUdid);
        options.setCapability("appium:noReset", false);
        // First WebDriverAgent build on CI takes several minutes
        options.setCapability("appium:wdaLaunchTimeout", 300_000);
        options.setCapability("appium:wdaConnectionTimeout", 300_000);
        options.setCapability("appium:simulatorStartupTimeout", 300_000);
        options.setCapability("appium:useNewWDA", false);
        return options;
    }

    private static URL serverUrl() {
        try {
            return URI.create(Config.appiumUrl()).toURL();
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("Invalid Appium URL: " + Config.appiumUrl(), e);
        }
    }
}
