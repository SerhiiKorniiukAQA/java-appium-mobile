package io.github.serhiikorniiuk.mobile.pages;

import io.github.serhiikorniiuk.mobile.data.Address;
import io.github.serhiikorniiuk.mobile.utils.MobileActions;
import io.qameta.allure.Step;
import org.openqa.selenium.By;

public class CheckoutAddressPage extends BasePage {

    private static final By SCREEN = id("checkout address screen");
    private static final By FULL_NAME = id("Full Name* input field");
    private static final By ADDRESS_LINE_1 = id("Address Line 1* input field");
    private static final By CITY = id("City* input field");
    private static final By STATE = id("State/Region input field");
    private static final By ZIP = id("Zip Code* input field");
    private static final By COUNTRY = id("Country* input field");
    private static final By TO_PAYMENT = id("To Payment button");

    @Override
    protected By screenLocator() {
        return SCREEN;
    }

    @Step("Fill shipping address")
    public CheckoutAddressPage fill(Address address) {
        type(FULL_NAME, address.fullName());
        type(ADDRESS_LINE_1, address.line1());
        type(CITY, address.city());
        typeAfterScroll(STATE, address.state());
        typeAfterScroll(ZIP, address.zipCode());
        typeAfterScroll(COUNTRY, address.country());
        return this;
    }

    @Step("Go to payment")
    public CheckoutPaymentPage toPayment() {
        scrollAndTap(TO_PAYMENT);
        return new CheckoutPaymentPage();
    }

    private void typeAfterScroll(By locator, String text) {
        MobileActions.hideKeyboard();
        scrollTo(locator);
        type(locator, text);
    }
}
