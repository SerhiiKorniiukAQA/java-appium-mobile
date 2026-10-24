package io.github.serhiikorniiuk.mobile.listeners;

import io.github.serhiikorniiuk.mobile.config.Config;
import io.github.serhiikorniiuk.mobile.utils.Evidence;
import io.qameta.allure.listener.StepLifecycleListener;
import io.qameta.allure.model.StepResult;

/**
 * Takes a screenshot at the end of every step, so the Allure report shows what
 * the screen looked like after each action and check (including grouped steps).
 * Registered via META-INF/services; switched off with -Dallure.step.screenshots=false.
 */
public class StepScreenshotListener implements StepLifecycleListener {

    @Override
    public void beforeStepStop(StepResult result) {
        boolean enabled = Boolean.parseBoolean(Config.optional("allure.step.screenshots").orElse("true"));
        if (enabled) {
            Evidence.attachScreenshot("Screen");
        }
    }
}
