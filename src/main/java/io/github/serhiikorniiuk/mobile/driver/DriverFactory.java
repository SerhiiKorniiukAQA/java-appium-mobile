package io.github.serhiikorniiuk.mobile.driver;

import io.appium.java_client.AppiumClientConfig;
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
            case ANDROID -> new AndroidDriver(clientConfig(), androidOptions());
            case IOS -> new IOSDriver(clientConfig(), iosOptions());
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
        // WebDriverAgent build on a CI macOS runner can take 10+ minutes (pre-built in the workflow)
        options.setCapability("appium:wdaLaunchTimeout", 900_000);
        options.setCapability("appium:wdaConnectionTimeout", 900_000);
        options.setCapability("appium:simulatorStartupTimeout", 300_000);
        options.setCapability("appium:useNewWDA", false);
        // Simulator on a busy CI runner drops characters when typing fast (default 60)
        options.setCapability("appium:maxTypingFrequency", 30);
        // WebDriverAgent pre-built by the CI workflow (xcodebuild build-for-testing)
        Config.optional("ios.wda.derived.data").ifPresent(path -> {
            options.setCapability("appium:derivedDataPath", path);
            options.setCapability("appium:usePrebuiltWDA", true);
        });
        return options;
    }

    /**
     * Creating the first iOS session may include a WebDriverAgent build that takes longer than
     * the client's default 10 minute HTTP read timeout.
     */
    private static AppiumClientConfig clientConfig() {
        return AppiumClientConfig.defaultConfig()
                .baseUrl(serverUrl())
                .readTimeout(Duration.ofMinutes(20));
    }

    private static URL serverUrl() {
        try {
            return URI.create(Config.appiumUrl()).toURL();
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("Invalid Appium URL: " + Config.appiumUrl(), e);
        }
    }
}
