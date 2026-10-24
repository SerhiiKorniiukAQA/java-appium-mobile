package io.github.serhiikorniiuk.mobile.pages;

import io.qameta.allure.Step;
import org.openqa.selenium.By;

public class CheckoutCompletePage extends BasePage {

    private static final By SCREEN = id("checkout complete screen");
    private static final By CONTINUE_SHOPPING = id("Continue Shopping button");

    @Override
    protected By screenLocator() {
        return SCREEN;
    }

    @Step("Continue shopping")
    public CatalogPage continueShopping() {
        scrollAndTap(CONTINUE_SHOPPING);
        return new CatalogPage();
    }
}
