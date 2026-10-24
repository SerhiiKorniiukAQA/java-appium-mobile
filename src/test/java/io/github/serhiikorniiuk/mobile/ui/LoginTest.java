package io.github.serhiikorniiuk.mobile.ui;

import io.github.serhiikorniiuk.mobile.base.BaseMobileTest;
import io.github.serhiikorniiuk.mobile.data.User;
import io.github.serhiikorniiuk.mobile.pages.CatalogPage;
import io.github.serhiikorniiuk.mobile.pages.LoginPage;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static io.github.serhiikorniiuk.mobile.base.Checks.verify;
import static org.assertj.core.api.Assertions.assertThat;

@Epic("Mobile UI")
@Feature("Login")
public class LoginTest extends BaseMobileTest {

    @Test(groups = {"smoke", "regression"},
            description = "TC-LOGIN-001: User logs in with valid credentials")
    @Severity(SeverityLevel.BLOCKER)
    public void validLogin() {
        LoginPage login = catalog().header.openLogin();
        verify("login screen is opened", () ->
                assertThat(login.isOpened()).isTrue());

        login.loginAs(User.STANDARD);

        CatalogPage catalog = new CatalogPage();
        verify("user is redirected to catalog", () ->
                assertThat(catalog.isOpened()).isTrue());
        verify("'Log Out' is available in menu", () ->
                assertThat(catalog.header.isLogoutAvailable()).isTrue());
    }

    @DataProvider(name = "invalidCredentials")
    public Object[][] invalidCredentials() {
        return new Object[][]{
                {"TC-LOGIN-002", "", "10203040", "Username is required"},
                {"TC-LOGIN-003", User.STANDARD.username(), "", "Password is required"},
                {"TC-LOGIN-004", User.LOCKED.username(), User.LOCKED.password(), "locked out"},
                {"TC-LOGIN-005", User.UNKNOWN.username(), User.UNKNOWN.password(), "do not match"},
        };
    }

    @Test(groups = "regression", dataProvider = "invalidCredentials",
            description = "TC-LOGIN-002..005: Login is rejected with a clear validation message")
    @Severity(SeverityLevel.CRITICAL)
    public void invalidLoginShowsError(String caseId, String username, String password, String expectedError) {
        LoginPage login = catalog().header.openLogin();

        login.submit(username, password);

        verify(caseId + ": user stays on login screen", () ->
                assertThat(login.isOpened()).isTrue());
        verify(caseId + ": message contains '" + expectedError + "'", () ->
                assertThat(login.anyError()).containsIgnoringCase(expectedError));
    }
}
