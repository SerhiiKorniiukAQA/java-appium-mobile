package io.github.serhiikorniiuk.mobile.pages;

import org.openqa.selenium.By;

public class GeoLocationPage extends BasePage {

    private static final By SCREEN = id("geo location screen");
    private static final By LATITUDE = id("latitude data");
    private static final By LONGITUDE = id("longitude data");

    @Override
    protected By screenLocator() {
        return SCREEN;
    }

    public String latitude() {
        return textOf(LATITUDE);
    }

    public String longitude() {
        return textOf(LONGITUDE);
    }
}
