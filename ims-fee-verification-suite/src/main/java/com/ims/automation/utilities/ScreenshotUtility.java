package com.ims.automation.utilities;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

import com.ims.automation.constants.FrameworkConstants;
import com.ims.automation.factory.DriverFactory;

public final class ScreenshotUtility {

    private ScreenshotUtility() {
    }

    public static String captureScreenshot(String testName) {

        String timestamp = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));

        String fileName = testName + "_" + timestamp + ".png";

        String destination =
                FrameworkConstants.SCREENSHOT_PATH + fileName;

        File source = ((TakesScreenshot) DriverFactory.getDriver())
                .getScreenshotAs(OutputType.FILE);

        try {

            FileUtils.copyFile(source, new File(destination));

        } catch (IOException e) {

            throw new RuntimeException(
                    "Unable to capture screenshot.", e);

        }

        return destination;

    }

}