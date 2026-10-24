package io.github.serhiikorniiuk.mobile.pages;

import io.qameta.allure.Step;
import org.openqa.selenium.By;

import java.time.Duration;

public class CartPage extends BasePage {

    private static final By SCREEN = id("cart screen");
    private static final By PRODUCT_ROW = id("product row");
    private static final By REMOVE_ITEM = id("remove item");
    private static final By AMOUNT = id("counter amount");
    private static final By PROCEED = id("Proceed To Checkout button");
    private static final By GO_SHOPPING = id("Go Shopping button");
    private static final Duration EMPTY_STATE_TIMEOUT = Duration.ofSeconds(10);

    public final AppHeader header = new AppHeader();

    @Override
    protected By screenLocator() {
        return SCREEN;
    }

    public int productRowsCount() {
        if (!isVisible(PRODUCT_ROW, Duration.ofSeconds(5))) {
            return 0;
        }
        return driver.findElements(PRODUCT_ROW).size();
    }

    public int firstItemQuantity() {
        return Integer.parseInt(textOf(AMOUNT).replaceAll("\\D", ""));
    }

    public boolean isEmpty() {
        return isVisible(GO_SHOPPING, EMPTY_STATE_TIMEOUT);
    }

    @Step("Remove first item from cart")
    public CartPage removeFirstItem() {
        tap(REMOVE_ITEM);
        return this;
    }

    @Step("Proceed to checkout")
    public void proceedToCheckout() {
        scrollAndTap(PROCEED);
    }
}
