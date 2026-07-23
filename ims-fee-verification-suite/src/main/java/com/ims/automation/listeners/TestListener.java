package com.ims.automation.listeners;

import java.util.Arrays;

import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import com.aventstack.extentreports.Status;
import com.ims.automation.factory.DriverFactory;
import com.ims.automation.reports.ExtentManager;
import com.ims.automation.reports.ExtentTestManager;
import com.ims.automation.utilities.ScreenshotUtility;

public class TestListener implements ITestListener {

    @Override
    public void onStart(ITestContext context) {

        ExtentManager.getInstance();

    }

    @Override
    public void onTestStart(ITestResult result) {

        String testName = result.getMethod().getMethodName()
                + " " + Arrays.toString(result.getParameters());

        ExtentTestManager.setTest(

                ExtentManager.getInstance()

                        .createTest(testName)
                        .assignCategory("Fee Verification"));

    }

    @Override
    public void onTestSuccess(ITestResult result) {

        ExtentTestManager.getTest()

                .log(Status.PASS, "Fee matched expected value");

    }

    @Override
    public void onTestFailure(ITestResult result) {

        // Categorize failure
        if (result.getThrowable() instanceof AssertionError) {

            ExtentTestManager.getTest()
                    .assignCategory("Fee Discrepancy");

        } else {

            ExtentTestManager.getTest()
                    .assignCategory("Automation Defect");

        }

        // Log failure
        ExtentTestManager.getTest()

                .fail(result.getThrowable());

        // Capture screenshot safely
        try {

            if (DriverFactory.getDriver() != null) {

                ScreenshotUtility.Capture capture =
                        ScreenshotUtility.captureScreenshot(
                                result.getMethod().getMethodName());

                if (capture != null
                        && capture.getBase64() != null
                        && !capture.getBase64().isEmpty()) {

                    // Embedded as Base64 so the report is self-contained —
                    // it no longer depends on the screenshots/ folder being
                    // present next to it. capture.getPath() still lands on
                    // disk purely for the separate CI artifact/audit trail.
                    ExtentTestManager.getTest()
                            .addScreenCaptureFromBase64String(
                                    capture.getBase64(),
                                    "Failure Screenshot");

                }

            } else {

                ExtentTestManager.getTest()

                        .warning("WebDriver was not initialized. Screenshot not captured.");

            }

        } catch (Exception e) {

            ExtentTestManager.getTest()

                    .warning("Unable to capture screenshot : "
                            + e.getMessage());

        }

    }

    @Override
    public void onTestSkipped(ITestResult result) {

        ExtentTestManager.getTest()

                .log(Status.SKIP, "Test Skipped");

    }

    @Override
    public void onFinish(ITestContext context) {

        ExtentManager.flush();

        ExtentTestManager.unload();

    }

}
