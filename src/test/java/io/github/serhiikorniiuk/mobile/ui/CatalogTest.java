package io.github.serhiikorniiuk.mobile.ui;

import io.github.serhiikorniiuk.mobile.base.BaseMobileTest;
import io.github.serhiikorniiuk.mobile.pages.CatalogPage;
import io.github.serhiikorniiuk.mobile.pages.ProductDetailsPage;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.util.List;

import static io.github.serhiikorniiuk.mobile.base.Checks.verify;
import static org.assertj.core.api.Assertions.assertThat;

@Epic("Mobile UI")
@Feature("Catalog")
public class CatalogTest extends BaseMobileTest {

    @Test(groups = {"smoke", "regression"},
            description = "TC-CAT-001: Catalog shows products with title and price")
    @Severity(SeverityLevel.BLOCKER)
    public void catalogShowsProducts() {
        CatalogPage catalog = catalog();

        verify("products are displayed", () ->
                assertThat(catalog.visibleProductsCount()).isPositive());
        verify("every product has a title", () ->
                assertThat(catalog.visibleProductTitles()).allSatisfy(t -> assertThat(t).isNotBlank()));
        verify("every product has a positive price", () ->
                assertThat(catalog.visibleProductPrices()).allSatisfy(p -> assertThat(p).isPositive()));
    }

    @Test(groups = "regression", description = "TC-CAT-002: Sort by price ascending")
    @Severity(SeverityLevel.NORMAL)
    public void sortByPriceAscending() {
        List<BigDecimal> prices = catalog().sortBy(CatalogPage.Sort.PRICE_ASC).visibleProductPrices();

        verify("prices are in ascending order " + prices, () ->
                assertThat(prices).hasSizeGreaterThan(1).isSorted());
    }

    @Test(groups = "regression", description = "TC-CAT-003: Sort by name descending")
    @Severity(SeverityLevel.NORMAL)
    public void sortByNameDescending() {
        List<String> titles = catalog().sortBy(CatalogPage.Sort.NAME_DESC).visibleProductTitles();

        verify("titles are in Z-A order " + titles, () ->
                assertThat(titles).hasSizeGreaterThan(1)
                        .isSortedAccordingTo(String.CASE_INSENSITIVE_ORDER.reversed()));
    }

    @Test(groups = "regression", description = "TC-CAT-004: Product details price matches catalog price")
    @Severity(SeverityLevel.CRITICAL)
    public void productDetailsMatchCatalog() {
        CatalogPage catalog = catalog();
        BigDecimal catalogPrice = catalog.visibleProductPrices().get(0);

        ProductDetailsPage details = catalog.openProduct(0);

        verify("product screen is opened", () ->
                assertThat(details.isOpened()).isTrue());
        verify("price on details equals catalog price " + catalogPrice, () ->
                assertThat(details.price()).isEqualByComparingTo(catalogPrice));
        verify("default quantity is 1", () ->
                assertThat(details.quantity()).isEqualTo(1));
    }

    @Test(groups = "regression", description = "TC-CAT-005: Catalog list is scrollable to the last product")
    @Severity(SeverityLevel.MINOR)
    public void catalogIsScrollable() {
        CatalogPage catalog = catalog();
        List<String> initiallyVisible = catalog.visibleProductTitles();

        String lastTitle = catalog.scrollToLastProductTitle();

        verify("product hidden on the first screen is shown: " + lastTitle, () ->
                assertThat(lastTitle).isNotBlank().isNotIn(initiallyVisible));
    }
}
