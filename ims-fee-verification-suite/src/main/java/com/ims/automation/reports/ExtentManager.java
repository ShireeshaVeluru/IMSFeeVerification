package com.ims.automation.reports;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.ims.automation.utilities.ConfigReader;

public final class ExtentManager {

    private static ExtentReports extent;

    private ExtentManager() {
    }

    public static synchronized ExtentReports getInstance() {

        if (extent == null) {

            // Create reports folder if it doesn't exist
            File reportFolder = new File("reports");

            if (!reportFolder.exists()) {
                reportFolder.mkdirs();
            }

            // Timestamp
            String timestamp = LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));

            // Report file
            String reportPath = reportFolder.getAbsolutePath()
                    + File.separator
                    + "IMS_Fee_Verification_" + timestamp + ".html";

            ExtentSparkReporter spark = new ExtentSparkReporter(reportPath);

            spark.config().setDocumentTitle("IMS Automation Report");
            spark.config().setReportName("IMS Fee Verification");
            spark.config().setTimelineEnabled(true);

            extent = new ExtentReports();
            extent.attachReporter(spark);

            // System Information

            extent.setSystemInfo("Environment",
                    ConfigReader.getProperty("env"));

            extent.setSystemInfo("Browser",
                    ConfigReader.getProperty("browser"));

            extent.setSystemInfo("Tester",
                    ConfigReader.getProperty("testerName"));

            extent.setSystemInfo("Java Version",
                    System.getProperty("java.version"));

            extent.setSystemInfo("Operating System",
                    System.getProperty("os.name"));

            extent.setSystemInfo("Execution Time",
                    LocalDateTime.now().format(
                            DateTimeFormatter.ofPattern("dd-MMM-yyyy HH:mm:ss")));

        }

        return extent;

    }

    public static synchronized void flush() {

        if (extent != null) {
            extent.flush();
        }

    }

}