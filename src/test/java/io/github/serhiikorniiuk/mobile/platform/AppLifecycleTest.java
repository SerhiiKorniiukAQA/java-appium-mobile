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

import static org.assertj.core.api.Assertions.assertThat;

@Epic("Platform specifics")
@Feature("App lifecycle")
public class AppLifecycleTest extends BaseMobileTest {

    @Test(groups = "regression",
            description = "TC-PLAT-001: Cart state survives app going to background and back")
    @Severity(SeverityLevel.CRITICAL)
    public void cartSurvivesBackground() {
        ProductDetailsPage product = catalog().openProduct(0).addToCart();
        assertThat(product.header.cartBadgeCount()).isEqualTo(1);

        MobileActions.backgroundApp(3);

        assertThat(MobileActions.appState()).as("App state after background")
                .isEqualTo(AppState.RUNNING_IN_FOREGROUND);
        assertThat(product.isOpened()).as("Same screen is restored").isTrue();
        assertThat(product.header.cartBadgeCount()).as("Cart badge after resume").isEqualTo(1);
    }

    @Test(groups = "regression",
            description = "TC-PLAT-002: App is terminated and cold-started back to catalog")
    @Severity(SeverityLevel.NORMAL)
    public void terminateAndRelaunch() {
        catalog();

        MobileActions.terminateApp();
        assertThat(MobileActions.appState()).as("State after terminate").isEqualTo(AppState.NOT_RUNNING);

        MobileActions.activateApp();
        assertThat(MobileActions.appState()).as("State after activate").isEqualTo(AppState.RUNNING_IN_FOREGROUND);
        assertThat(new CatalogPage().isOpened()).as("Catalog after cold start").isTrue();
    }
}
