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

                // --start-maximized below has no effect in headless mode —
                // there's no real screen to maximize against, so headless
                // Chrome silently falls back to a small default viewport
                // (often 800x600). Set an explicit desktop-sized viewport
                // so screenshots look like a real browser window instead
                // of a cramped one.
                chromeOptions.addArguments("--window-size=1920,1080");

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
