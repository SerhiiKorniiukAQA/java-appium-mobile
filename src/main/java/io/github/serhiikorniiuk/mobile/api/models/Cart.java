package io.github.serhiikorniiuk.mobile.api.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Cart(
        int id,
        int userId,
        List<Line> products,
        double total,
        int totalProducts,
        int totalQuantity) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Line(int id, String title, double price, int quantity, double total) {
    }
}
