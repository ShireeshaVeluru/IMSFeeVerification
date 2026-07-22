package com.ims.automation.utilities;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import com.ims.automation.constants.FrameworkConstants;

/**
 * Loads config-{env}.properties, where {env} defaults to "qa" and can be
 * overridden per run without touching any code, e.g.:
 *
 *   mvn test -Denv=staging
 *   mvn test -Denv=prod
 *
 * This is the single point where environment (base URL, browser, timeouts)
 * is resolved, keeping test/page-object code environment-agnostic.
 */
public final class ConfigReader {

    private static final Properties properties = new Properties();

    static {
    	
        		
        String path = FrameworkConstants.CONFIG_DIR + "config-qa.properties";

        try (FileInputStream fis = new FileInputStream(path)) {
            properties.load(fis);
        } catch (IOException e) {
            throw new RuntimeException(
                    "Unable to load config file for environment: " + path, e);
        }

    }

    private ConfigReader() {
    }

    public static String getProperty(String key) {
        return properties.getProperty(key);
    }

}
