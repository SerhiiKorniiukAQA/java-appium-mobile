package io.github.serhiikorniiuk.mobile.e2e;

import io.github.serhiikorniiuk.mobile.base.BaseMobileTest;
import io.github.serhiikorniiuk.mobile.data.Address;
import io.github.serhiikorniiuk.mobile.data.TestDataFactory;
import io.github.serhiikorniiuk.mobile.data.User;
import io.github.serhiikorniiuk.mobile.pages.CartPage;
import io.github.serhiikorniiuk.mobile.pages.CatalogPage;
import io.github.serhiikorniiuk.mobile.pages.CheckoutAddressPage;
import io.github.serhiikorniiuk.mobile.pages.CheckoutCompletePage;
import io.github.serhiikorniiuk.mobile.pages.CheckoutPaymentPage;
import io.github.serhiikorniiuk.mobile.pages.CheckoutReviewPage;
import io.github.serhiikorniiuk.mobile.pages.LoginPage;
import io.qameta.allure.Allure;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

@Epic("End-to-End")
@Feature("Checkout")
public class CheckoutE2ETest extends BaseMobileTest {

    @Test(groups = {"smoke", "regression", "e2e"},
            description = "TC-E2E-001: Guest adds product, logs in during checkout and places an order")
    @Severity(SeverityLevel.BLOCKER)
    public void guestCompletesPurchase() {
        Address address = TestDataFactory.address();

        Allure.step("Add product to cart", () ->
                catalog().openProduct(0).addToCart());

        Allure.step("Open cart and proceed to checkout", () -> {
            CartPage cart = new CatalogPage().header.openCart();
            assertThat(cart.productRowsCount()).isEqualTo(1);
            cart.proceedToCheckout();
        });

        Allure.step("Log in when checkout requires authentication", () -> {
            LoginPage login = new LoginPage();
            assertThat(login.isOpened()).as("Checkout asks guest to log in").isTrue();
            login.loginAs(User.STANDARD);
        });

        CheckoutAddressPage addressPage = new CheckoutAddressPage();
        assertThat(addressPage.isOpened()).as("Shipping address screen").isTrue();
        CheckoutPaymentPage payment = addressPage.fill(address).toPayment();

        assertThat(payment.isOpened()).as("Payment screen").isTrue();
        CheckoutReviewPage review = payment.fill(TestDataFactory.card(address.fullName())).reviewOrder();

        assertThat(review.isOpened()).as("Review order screen").isTrue();
        CheckoutCompletePage complete = review.placeOrder();

        assertThat(complete.isOpened()).as("Order confirmation screen").isTrue();
        CatalogPage catalog = complete.continueShopping();
        assertThat(catalog.isOpened()).as("Back to catalog").isTrue();
        assertThat(catalog.header.cartBadgeCount()).as("Cart is cleared after order").isZero();
    }
}
