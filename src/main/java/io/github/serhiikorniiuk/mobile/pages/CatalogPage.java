package io.github.serhiikorniiuk.mobile.pages;

import io.github.serhiikorniiuk.mobile.utils.Gestures;
import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;

public class CatalogPage extends BasePage {

    private static final By SCREEN = id("products screen");
    private static final By ITEM = id("store item");
    private static final By ITEM_TITLE = id("store item text");
    private static final By ITEM_PRICE = id("store item price");
    private static final By SORT_BUTTON = id("sort button");
    private static final Duration APP_START_TIMEOUT = Duration.ofSeconds(60);

    public enum Sort {
        NAME_ASC("nameAsc"), NAME_DESC("nameDesc"), PRICE_ASC("priceAsc"), PRICE_DESC("priceDesc");

        private final String accessibilityId;

        Sort(String accessibilityId) {
            this.accessibilityId = accessibilityId;
        }
    }

    public final AppHeader header = new AppHeader();

    @Override
    protected By screenLocator() {
        return SCREEN;
    }

    /** First screen after app launch: cold start on a CI emulator can take a while. */
    public CatalogPage waitOpened() {
        new WebDriverWait(driver, APP_START_TIMEOUT).until(ExpectedConditions.visibilityOfElementLocated(SCREEN));
        return this;
    }

    public int visibleProductsCount() {
        waitVisible(ITEM);
        return driver.findElements(ITEM).size();
    }

    public List<String> visibleProductTitles() {
        waitVisible(ITEM_TITLE);
        return driver.findElements(ITEM_TITLE).stream().map(this::textOf).toList();
    }

    public List<BigDecimal> visibleProductPrices() {
        waitVisible(ITEM_PRICE);
        return driver.findElements(ITEM_PRICE).stream().map(this::textOf).map(CatalogPage::parsePrice).toList();
    }

    @Step("Sort catalog by {sort}")
    public CatalogPage sortBy(Sort sort) {
        tap(SORT_BUTTON);
        tap(id(sort.accessibilityId));
        return this;
    }

    @Step("Open product #{index} from catalog")
    public ProductDetailsPage openProduct(int index) {
        waitVisible(ITEM);
        List<WebElement> items = driver.findElements(ITEM);
        items.get(index).click();
        return new ProductDetailsPage();
    }

    @Step("Scroll catalog to the last product")
    public String scrollToLastProductTitle() {
        String previousLast = "";
        for (int i = 0; i < 6; i++) {
            List<String> titles = visibleProductTitles();
            String last = titles.get(titles.size() - 1);
            if (last.equals(previousLast)) {
                return last;
            }
            previousLast = last;
            Gestures.swipe(driver, Gestures.Direction.UP);
        }
        return previousLast;
    }

    static BigDecimal parsePrice(String text) {
        return new BigDecimal(text.replaceAll("[^0-9.]", ""));
    }
}
