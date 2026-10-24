package io.github.serhiikorniiuk.mobile.pages;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.AppiumDriver;
import io.github.serhiikorniiuk.mobile.config.Config;
import io.github.serhiikorniiuk.mobile.driver.DriverManager;
import io.github.serhiikorniiuk.mobile.utils.Gestures;
import io.github.serhiikorniiuk.mobile.utils.MobileActions;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

/**
 * Common page behaviour.
 * <p>
 * My Demo App (React Native) exposes the same accessibility ids on both platforms
 * (content-desc on Android, accessibility identifier on iOS), so one Page Object
 * serves Android and iOS. Platform differences are isolated in this class.
 */
public abstract class BasePage {

    private static final int MAX_SWIPES = 6;

    protected final AppiumDriver driver;
    protected final WebDriverWait wait;

    protected BasePage() {
        this.driver = DriverManager.driver();
        this.wait = new WebDriverWait(driver, Config.timeout());
    }

    /** Locator that identifies the screen. */
    protected abstract By screenLocator();

    public boolean isOpened() {
        return isVisible(screenLocator(), Config.timeout());
    }

    protected static By id(String accessibilityId) {
        return AppiumBy.accessibilityId(accessibilityId);
    }

    /** Same element exposed under different accessibility ids on Android and iOS. */
    protected static By platformId(String android, String ios) {
        return id(DriverManager.isIos() ? ios : android);
    }

    /** All matching elements that are currently on screen (off-screen list items are skipped). */
    protected List<WebElement> visibleElements(By locator) {
        return wait.until(d -> {
            List<WebElement> visible = d.findElements(locator).stream().filter(BasePage::isDisplayedSafe).toList();
            return visible.isEmpty() ? null : visible;
        });
    }

    protected WebElement waitVisible(By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    protected boolean isVisible(By locator, Duration timeout) {
        try {
            new WebDriverWait(driver, timeout).until(ExpectedConditions.visibilityOfElementLocated(locator));
            return true;
        } catch (TimeoutException e) {
            return false;
        }
    }

    protected void tap(By locator) {
        waitVisible(locator).click();
    }

    protected void type(By locator, String text) {
        WebElement element = waitVisible(locator);
        element.clear();
        element.sendKeys(text);
    }

    /** Scrolls down until the element is visible, then taps it. Useful for long forms. */
    protected void scrollAndTap(By locator) {
        MobileActions.hideKeyboard();
        scrollTo(locator).click();
    }

    protected WebElement scrollTo(By locator) {
        for (int i = 0; i <= MAX_SWIPES; i++) {
            List<WebElement> found = driver.findElements(locator);
            if (!found.isEmpty() && isDisplayedSafe(found.get(0))) {
                return found.get(0);
            }
            Gestures.swipe(driver, Gestures.Direction.UP);
        }
        throw new NoSuchElementException("Element not found after " + MAX_SWIPES + " swipes: " + locator);
    }

    /**
     * Reads visible text. In RN apps an element with an accessibility id on Android is
     * often a ViewGroup whose text lives in child TextViews, while on iOS the label is
     * exposed on the element itself.
     */
    protected String textOf(By locator) {
        return textOf(waitVisible(locator));
    }

    protected String textOf(WebElement element) {
        String text = element.getText();
        if ((text == null || text.isBlank()) && DriverManager.isAndroid()) {
            List<WebElement> children = element.findElements(By.className("android.widget.TextView"));
            text = String.join(" ", children.stream().map(WebElement::getText).toList());
        }
        return text == null ? "" : text.trim();
    }

    private static boolean isDisplayedSafe(WebElement element) {
        try {
            return element.isDisplayed();
        } catch (StaleElementReferenceException e) {
            return false;
        }
    }
}
