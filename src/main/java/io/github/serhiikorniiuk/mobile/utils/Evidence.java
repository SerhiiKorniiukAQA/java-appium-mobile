package io.github.serhiikorniiuk.mobile.utils;

import io.github.serhiikorniiuk.mobile.driver.DriverManager;
import io.qameta.allure.Allure;
import org.openqa.selenium.OutputType;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Report evidence: screenshots, page source, video.
 * <p>
 * Screenshots are attached to every step, so they are resized and stored as JPEG
 * (~20-40 KB instead of ~500 KB PNG) to keep the published report small.
 */
public final class Evidence {

    private static final int SCREENSHOT_WIDTH = 360;
    private static final float JPEG_QUALITY = 0.6f;

    private Evidence() {
    }

    /** Attaches a compressed screenshot to the current Allure step (or test if no step is running). */
    public static void attachScreenshot(String name) {
        if (!DriverManager.isStarted()) {
            return;
        }
        try {
            byte[] png = DriverManager.driver().getScreenshotAs(OutputType.BYTES);
            Allure.getLifecycle().addAttachment(name, "image/jpeg", "jpg", toSmallJpeg(png));
        } catch (RuntimeException | IOException e) {
            // evidence must never break a test
        }
    }

    /** Full-size PNG: used once per failed test, where details matter. */
    public static void attachFullScreenshot(String name) {
        if (!DriverManager.isStarted()) {
            return;
        }
        try {
            byte[] png = DriverManager.driver().getScreenshotAs(OutputType.BYTES);
            Allure.addAttachment(name, "image/png", new ByteArrayInputStream(png), "png");
        } catch (RuntimeException e) {
            Allure.addAttachment(name + " error", e.toString());
        }
    }

    public static void attachPageSource() {
        if (!DriverManager.isStarted()) {
            return;
        }
        try {
            String source = DriverManager.driver().getPageSource();
            Allure.addAttachment("Page source", "text/xml",
                    new ByteArrayInputStream(source.getBytes(StandardCharsets.UTF_8)), "xml");
        } catch (RuntimeException e) {
            Allure.addAttachment("Page source error", e.toString());
        }
    }

    public static void attachVideo(String name, byte[] mp4) {
        Allure.addAttachment(name, "video/mp4", new ByteArrayInputStream(mp4), "mp4");
    }

    static byte[] toSmallJpeg(byte[] png) throws IOException {
        BufferedImage source = ImageIO.read(new ByteArrayInputStream(png));
        if (source == null) {
            return png;
        }
        int width = Math.min(SCREENSHOT_WIDTH, source.getWidth());
        int height = (int) Math.round(source.getHeight() * (width / (double) source.getWidth()));

        BufferedImage scaled = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = scaled.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.drawImage(source, 0, 0, width, height, null);
        g.dispose();

        ImageWriter writer = ImageIO.getImageWritersByFormatName("jpg").next();
        ImageWriteParam params = writer.getDefaultWriteParam();
        params.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
        params.setCompressionQuality(JPEG_QUALITY);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (ImageOutputStream ios = ImageIO.createImageOutputStream(out)) {
            writer.setOutput(ios);
            writer.write(null, new IIOImage(scaled, null, null), params);
        } finally {
            writer.dispose();
        }
        return out.toByteArray();
    }
}
