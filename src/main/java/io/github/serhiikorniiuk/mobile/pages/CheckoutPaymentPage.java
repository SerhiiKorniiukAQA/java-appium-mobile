package io.github.serhiikorniiuk.mobile.pages;

import io.github.serhiikorniiuk.mobile.data.PaymentCard;
import io.github.serhiikorniiuk.mobile.utils.MobileActions;
import io.qameta.allure.Step;
import org.openqa.selenium.By;

public class CheckoutPaymentPage extends BasePage {

    private static final By SCREEN = id("checkout payment screen");
    private static final By HOLDER = id("Full Name* input field");
    private static final By CARD_NUMBER = id("Card Number* input field");
    private static final By EXPIRATION = id("Expiration Date* input field");
    private static final By SECURITY_CODE = id("Security Code* input field");
    private static final By REVIEW_ORDER = id("Review Order button");

    @Override
    protected By screenLocator() {
        return SCREEN;
    }

    @Step("Fill payment method")
    public CheckoutPaymentPage fill(PaymentCard card) {
        type(HOLDER, card.holder());
        type(CARD_NUMBER, card.number());
        type(EXPIRATION, card.expiration());
        type(SECURITY_CODE, card.securityCode());
        MobileActions.hideKeyboard();
        return this;
    }

    @Step("Review order")
    public CheckoutReviewPage reviewOrder() {
        scrollAndTap(REVIEW_ORDER);
        return new CheckoutReviewPage();
    }
}
