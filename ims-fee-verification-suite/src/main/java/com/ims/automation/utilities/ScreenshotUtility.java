package com.ims.automation.utilities;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

import com.ims.automation.constants.FrameworkConstants;
import com.ims.automation.factory.DriverFactory;

public final class ScreenshotUtility {

    private ScreenshotUtility() {
    }

    /**
     * Result of a single screenshot capture: the on-disk path (kept purely
     * for the CI "failure-screenshots" artifact / audit trail) and the
     * Base64 encoding of the same bytes, used to embed the image directly
     * into the Extent HTML report. The report no longer references an
     * absolute filesystem path — that path only exists on the machine that
     * generated it, which is why screenshots showed as broken images once
     * the report was downloaded or opened anywhere else.
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

        byte[] screenshotBytes =
                ((TakesScreenshot) DriverFactory.getDriver())
                        .getScreenshotAs(OutputType.BYTES);

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

        String base64 = Base64.getEncoder().encodeToString(screenshotBytes);

        return new Capture(destination, base64);

    }

}
