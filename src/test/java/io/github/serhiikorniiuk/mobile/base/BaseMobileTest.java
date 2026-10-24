package io.github.serhiikorniiuk.mobile.base;

import io.appium.java_client.android.AndroidStartScreenRecordingOptions;
import io.appium.java_client.screenrecording.CanRecordScreen;
import io.github.serhiikorniiuk.mobile.config.Config;
import io.github.serhiikorniiuk.mobile.driver.DriverManager;
import io.github.serhiikorniiuk.mobile.pages.CatalogPage;
import io.github.serhiikorniiuk.mobile.utils.Evidence;
import io.qameta.allure.Step;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import java.time.Duration;
import java.util.Base64;

/**
 * One Appium session per test: every test starts from a clean app state
 * (noReset=false), so tests are independent and can run in any order.
 * <p>
 * Evidence in the Allure report:
 * <ul>
 *     <li>every step - compressed screenshot ({@code StepScreenshotListener})</li>
 *     <li>failed test - full-size screenshot, page source, screen recording (Android)</li>
 * </ul>
 */
public abstract class BaseMobileTest {

    private boolean recording;

    @BeforeMethod(alwaysRun = true)
    public void startSession() {
        DriverManager.start();
        startScreenRecording();
    }

    @AfterMethod(alwaysRun = true)
    public void finishSession(ITestResult result) {
        if (!DriverManager.isStarted()) {
            return;
        }
        try {
            byte[] video = stopScreenRecording();
            if (!result.isSuccess()) {
                Evidence.attachFullScreenshot("Screenshot on failure");
                Evidence.attachPageSource();
                if (video.length > 0) {
                    Evidence.attachVideo("Screen recording", video);
                }
            }
        } finally {
            DriverManager.quit();
        }
    }

    @Step("Launch app on catalog screen")
    protected CatalogPage catalog() {
        return new CatalogPage().waitOpened();
    }

    protected String deepLink(String path) {
        return Config.deepLinkScheme() + path;
    }

    /**
     * Android emulators record with the built-in screenrecord tool. iOS recording needs
     * ffmpeg on the runner, so iOS relies on per-step screenshots instead.
     */
    private void startScreenRecording() {
        recording = false;
        boolean enabled = Boolean.parseBoolean(Config.optional("allure.video.on.failure").orElse("true"));
        if (!enabled || !DriverManager.isAndroid()) {
            return;
        }
        try {
            ((CanRecordScreen) DriverManager.driver()).startRecordingScreen(
                    AndroidStartScreenRecordingOptions.startScreenRecordingOptions()
                            .withTimeLimit(Duration.ofMinutes(3))
                            .withBitRate(1_000_000));
            recording = true;
        } catch (RuntimeException e) {
            // recording is optional evidence
        }
    }

    private byte[] stopScreenRecording() {
        if (!recording) {
            return new byte[0];
        }
        try {
            String base64 = ((CanRecordScreen) DriverManager.driver()).stopRecordingScreen();
            return base64 == null ? new byte[0] : Base64.getMimeDecoder().decode(base64);
        } catch (RuntimeException e) {
            return new byte[0];
        } finally {
            recording = false;
        }
    }
}
