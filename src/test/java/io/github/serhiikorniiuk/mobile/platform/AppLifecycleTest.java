package io.github.serhiikorniiuk.mobile.platform;

import io.github.serhiikorniiuk.mobile.base.BaseMobileTest;
import io.github.serhiikorniiuk.mobile.pages.CatalogPage;
import io.github.serhiikorniiuk.mobile.pages.ProductDetailsPage;
import io.github.serhiikorniiuk.mobile.utils.MobileActions;
import io.github.serhiikorniiuk.mobile.utils.MobileActions.AppState;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import org.testng.annotations.Test;

import static io.github.serhiikorniiuk.mobile.base.Checks.verify;
import static org.assertj.core.api.Assertions.assertThat;

@Epic("Platform specifics")
@Feature("App lifecycle")
public class AppLifecycleTest extends BaseMobileTest {

    @Test(groups = "regression",
            description = "TC-PLAT-001: Cart state survives app going to background and back")
    @Severity(SeverityLevel.CRITICAL)
    public void cartSurvivesBackground() {
        ProductDetailsPage product = catalog().openProduct(0).addToCart();
        verify("cart badge shows 1 before going to background", () ->
                assertThat(product.header.cartBadgeCount()).isEqualTo(1));

        MobileActions.backgroundApp(3);

        verify("app is back in foreground", () ->
                assertThat(MobileActions.appState()).isEqualTo(AppState.RUNNING_IN_FOREGROUND));
        verify("the same product screen is restored", () ->
                assertThat(product.isOpened()).isTrue());
        verify("cart badge still shows 1", () ->
                assertThat(product.header.cartBadgeCount()).isEqualTo(1));
    }

    @Test(groups = "regression",
            description = "TC-PLAT-002: App is terminated and cold-started back to catalog")
    @Severity(SeverityLevel.NORMAL)
    public void terminateAndRelaunch() {
        catalog();

        MobileActions.terminateApp();
        verify("app state is NOT_RUNNING", () ->
                assertThat(MobileActions.appState()).isEqualTo(AppState.NOT_RUNNING));

        MobileActions.activateApp();
        verify("app state is RUNNING_IN_FOREGROUND", () ->
                assertThat(MobileActions.appState()).isEqualTo(AppState.RUNNING_IN_FOREGROUND));
        verify("catalog is shown after cold start", () ->
                assertThat(new CatalogPage().isOpened()).isTrue());
    }
}
