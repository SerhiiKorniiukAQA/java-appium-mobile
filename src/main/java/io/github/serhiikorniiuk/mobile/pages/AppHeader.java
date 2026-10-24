package io.github.serhiikorniiuk.mobile.pages;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.time.Duration;
import java.util.List;

/** Header / navigation that is shared by all screens: burger menu and cart icon. */
public class AppHeader extends BasePage {

    private static final By MENU = id("open menu");
    private static final By MENU_LOGIN = id("menu item log in");
    private static final By MENU_LOGOUT = id("menu item log out");
    private static final By MENU_CATALOG = id("menu item catalog");
    private static final By CART = id("tab bar option cart");
    private static final By CART_BADGE = id("cart badge");

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

    @Step("Open catalog from menu")
    public CatalogPage openCatalog() {
        openMenu();
        tap(MENU_CATALOG);
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

    /** Number shown on the cart icon, 0 when badge is hidden. */
    public int cartBadgeCount() {
        List<WebElement> badge = driver.findElements(CART_BADGE);
        if (badge.isEmpty()) {
            return 0;
        }
        String text = textOf(badge.get(0)).replaceAll("\\D", "");
        return text.isEmpty() ? 0 : Integer.parseInt(text);
    }
}
