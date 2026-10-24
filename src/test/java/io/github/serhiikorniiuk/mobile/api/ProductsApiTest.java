package io.github.serhiikorniiuk.mobile.api;

import io.github.serhiikorniiuk.mobile.api.models.Product;
import io.github.serhiikorniiuk.mobile.api.models.ProductsPage;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import java.util.Map;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.assertj.core.api.Assertions.assertThat;

@Epic("API")
@Feature("Products")
public class ProductsApiTest {

    @Test(groups = {"smoke", "api"}, description = "TC-API-001: Product list is returned with default pagination")
    @Severity(SeverityLevel.BLOCKER)
    public void productListIsReturned() {
        ProductsPage page = StoreApi.products(30, 0)
                .then().statusCode(200)
                .extract().as(ProductsPage.class);

        assertThat(page.products()).hasSize(30);
        assertThat(page.total()).isGreaterThan(page.products().size());
        assertThat(page.products()).allSatisfy(p -> {
            assertThat(p.title()).isNotBlank();
            assertThat(p.price()).isPositive();
        });
    }

    @Test(groups = "api", description = "TC-API-002: Pagination with limit and skip")
    @Severity(SeverityLevel.NORMAL)
    public void paginationWorks() {
        ProductsPage page = StoreApi.products(10, 10)
                .then().statusCode(200)
                .extract().as(ProductsPage.class);

        assertThat(page.limit()).isEqualTo(10);
        assertThat(page.skip()).isEqualTo(10);
        assertThat(page.products()).extracting(Product::id).containsExactly(11, 12, 13, 14, 15, 16, 17, 18, 19, 20);
    }

    @Test(groups = "api", description = "TC-API-003: Single product matches JSON schema")
    @Severity(SeverityLevel.CRITICAL)
    public void productMatchesSchema() {
        StoreApi.product(1)
                .then().statusCode(200)
                .body(matchesJsonSchemaInClasspath("schemas/product-schema.json"));
    }

    @Test(groups = "api", description = "TC-API-004: Unknown product returns 404 with message")
    @Severity(SeverityLevel.NORMAL)
    public void unknownProductReturns404() {
        Response response = StoreApi.product(999_999);

        assertThat(response.statusCode()).isEqualTo(404);
        assertThat(response.jsonPath().getString("message")).containsIgnoringCase("not found");
    }

    @Test(groups = "api", description = "TC-API-005: Field selection returns only requested fields")
    @Severity(SeverityLevel.MINOR)
    public void selectReturnsOnlyRequestedFields() {
        Response response = StoreApi.productFields(1, "title,price");

        assertThat(response.statusCode()).isEqualTo(200);
        Map<String, Object> body = response.jsonPath().getMap("");
        assertThat(body).containsOnlyKeys("id", "title", "price");
    }

    @Test(groups = "api", description = "TC-API-006: Category filter returns only that category")
    @Severity(SeverityLevel.NORMAL)
    public void categoryFilter() {
        ProductsPage page = StoreApi.productsByCategory("smartphones")
                .then().statusCode(200)
                .extract().as(ProductsPage.class);

        assertThat(page.products()).isNotEmpty()
                .extracting(Product::category).containsOnly("smartphones");
    }
}
