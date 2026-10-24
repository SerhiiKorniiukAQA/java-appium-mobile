package io.github.serhiikorniiuk.mobile.pages;

import io.github.serhiikorniiuk.mobile.driver.DriverManager;
import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

/**
 * Navigation shared by all screens. This is where the platforms really differ:
 * <ul>
 *     <li>Android: burger menu ("open menu") in the header and a cart icon with a badge ("cart badge")</li>
 *     <li>iOS: bottom tab bar ("tab bar option menu" / "tab bar option cart"), cart count is the tab label</li>
 * </ul>
 */
public class AppHeader extends BasePage {

    private static final By MENU = platformId("open menu", "tab bar option menu");
    private static final By CART = platformId("cart badge", "tab bar option cart");
    private static final By CATALOG = platformId("menu item catalog", "tab bar option catalog");
    private static final By MENU_LOGIN = id("menu item log in");
    private static final By MENU_LOGOUT = id("menu item log out");
    private static final Duration BADGE_UPDATE_TIMEOUT = Duration.ofSeconds(5);

    @Override
    protected By screenLocator() {
        return MENU;
    }

    @Step("Open menu")
    public AppHeader openMenu() {
        tap(MENU);
        return this;
    }

    @Step("Open login screen from menu")
    public LoginPage openLogin() {
        openMenu();
        tap(MENU_LOGIN);
        return new LoginPage();
    }

    @Step("Open catalog")
    public CatalogPage openCatalog() {
        if (DriverManager.isAndroid()) {
            openMenu();
        }
        tap(CATALOG);
        return new CatalogPage();
    }

    public boolean isLogoutAvailable() {
        openMenu();
        return isVisible(MENU_LOGOUT, Duration.ofSeconds(5));
    }

    @Step("Open cart")
    public CartPage openCart() {
        tap(CART);
        return new CartPage();
    }

    /**
     * Number shown on the cart icon/tab, 0 when there is no badge.
     * The badge is updated asynchronously after "Add To Cart", so a zero value is re-checked for a few seconds.
     */
    public int cartBadgeCount() {
        try {
            return new WebDriverWait(driver, BADGE_UPDATE_TIMEOUT)
                    .ignoring(StaleElementReferenceException.class) // badge is re-rendered on update
                    .until(d -> {
                int count = readBadge();
                return count > 0 ? count : null;
            });
        } catch (org.openqa.selenium.TimeoutException e) {
            return 0;
        }
    }

    private int readBadge() {
        List<WebElement> badge = driver.findElements(CART);
        if (badge.isEmpty()) {
            return 0;
        }
        // iOS: the tab's "value" can be stale, the visible count is its "label"
        String raw = DriverManager.isIos() ? badge.get(0).getAttribute("label") : textOf(badge.get(0));
        String digits = raw == null ? "" : raw.replaceAll("\\D", "");
        return digits.isEmpty() ? 0 : Integer.parseInt(digits);
    }
}
