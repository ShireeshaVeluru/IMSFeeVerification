package com.ims.automation.constants;

public final class FrameworkConstants {

    private FrameworkConstants() {
    }

    public static final String CONFIG_DIR =
            System.getProperty("user.dir")
            + "/src/test/resources/config/";

    public static final String SCREENSHOT_PATH =
            System.getProperty("user.dir")
            + "/screenshots/";

    public static final String REPORT_PATH =
            System.getProperty("user.dir")
            + "/reports/";

    public static final String EXCEL_PATH =
            System.getProperty("user.dir")
            + "/src/test/resources/testData/FeeData.xlsx";

}
