package io.github.serhiikorniiuk.mobile.pages;

import io.qameta.allure.Step;
import org.openqa.selenium.By;

import java.math.BigDecimal;

public class ProductDetailsPage extends BasePage {

    private static final By SCREEN = id("product screen");
    private static final By PRICE = id("product price");
    private static final By PLUS = id("counter plus button");
    private static final By MINUS = id("counter minus button");
    private static final By AMOUNT = id("counter amount");
    private static final By ADD_TO_CART = id("Add To Cart button");

    public final AppHeader header = new AppHeader();

    @Override
    protected By screenLocator() {
        return SCREEN;
    }

    public BigDecimal price() {
        return CatalogPage.parsePrice(textOf(PRICE));
    }

    public int quantity() {
        return Integer.parseInt(textOf(scrollTo(AMOUNT)).replaceAll("\\D", ""));
    }

    @Step("Increase quantity by {times}")
    public ProductDetailsPage increaseQuantity(int times) {
        for (int i = 0; i < times; i++) {
            scrollAndTap(PLUS);
        }
        return this;
    }

    @Step("Decrease quantity")
    public ProductDetailsPage decreaseQuantity() {
        scrollAndTap(MINUS);
        return this;
    }

    @Step("Add product to cart")
    public ProductDetailsPage addToCart() {
        scrollAndTap(ADD_TO_CART);
        return this;
    }
}
