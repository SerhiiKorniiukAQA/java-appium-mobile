package io.github.serhiikorniiuk.mobile.ui;

import io.github.serhiikorniiuk.mobile.base.BaseMobileTest;
import io.github.serhiikorniiuk.mobile.pages.CartPage;
import io.github.serhiikorniiuk.mobile.pages.ProductDetailsPage;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import org.testng.annotations.Test;

import static io.github.serhiikorniiuk.mobile.base.Checks.verify;
import static org.assertj.core.api.Assertions.assertThat;

@Epic("Mobile UI")
@Feature("Cart")
public class CartTest extends BaseMobileTest {

    @Test(groups = {"smoke", "regression"}, description = "TC-CART-001: Add product to cart")
    @Severity(SeverityLevel.BLOCKER)
    public void addProductToCart() {
        ProductDetailsPage product = catalog().openProduct(0).addToCart();

        verify("cart badge shows 1", () ->
                assertThat(product.header.cartBadgeCount()).isEqualTo(1));

        CartPage cart = product.header.openCart();
        verify("cart screen is opened", () ->
                assertThat(cart.isOpened()).isTrue());
        verify("cart contains 1 product row", () ->
                assertThat(cart.productRowsCount()).isEqualTo(1));
    }

    @Test(groups = "regression", description = "TC-CART-002: Add product with quantity 3")
    @Severity(SeverityLevel.CRITICAL)
    public void addProductWithQuantity() {
        ProductDetailsPage product = catalog().openProduct(1).increaseQuantity(2);
        verify("selected quantity is 3", () ->
                assertThat(product.quantity()).isEqualTo(3));

        product.addToCart();

        verify("cart badge shows 3", () ->
                assertThat(product.header.cartBadgeCount()).isEqualTo(3));
        CartPage cart = product.header.openCart();
        verify("quantity in cart is 3", () ->
                assertThat(cart.firstItemQuantity()).isEqualTo(3));
    }

    @Test(groups = "regression", description = "TC-CART-003: Remove the only product from cart")
    @Severity(SeverityLevel.CRITICAL)
    public void removeProductFromCart() {
        CartPage cart = catalog().openProduct(0).addToCart().header.openCart();

        cart.removeFirstItem();

        verify("empty cart state with 'Go Shopping' is shown", () ->
                assertThat(cart.isEmpty()).isTrue());
        verify("cart badge is cleared", () ->
                assertThat(cart.header.cartBadgeCount()).isZero());
    }
}
