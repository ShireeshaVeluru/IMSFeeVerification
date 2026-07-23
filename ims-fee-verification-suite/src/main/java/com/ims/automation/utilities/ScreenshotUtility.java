package com.ims.automation.utilities;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import com.ims.automation.constants.FrameworkConstants;
import com.ims.automation.factory.DriverFactory;

public final class ScreenshotUtility {

    private ScreenshotUtility() {
    }

    /**
     * Result of a single screenshot capture: the on-disk path (kept purely
     * for the CI "failure-screenshots" artifact / audit trail) and a
     * ready-to-use "data:image/png;base64,..." URI of the same bytes, used
     * to embed the image directly into the Extent HTML report. The report
     * no longer references an absolute filesystem path — that path only
     * existed on the machine that generated it, which is why screenshots
     * showed as broken images once the report was downloaded or opened
     * anywhere else. The data URI prefix is included here (not left to the
     * caller) because ExtentReports' base64 image viewer only renders
     * correctly with the full "data:image/...;base64," prefix present —
     * without it, the report's "base64 img" badge links to a raw blob the
     * browser can't interpret as an image and clicking it shows a blank
     * lightbox.
     */
    public static class Capture {

        private final String path;
        private final String base64;

        public Capture(String path, String base64) {
            this.path = path;
            this.base64 = base64;
        }

        public String getPath() {
            return path;
        }

        public String getBase64() {
            return base64;
        }

    }

    public static Capture captureScreenshot(String testName) {

        WebDriver driver = DriverFactory.getDriver();
        Dimension originalSize = driver.manage().window().getSize();

        byte[] screenshotBytes;

        try {

            // Resize the window to the page's actual full height/width so
            // the capture below shows everything top-to-bottom in one shot
            // (program name, banner, and the Select Batch section) instead
            // of only whatever happened to fit in the current viewport.
            JavascriptExecutor js = (JavascriptExecutor) driver;

            long pageHeight = (Long) js.executeScript(
                    "return Math.max("
                            + "document.body.scrollHeight, "
                            + "document.documentElement.scrollHeight);");

            long pageWidth = (Long) js.executeScript(
                    "return Math.max("
                            + "document.body.scrollWidth, "
                            + "document.documentElement.scrollWidth);");

            driver.manage().window().setSize(
                    new Dimension((int) pageWidth, (int) pageHeight));

            screenshotBytes =
                    ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);

        } finally {

            // Always restore the original window size, even if the resize
            // or capture above failed, so the browser isn't left in a
            // stretched state for whatever runs next.
            driver.manage().window().setSize(originalSize);

        }

        String timestamp = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));

        String fileName = testName + "_" + timestamp + ".png";

        String destination =
                FrameworkConstants.SCREENSHOT_PATH + fileName;

        try {

            FileUtils.writeByteArrayToFile(new File(destination), screenshotBytes);

        } catch (IOException e) {

            throw new RuntimeException(
                    "Unable to capture screenshot.", e);

        }

        String base64 =
                "data:image/png;base64,"
                        + Base64.getEncoder().encodeToString(screenshotBytes);

        return new Capture(destination, base64);

    }

}
