package io.github.serhiikorniiuk.mobile.api.models;

import java.util.List;

public record CartRequest(int userId, List<Item> products) {

    public record Item(int id, int quantity) {
    }
}
