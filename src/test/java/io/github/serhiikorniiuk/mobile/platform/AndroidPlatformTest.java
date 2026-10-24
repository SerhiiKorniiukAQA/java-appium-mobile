package io.github.serhiikorniiuk.mobile.platform;

import io.github.serhiikorniiuk.mobile.base.BaseMobileTest;
import io.github.serhiikorniiuk.mobile.config.Config;
import io.github.serhiikorniiuk.mobile.driver.DriverManager;
import io.github.serhiikorniiuk.mobile.pages.CatalogPage;
import io.github.serhiikorniiuk.mobile.pages.GeoLocationPage;
import io.github.serhiikorniiuk.mobile.utils.MobileActions;
import io.github.serhiikorniiuk.mobile.utils.MobileActions.AppState;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Android specific behaviour: runtime permissions, location, system notification shade.
 * Excluded from the iOS suite via the "android-only" group.
 */
@Epic("Platform specifics")
@Feature("Android system integration")
public class AndroidPlatformTest extends BaseMobileTest {

    private static final double KYIV_LAT = 50.4501;
    private static final double KYIV_LON = 30.5234;

    @Test(groups = {"regression", "android-only"},
            description = "TC-PLAT-006: Granted location permission lets app show device coordinates")
    @Severity(SeverityLevel.NORMAL)
    public void geoLocationWithGrantedPermission() {
        catalog();
        MobileActions.changePermissions("grant",
                "android.permission.ACCESS_FINE_LOCATION", "android.permission.ACCESS_COARSE_LOCATION");
        MobileActions.setGeolocation(KYIV_LAT, KYIV_LON);

        MobileActions.openDeepLink(deepLink("geo-locations"));

        GeoLocationPage geo = new GeoLocationPage();
        assertThat(geo.isOpened()).as("Geo location screen").isTrue();
        new WebDriverWait(DriverManager.driver(), Config.timeout())
                .until(d -> geo.latitude().startsWith("50.45"));
        assertThat(geo.latitude()).as("Latitude").startsWith("50.45");
        assertThat(geo.longitude()).as("Longitude").startsWith("30.52");
    }

    @Test(groups = {"regression", "android-only"},
            description = "TC-PLAT-007: Notification shade opens over the app and app resumes after closing it")
    @Severity(SeverityLevel.MINOR)
    public void notificationShadeInterruption() {
        catalog();

        MobileActions.openNotifications();
        new WebDriverWait(DriverManager.driver(), Config.timeout())
                .until(ExpectedConditions.presenceOfElementLocated(By.xpath("//*[@package='com.android.systemui']")));

        DriverManager.driver().navigate().back();

        assertThat(MobileActions.appState()).isEqualTo(AppState.RUNNING_IN_FOREGROUND);
        assertThat(new CatalogPage().isOpened()).as("Catalog is shown after closing shade").isTrue();
    }
}
