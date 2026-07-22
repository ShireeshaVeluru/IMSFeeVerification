package com.ims.automation.tests;

import org.apache.logging.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.ims.automation.base.BaseTest;
import com.ims.automation.dataproviders.FeeDataProvider;
import com.ims.automation.pages.BatchDetails;
import com.ims.automation.pages.ProgramPage;
import com.ims.automation.utilities.ConfigReader;
import com.ims.automation.utilities.LoggerUtility;

/**
 * Verifies displayed batch fee against expected fee for every
 * (Program, Batch) pair defined in FeeData.xlsx. Covers the entire
 * program catalog: adding a new program or batch is a spreadsheet
 * edit, not a code change.
 */
public class FeeVerificationTest extends BaseTest {

    private static final Logger log =
            LoggerUtility.getLogger(FeeVerificationTest.class);

    @Test(dataProvider = "feeData",
          dataProviderClass = FeeDataProvider.class,
          description = "Verify displayed fee matches expected fee for a Program/Batch")
    public void verifyFee(String programName,
                          String programUrlPath,
                          String batchName,
                          String expectedFee) {

        String programUrl = ConfigReader.getProperty("baseUrl") + programUrlPath;

        log.info("Verifying Fee | Program : {} | Batch : {}", programName, batchName);

        BatchDetails batchDetails =
                ProgramPage.open(programUrl)
                        .getBatchDetails(batchName);

        Assert.assertEquals(
                batchDetails.getFee(),
                expectedFee,
                "Fee mismatch | Program: " + programName
                        + " | Batch: " + batchName
                        + " | Displayed: " + batchDetails.getFee()
                        + " | Expected: " + expectedFee);

    }

}
