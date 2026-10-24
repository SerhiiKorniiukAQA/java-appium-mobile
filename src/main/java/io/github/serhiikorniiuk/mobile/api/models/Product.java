package io.github.serhiikorniiuk.mobile.api.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Product(
        int id,
        String title,
        String description,
        String category,
        double price,
        double rating,
        int stock,
        String brand) {
}
