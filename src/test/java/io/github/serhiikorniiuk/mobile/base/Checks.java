package io.github.serhiikorniiuk.mobile.base;

import io.qameta.allure.Allure;

/**
 * Wraps an assertion into a named Allure step, so the report shows
 * WHAT was verified (plus a screenshot of the screen at that moment),
 * not only the actions that led there.
 */
public final class Checks {

    private Checks() {
    }

    public static void verify(String description, Allure.ThrowableRunnableVoid assertion) {
        Allure.step("Verify: " + description, assertion);
    }
}
