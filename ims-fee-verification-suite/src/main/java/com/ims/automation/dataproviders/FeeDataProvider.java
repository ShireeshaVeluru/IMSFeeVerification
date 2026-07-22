package com.ims.automation.dataproviders;

import org.testng.annotations.DataProvider;

import com.ims.automation.constants.FrameworkConstants;
import com.ims.automation.utilities.ExcelUtility;

public class FeeDataProvider {

    @DataProvider(name = "feeData")
    public Object[][] getFeeData() {

        return ExcelUtility.getSheetData(
                FrameworkConstants.EXCEL_PATH,
                "FeeData");

    }

}