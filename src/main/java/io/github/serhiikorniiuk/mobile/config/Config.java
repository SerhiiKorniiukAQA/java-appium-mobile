package io.github.serhiikorniiuk.mobile.config;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Optional;
import java.util.Properties;

/**
 * Single source of configuration.
 * Resolution order: JVM system property -> environment variable -> config.properties.
 */
public final class Config {

    private static final Properties FILE = load();

    private Config() {
    }

    public static Platform platform() {
        return Platform.from(get("platform"));
    }

    public static String appiumUrl() {
        return get("appium.url");
    }

    public static Duration timeout() {
        return Duration.ofSeconds(Long.parseLong(get("timeout.seconds")));
    }

    public static int retryCount() {
        return Integer.parseInt(get("retry.count"));
    }

    // ---------- Android ----------
    public static String androidDeviceName() {
        return get("android.device.name");
    }

    public static Optional<String> androidUdid() {
        return optional("android.udid");
    }

    public static String androidApp() {
        return absolute(get("android.app"));
    }

    public static String androidAppPackage() {
        return get("android.app.package");
    }

    // ---------- iOS ----------
    public static String iosDeviceName() {
        return get("ios.device.name");
    }

    public static Optional<String> iosPlatformVersion() {
        return optional("ios.platform.version");
    }

    public static Optional<String> iosUdid() {
        return optional("ios.udid");
    }

    public static String iosApp() {
        return absolute(get("ios.app"));
    }

    public static String iosBundleId() {
        return get("ios.bundle.id");
    }

    // ---------- Shared ----------
    public static String deepLinkScheme() {
        return get("app.deeplink.scheme");
    }

    public static String apiBaseUrl() {
        return get("api.base.url");
    }

    /** Application id for the current platform (package on Android, bundle id on iOS). */
    public static String appId() {
        return platform() == Platform.IOS ? iosBundleId() : androidAppPackage();
    }

    public static String get(String key) {
        return optional(key).orElseThrow(() -> new IllegalStateException("Missing config value: " + key));
    }

    public static Optional<String> optional(String key) {
        String value = System.getProperty(key);
        if (isBlank(value)) {
            value = System.getenv(key.toUpperCase().replace('.', '_'));
        }
        if (isBlank(value)) {
            value = FILE.getProperty(key);
        }
        return isBlank(value) ? Optional.empty() : Optional.of(value.trim());
    }

    private static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private static String absolute(String path) {
        return Path.of(path).toAbsolutePath().normalize().toString();
    }

    private static Properties load() {
        Properties properties = new Properties();
        try (InputStream in = Config.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (in != null) {
                properties.load(in);
            }
        } catch (IOException e) {
            throw new UncheckedIOException("Cannot read config.properties", e);
        }
        return properties;
    }
}
