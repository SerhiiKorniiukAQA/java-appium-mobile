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

import static org.assertj.core.api.Assertions.assertThat;

@Epic("Mobile UI")
@Feature("Catalog")
public class CatalogTest extends BaseMobileTest {

    @Test(groups = {"smoke", "regression"},
            description = "TC-CAT-001: Catalog shows products with title and price")
    @Severity(SeverityLevel.BLOCKER)
    public void catalogShowsProducts() {
        CatalogPage catalog = catalog();

        assertThat(catalog.visibleProductsCount()).as("Visible products").isPositive();
        assertThat(catalog.visibleProductTitles()).as("Product titles").allSatisfy(t -> assertThat(t).isNotBlank());
        assertThat(catalog.visibleProductPrices()).as("Product prices").allSatisfy(p -> assertThat(p).isPositive());
    }

    @Test(groups = "regression", description = "TC-CAT-002: Sort by price ascending")
    @Severity(SeverityLevel.NORMAL)
    public void sortByPriceAscending() {
        List<BigDecimal> prices = catalog().sortBy(CatalogPage.Sort.PRICE_ASC).visibleProductPrices();

        assertThat(prices).as("Prices after sort").hasSizeGreaterThan(1).isSorted();
    }

    @Test(groups = "regression", description = "TC-CAT-003: Sort by name descending")
    @Severity(SeverityLevel.NORMAL)
    public void sortByNameDescending() {
        List<String> titles = catalog().sortBy(CatalogPage.Sort.NAME_DESC).visibleProductTitles();

        assertThat(titles).as("Titles after sort")
                .hasSizeGreaterThan(1)
                .isSortedAccordingTo(String.CASE_INSENSITIVE_ORDER.reversed());
    }

    @Test(groups = "regression", description = "TC-CAT-004: Product details price matches catalog price")
    @Severity(SeverityLevel.CRITICAL)
    public void productDetailsMatchCatalog() {
        CatalogPage catalog = catalog();
        BigDecimal catalogPrice = catalog.visibleProductPrices().get(0);

        ProductDetailsPage details = catalog.openProduct(0);

        assertThat(details.isOpened()).as("Product screen is opened").isTrue();
        assertThat(details.price()).as("Price on details").isEqualByComparingTo(catalogPrice);
        assertThat(details.quantity()).as("Default quantity").isEqualTo(1);
    }

    @Test(groups = "regression", description = "TC-CAT-005: Catalog list is scrollable to the last product")
    @Severity(SeverityLevel.MINOR)
    public void catalogIsScrollable() {
        CatalogPage catalog = catalog();
        List<String> initiallyVisible = catalog.visibleProductTitles();

        String lastTitle = catalog.scrollToLastProductTitle();

        assertThat(lastTitle).as("Last product after scroll is a new, previously hidden item")
                .isNotBlank()
                .isNotIn(initiallyVisible);
    }
}
