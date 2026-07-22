package com.ims.automation.factory;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

import com.ims.automation.utilities.ConfigReader;

import io.github.bonigarcia.wdm.WebDriverManager;

public final class BrowserFactory {

    private BrowserFactory() {
    }

    public static WebDriver createDriver() {

        String browser = ConfigReader.getProperty("browser").toLowerCase();
        boolean headless = Boolean.parseBoolean(ConfigReader.getProperty("headless"));

        switch (browser) {

        case "chrome":

            WebDriverManager.chromedriver().setup();

            ChromeOptions chromeOptions = new ChromeOptions();

            if (headless) {
                chromeOptions.addArguments("--headless=new");
            }

            chromeOptions.addArguments("--start-maximized");

            return new ChromeDriver(chromeOptions);

        case "edge":

            WebDriverManager.edgedriver().setup();

            return new EdgeDriver();

        case "firefox":

            WebDriverManager.firefoxdriver().setup();

            return new FirefoxDriver();

        default:

            throw new IllegalArgumentException(
                    "Unsupported browser : " + browser);

        }

    }

}