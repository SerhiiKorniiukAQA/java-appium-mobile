package io.github.serhiikorniiuk.mobile.api;

import io.github.serhiikorniiuk.mobile.api.models.CartRequest;
import io.github.serhiikorniiuk.mobile.api.models.LoginRequest;
import io.qameta.allure.Step;
import io.restassured.response.Response;

/**
 * Service layer for the store backend (DummyJSON). Tests talk to this class,
 * not to raw endpoints, so a changed URL is fixed in one place.
 */
public final class StoreApi {

    private StoreApi() {
    }

    // ---------- Products ----------

    @Step("GET /products?limit={limit}&skip={skip}")
    public static Response products(int limit, int skip) {
        return ApiClient.request()
                .queryParam("limit", limit)
                .queryParam("skip", skip)
                .get("/products");
    }

    @Step("GET /products/{id}")
    public static Response product(int id) {
        return ApiClient.request().get("/products/{id}", id);
    }

    @Step("GET /products/{id}?select={fields}")
    public static Response productFields(int id, String fields) {
        return ApiClient.request().queryParam("select", fields).get("/products/{id}", id);
    }

    @Step("GET /products/category/{category}")
    public static Response productsByCategory(String category) {
        return ApiClient.request().get("/products/category/{category}", category);
    }

    // ---------- Auth ----------

    @Step("POST /auth/login as {request.username}")
    public static Response login(LoginRequest request) {
        return ApiClient.request().body(request).post("/auth/login");
    }

    @Step("GET /auth/me")
    public static Response me(String accessToken) {
        return ApiClient.authorized(accessToken).get("/auth/me");
    }

    @Step("GET /auth/me without token")
    public static Response meAnonymous() {
        return ApiClient.request().get("/auth/me");
    }

    // ---------- Carts ----------

    @Step("POST /carts/add")
    public static Response addCart(CartRequest request) {
        return ApiClient.request().body(request).post("/carts/add");
    }
}
