package io.github.serhiikorniiuk.mobile.driver;

import io.appium.java_client.AppiumDriver;
import io.github.serhiikorniiuk.mobile.config.Config;
import io.github.serhiikorniiuk.mobile.config.Platform;

/** Thread-safe holder of the current driver (ready for parallel runs on several devices). */
public final class DriverManager {

    private static final ThreadLocal<AppiumDriver> DRIVER = new ThreadLocal<>();

    private DriverManager() {
    }

    public static void start() {
        DRIVER.set(DriverFactory.create(Config.platform()));
    }

    public static AppiumDriver driver() {
        AppiumDriver driver = DRIVER.get();
        if (driver == null) {
            throw new IllegalStateException("Driver is not started. Call DriverManager.start() first");
        }
        return driver;
    }

    public static boolean isStarted() {
        return DRIVER.get() != null;
    }

    public static void quit() {
        AppiumDriver driver = DRIVER.get();
        if (driver != null) {
            try {
                driver.quit();
            } finally {
                DRIVER.remove();
            }
        }
    }

    public static boolean isAndroid() {
        return Config.platform() == Platform.ANDROID;
    }

    public static boolean isIos() {
        return Config.platform() == Platform.IOS;
    }
}
