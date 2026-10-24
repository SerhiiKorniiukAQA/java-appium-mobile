package io.github.serhiikorniiuk.mobile.api;

import io.github.serhiikorniiuk.mobile.config.Config;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.given;

/** Base request specification: base URL, JSON, Allure request/response attachments. */
public final class ApiClient {

    private ApiClient() {
    }

    public static RequestSpecification spec() {
        return new RequestSpecBuilder()
                .setBaseUri(Config.apiBaseUrl())
                .setContentType(ContentType.JSON)
                .setAccept(ContentType.JSON)
                .addFilter(new AllureRestAssured())
                .log(LogDetail.URI)
                .build();
    }

    public static RequestSpecification request() {
        return given().spec(spec());
    }

    public static RequestSpecification authorized(String accessToken) {
        return request().header("Authorization", "Bearer " + accessToken);
    }
}
