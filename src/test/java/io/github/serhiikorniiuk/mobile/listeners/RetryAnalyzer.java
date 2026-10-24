package io.github.serhiikorniiuk.mobile.listeners;

import io.github.serhiikorniiuk.mobile.config.Config;
import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

/**
 * Retries a failed test {@code retry.count} times (0 locally, 1 on CI).
 * Emulators/simulators on shared CI runners are slower and less stable than
 * real devices; a retry hides infrastructure noise, while Allure still shows
 * every attempt, so real flakiness stays visible.
 */
public class RetryAnalyzer implements IRetryAnalyzer {

    private int attempt = 0;

    @Override
    public boolean retry(ITestResult result) {
        if (attempt < Config.retryCount()) {
            attempt++;
            return true;
        }
        return false;
    }
}
