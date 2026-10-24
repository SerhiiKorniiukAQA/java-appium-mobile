package io.github.serhiikorniiuk.mobile.base;

import io.appium.java_client.AppiumDriver;
import io.github.serhiikorniiuk.mobile.config.Config;
import io.github.serhiikorniiuk.mobile.driver.DriverManager;
import io.github.serhiikorniiuk.mobile.pages.CatalogPage;
import io.qameta.allure.Allure;
import org.openqa.selenium.OutputType;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

/**
 * One Appium session per test: every test starts from a clean app state
 * (noReset=false), so tests are independent and can run in any order.
 */
public abstract class BaseMobileTest {

    @BeforeMethod(alwaysRun = true)
    public void startSession() {
        DriverManager.start();
    }

    @AfterMethod(alwaysRun = true)
    public void finishSession(ITestResult result) {
        if (!DriverManager.isStarted()) {
            return;
        }
        try {
            if (!result.isSuccess()) {
                attachFailureEvidence();
            }
        } finally {
            DriverManager.quit();
        }
    }

    protected CatalogPage catalog() {
        return new CatalogPage().waitOpened();
    }

    protected String deepLink(String path) {
        return Config.deepLinkScheme() + path;
    }

    private void attachFailureEvidence() {
        AppiumDriver driver = DriverManager.driver();
        try {
            byte[] png = driver.getScreenshotAs(OutputType.BYTES);
            Allure.addAttachment("Screenshot on failure", "image/png", new ByteArrayInputStream(png), "png");
        } catch (RuntimeException e) {
            Allure.addAttachment("Screenshot error", e.toString());
        }
        try {
            String source = driver.getPageSource();
            Allure.addAttachment("Page source", "text/xml",
                    new ByteArrayInputStream(source.getBytes(StandardCharsets.UTF_8)), "xml");
        } catch (RuntimeException e) {
            Allure.addAttachment("Page source error", e.toString());
        }
    }
}
