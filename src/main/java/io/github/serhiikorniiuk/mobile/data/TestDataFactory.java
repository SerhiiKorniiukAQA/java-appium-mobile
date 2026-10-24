package io.github.serhiikorniiuk.mobile.data;

import net.datafaker.Faker;

import java.util.Locale;

/** Generates unique, realistic test data for every run. */
public final class TestDataFactory {

    private static final Faker FAKER = new Faker(Locale.ENGLISH);

    private TestDataFactory() {
    }

    public static Address address() {
        return new Address(
                FAKER.name().fullName(),
                FAKER.address().streetAddress(),
                FAKER.address().city(),
                FAKER.address().state(),
                FAKER.number().digits(5),
                "Ukraine");
    }

    public static PaymentCard card(String holder) {
        // Demo app validates format only; this is a standard test card number
        return new PaymentCard(holder, "4111111111111111", "12/30", FAKER.number().digits(3));
    }
}
