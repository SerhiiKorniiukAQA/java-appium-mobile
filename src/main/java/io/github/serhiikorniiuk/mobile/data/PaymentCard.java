package io.github.serhiikorniiuk.mobile.data;

public record PaymentCard(String holder, String number, String expiration, String securityCode) {
}
