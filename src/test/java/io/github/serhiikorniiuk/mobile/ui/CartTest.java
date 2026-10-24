package io.github.serhiikorniiuk.mobile.ui;

import io.github.serhiikorniiuk.mobile.base.BaseMobileTest;
import io.github.serhiikorniiuk.mobile.pages.CartPage;
import io.github.serhiikorniiuk.mobile.pages.ProductDetailsPage;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

@Epic("Mobile UI")
@Feature("Cart")
public class CartTest extends BaseMobileTest {

    @Test(groups = {"smoke", "regression"}, description = "TC-CART-001: Add product to cart")
    @Severity(SeverityLevel.BLOCKER)
    public void addProductToCart() {
        ProductDetailsPage product = catalog().openProduct(0).addToCart();

        assertThat(product.header.cartBadgeCount()).as("Cart badge").isEqualTo(1);

        CartPage cart = product.header.openCart();
        assertThat(cart.isOpened()).as("Cart screen is opened").isTrue();
        assertThat(cart.productRowsCount()).as("Rows in cart").isEqualTo(1);
    }

    @Test(groups = "regression", description = "TC-CART-002: Add product with quantity 3")
    @Severity(SeverityLevel.CRITICAL)
    public void addProductWithQuantity() {
        ProductDetailsPage product = catalog().openProduct(1).increaseQuantity(2);
        assertThat(product.quantity()).as("Selected quantity").isEqualTo(3);

        product.addToCart();

        assertThat(product.header.cartBadgeCount()).as("Cart badge").isEqualTo(3);
        assertThat(product.header.openCart().firstItemQuantity()).as("Quantity in cart").isEqualTo(3);
    }

    @Test(groups = "regression", description = "TC-CART-003: Remove the only product from cart")
    @Severity(SeverityLevel.CRITICAL)
    public void removeProductFromCart() {
        CartPage cart = catalog().openProduct(0).addToCart().header.openCart();

        cart.removeFirstItem();

        assertThat(cart.isEmpty()).as("Empty cart state with 'Go Shopping' is shown").isTrue();
        assertThat(cart.header.cartBadgeCount()).as("Cart badge").isZero();
    }
}
