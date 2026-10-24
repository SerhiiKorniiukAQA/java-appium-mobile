package io.github.serhiikorniiuk.mobile.platform;

import io.github.serhiikorniiuk.mobile.base.BaseMobileTest;
import io.github.serhiikorniiuk.mobile.pages.CartPage;
import io.github.serhiikorniiuk.mobile.pages.LoginPage;
import io.github.serhiikorniiuk.mobile.pages.ProductDetailsPage;
import io.github.serhiikorniiuk.mobile.utils.MobileActions;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

@Epic("Platform specifics")
@Feature("Deep links")
public class DeepLinkTest extends BaseMobileTest {

    @DataProvider(name = "productIds")
    public Object[][] productIds() {
        return new Object[][]{{1}, {3}};
    }

    @Test(groups = "regression", dataProvider = "productIds",
            description = "TC-PLAT-003: Deep link opens product details")
    @Severity(SeverityLevel.NORMAL)
    public void deepLinkOpensProduct(int productId) {
        catalog();

        MobileActions.openDeepLink(deepLink("product-details/" + productId));

        ProductDetailsPage product = new ProductDetailsPage();
        assertThat(product.isOpened()).as("Product screen opened by deep link").isTrue();
        assertThat(product.price()).as("Product price").isPositive();
    }

    @Test(groups = "regression", description = "TC-PLAT-004: Deep link opens empty cart")
    @Severity(SeverityLevel.NORMAL)
    public void deepLinkOpensCart() {
        catalog();

        MobileActions.openDeepLink(deepLink("cart"));

        CartPage cart = new CartPage();
        assertThat(cart.isOpened() || cart.isEmpty()).as("Cart screen opened by deep link").isTrue();
        assertThat(cart.productRowsCount()).as("Rows in new user's cart").isZero();
    }

    @Test(groups = "regression", description = "TC-PLAT-005: Deep link opens login screen")
    @Severity(SeverityLevel.MINOR)
    public void deepLinkOpensLogin() {
        catalog();

        MobileActions.openDeepLink(deepLink("login"));

        assertThat(new LoginPage().isOpened()).as("Login screen opened by deep link").isTrue();
    }
}
