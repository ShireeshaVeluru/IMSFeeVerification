package com.ims.automation.base;

import java.time.Duration;

import org.apache.logging.log4j.Logger;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import com.ims.automation.factory.BrowserFactory;
import com.ims.automation.factory.DriverFactory;
import com.ims.automation.utilities.ConfigReader;
import com.ims.automation.utilities.LoggerUtility;

/**
 * Generic browser lifecycle for every test class. Deliberately contains
 * NO program/URL logic — navigation is test data (see FeeData.xlsx and
 * ProgramPage.open()), not framework code. This keeps BaseTest reusable
 * for any future IMS test suite, not just fee verification.
 */
public class BaseTest {

    private static final Logger log =
            LoggerUtility.getLogger(BaseTest.class);

    @BeforeMethod(alwaysRun = true)
    public void setUp() {

        log.info("Launching Browser");

        DriverFactory.setDriver(BrowserFactory.createDriver());

        log.info("Browser Launched Successfully");

        DriverFactory.getDriver().manage().window().maximize();

        DriverFactory.getDriver().manage().timeouts()
                .implicitlyWait(Duration.ofSeconds(
                        Long.parseLong(ConfigReader.getProperty("implicitWait"))));

        DriverFactory.getDriver().manage().timeouts()
                .pageLoadTimeout(Duration.ofSeconds(
                        Long.parseLong(ConfigReader.getProperty("pageLoadTimeout"))));

    }

    @AfterMethod(alwaysRun = true)
    public void tearDown() {

        DriverFactory.unload();

        log.info("Closing Browser");

    }

}
