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

import static io.github.serhiikorniiuk.mobile.base.Checks.verify;
import static org.assertj.core.api.Assertions.assertThat;

@Epic("End-to-End")
@Feature("Checkout")
public class CheckoutE2ETest extends BaseMobileTest {

    @Test(groups = {"smoke", "regression", "e2e"},
            description = "TC-E2E-001: Guest adds product, logs in during checkout and places an order")
    @Severity(SeverityLevel.BLOCKER)
    public void guestCompletesPurchase() {
        Address address = TestDataFactory.address();
        Allure.parameter("Shipping name", address.fullName());
        Allure.parameter("City", address.city());

        Allure.step("1. Add product to cart", () ->
                catalog().openProduct(0).addToCart());

        Allure.step("2. Open cart and proceed to checkout", () -> {
            CartPage cart = new CatalogPage().header.openCart();
            verify("cart contains 1 product", () -> assertThat(cart.productRowsCount()).isEqualTo(1));
            cart.proceedToCheckout();
        });

        Allure.step("3. Log in when checkout requires authentication", () -> {
            LoginPage login = new LoginPage();
            verify("checkout asks guest to log in", () -> assertThat(login.isOpened()).isTrue());
            login.loginAs(User.STANDARD);
        });

        Allure.step("4. Enter shipping address", () -> {
            CheckoutAddressPage addressPage = new CheckoutAddressPage();
            verify("shipping address screen is opened", () -> assertThat(addressPage.isOpened()).isTrue());
            addressPage.fill(address).toPayment();
        });

        Allure.step("5. Enter payment method", () -> {
            CheckoutPaymentPage payment = new CheckoutPaymentPage();
            verify("payment screen is opened", () -> assertThat(payment.isOpened()).isTrue());
            payment.fill(TestDataFactory.card(address.fullName())).reviewOrder();
        });

        Allure.step("6. Review and place order", () -> {
            CheckoutReviewPage review = new CheckoutReviewPage();
            verify("review order screen is opened", () -> assertThat(review.isOpened()).isTrue());
            review.placeOrder();
        });

        Allure.step("7. Confirm order and return to catalog", () -> {
            CheckoutCompletePage complete = new CheckoutCompletePage();
            verify("order confirmation screen is shown", () -> assertThat(complete.isOpened()).isTrue());
            CatalogPage catalog = complete.continueShopping();
            verify("user is back on catalog", () -> assertThat(catalog.isOpened()).isTrue());
            verify("cart is cleared after order", () -> assertThat(catalog.header.cartBadgeCount()).isZero());
        });
    }
}
