package com.ims.automation.pages;

import java.time.Duration;
import java.util.List;

import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.ims.automation.factory.DriverFactory;
import com.ims.automation.utilities.ConfigReader;
import com.ims.automation.utilities.LoggerUtility;

/**
 * Page Object for any IMS "program details" page (SimCAT Core, SimCAT Max,
 * SimCAT Plus, etc). All programs share the same DOM structure for the
 * "Select Batch" section, so ONE page object serves the entire catalog —
 * the specific program is just a URL, supplied as test data.
 */
public class ProgramPage {

    private static final Logger log =
            LoggerUtility.getLogger(ProgramPage.class);

    private final WebDriverWait wait;

    // All Batch Cards
    private final By batchCards =
            By.cssSelector("div.col-sm-4.row.form-check.mb-3.d-flex");

    // Elements inside each card
    private final By batchName =
            By.cssSelector("label.form-check-label");

    private final By batchFee =
            By.cssSelector("h4.batch-total-price");

    private final By batchGST =
            By.cssSelector("p.batch-price-details span");

    private final By batchInfo =
            By.cssSelector("p.check_text");

    public ProgramPage() {

        wait = new WebDriverWait(
                DriverFactory.getDriver(),
                Duration.ofSeconds(
                        Long.parseLong(
                                ConfigReader.getProperty("explicitWait"))));

    }

    /**
     * Navigates to the given program URL and returns a ready-to-use
     * ProgramPage. Centralizes navigation so tests never call
     * DriverFactory.getDriver().get(...) directly.
     *
     * @param programUrl fully-qualified program page URL
     * @return ProgramPage
     */
    public static ProgramPage open(String programUrl) {

        log.info("Opening Program Page : {}", programUrl);

        DriverFactory.getDriver().get(programUrl);

        return new ProgramPage();

    }

    /**
     * Returns complete Batch Details for the given Batch Name.
     *
     * @param expectedBatchName batch name as shown on the page
     * @return BatchDetails
     */
    public BatchDetails getBatchDetails(String expectedBatchName) {

        wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(batchCards));

        List<WebElement> cards =
                DriverFactory.getDriver().findElements(batchCards);

        log.info("Total Batches Found : {}", cards.size());

        for (WebElement card : cards) {

            String actualBatchName =
                    card.findElement(batchName)
                            .getText()
                            .trim();

            log.info("Checking Batch : {}", actualBatchName);

            if (actualBatchName.equalsIgnoreCase(expectedBatchName)) {

                BatchDetails details = new BatchDetails();

                details.setBatchName(actualBatchName);

                // Clean Fee — strip currency symbol, thousands separators,
                // and the trailing "/-" suffix used across the IMS catalog.
                String fee =
                        card.findElement(batchFee)
                                .getText()
                                .replace("₹", "")
                                .replace(",", "")
                                .replace("/-", "")
                                .trim();

                details.setFee(fee);

                // GST — regex tolerates spacing/wording variance
                // (e.g. "+ GST (18%):" vs "+ GST(18%):") instead of
                // matching one exact label string.
                String gst =
                        card.findElement(batchGST)
                                .getText()
                                .replaceAll("(?i)\\+?\\s*GST\\s*\\(\\d+%\\)\\s*:?", "")
                                .trim();

                details.setGst(gst);

                // Batch Info — split on newline; tolerant of the two lines
                // (Start Date, Batch Type) arriving in a fixed order today.
                String info =
                        card.findElement(batchInfo)
                                .getText();

                String[] values = info.split("\n");

                if (values.length >= 2) {

                    details.setStartDate(
                            values[0]
                                    .replace("Batch Start Date:", "")
                                    .trim());

                    details.setBatchType(
                            values[1]
                                    .replace("Batch Type:", "")
                                    .trim());

                }

                log.info("Batch Details : {}", details);

                return details;

            }

        }

        throw new RuntimeException(
                "Batch not found : " + expectedBatchName);

    }

}
