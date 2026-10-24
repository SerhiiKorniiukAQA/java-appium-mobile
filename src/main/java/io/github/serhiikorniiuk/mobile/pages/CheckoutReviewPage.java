package io.github.serhiikorniiuk.mobile.pages;

import io.qameta.allure.Step;
import org.openqa.selenium.By;

public class CheckoutReviewPage extends BasePage {

    private static final By SCREEN = id("checkout review order screen");
    private static final By PLACE_ORDER = id("Place Order button");

    @Override
    protected By screenLocator() {
        return SCREEN;
    }

    @Step("Place order")
    public CheckoutCompletePage placeOrder() {
        scrollAndTap(PLACE_ORDER);
        return new CheckoutCompletePage();
    }
}
