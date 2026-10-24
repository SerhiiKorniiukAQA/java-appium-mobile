package io.github.serhiikorniiuk.mobile.api.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ProductsPage(List<Product> products, int total, int skip, int limit) {
}
