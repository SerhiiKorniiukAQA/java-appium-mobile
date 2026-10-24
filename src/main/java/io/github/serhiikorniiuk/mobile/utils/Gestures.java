package io.github.serhiikorniiuk.mobile.utils;

import io.appium.java_client.AppiumDriver;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.interactions.PointerInput;
import org.openqa.selenium.interactions.Sequence;

import java.time.Duration;
import java.util.List;

/** Cross-platform W3C gestures (work the same on UiAutomator2 and XCUITest). */
public final class Gestures {

    private Gestures() {
    }

    public enum Direction { UP, DOWN }

    /** Swipe the screen content. {@code UP} scrolls the list down (finger moves up). */
    public static void swipe(AppiumDriver driver, Direction direction) {
        Dimension size = driver.manage().window().getSize();
        int x = size.getWidth() / 2;
        int top = (int) (size.getHeight() * 0.30);
        int bottom = (int) (size.getHeight() * 0.70);

        Point from = direction == Direction.UP ? new Point(x, bottom) : new Point(x, top);
        Point to = direction == Direction.UP ? new Point(x, top) : new Point(x, bottom);
        drag(driver, from, to, Duration.ofMillis(600));
    }

    public static void drag(AppiumDriver driver, Point from, Point to, Duration duration) {
        PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
        Sequence swipe = new Sequence(finger, 1)
                .addAction(finger.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), from.getX(), from.getY()))
                .addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()))
                .addAction(finger.createPointerMove(duration, PointerInput.Origin.viewport(), to.getX(), to.getY()))
                .addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
        driver.perform(List.of(swipe));
    }
}
