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

    // Defaults to "qa" when -Denv is not supplied, e.g.:
    //   mvn test                  -> config-qa.properties
    //   mvn test -Denv=staging    -> config-staging.properties
    //   mvn test -Denv=prod       -> config-prod.properties
    private static final String ENV =
            System.getProperty("env", "qa").trim().toLowerCase();

    static {

        String path = FrameworkConstants.CONFIG_DIR + "config-" + ENV + ".properties";

        try (FileInputStream fis = new FileInputStream(path)) {
            properties.load(fis);
        } catch (IOException e) {
            throw new RuntimeException(
                    "Unable to load config file for environment '" + ENV
                            + "' at path: " + path, e);
        }

    }

    private ConfigReader() {
    }

    public static String getProperty(String key) {
        return properties.getProperty(key);
    }

    /**
     * Returns the environment the suite is actually running against
     * (resolved from -Denv, defaulting to "qa") — not a value read out of
     * the properties file itself, so it can't drift if a config-*.properties
     * file forgets to declare its own env= key.
     */
    public static String getEnvironment() {
        return ENV;
    }

}
