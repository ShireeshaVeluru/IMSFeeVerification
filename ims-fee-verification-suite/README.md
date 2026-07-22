# IMS Program Fee Verification — Automation Suite

Selenium + TestNG suite that verifies the fee displayed on each IMS program
page matches the expected fee, across the entire program catalog.

## Why this is structured the way it is

The brief for a "Lead Software Tester" isn't just "make it pass" — it's
"design something a team can scale and maintain." The single design decision
that drives everything else in this project:

> **The program URL, batch name, and expected fee are all test data (an Excel
> row), never code.**

Everything below follows from that.

## Architectural patterns used

| Pattern | Where | Why |
|---|---|---|
| **Page Object Model (POM)** | `pages/ProgramPage.java` | One page object serves every program in the catalog, because SimCAT Core, SimCAT Max, SimCAT Plus, etc. all render the identical "Select Batch" DOM structure. Locators live in exactly one place. |
| **Data-Driven Testing** | `dataproviders/FeeDataProvider.java`, `testData/FeeData.xlsx` | `ProgramName`, `ProgramUrlPath`, `BatchName`, `ExpectedFee` all come from Excel. Onboarding a new program or batch is a spreadsheet edit — zero code changes, zero redeploy. |
| **Factory Pattern** | `factory/BrowserFactory.java`, `factory/DriverFactory.java` | Decouples test logic from `WebDriver` construction; swapping Chrome→Firefox or adding grid/cloud execution touches one class. `DriverFactory` uses `ThreadLocal<WebDriver>` so parallel execution (see `testng.xml`) doesn't share drivers across threads. |
| **Environment Abstraction** | `utilities/ConfigReader.java`, `config/config-{env}.properties` | `-Denv=qa\|staging\|prod` selects the properties file at startup. No environment-specific values are ever hardcoded in Java. |
| **Listener / Observer** | `listeners/TestListener.java` | TestNG's `ITestListener` hooks into every test lifecycle event to drive reporting and screenshot capture without polluting test methods with reporting calls. |
| **POJO / Value Object** | `pages/BatchDetails.java` | Carries fee, GST, start date, and batch type scraped from one card in a single pass — cheap to keep even though only `fee` is asserted on today, since GST/date verification is a one-line addition later. |

## Why NOT a Program/URL enum

An earlier iteration of this suite mapped program names to a Java `enum`
with a `switch` statement choosing the URL. That's fine for 2 programs; it
does not scale to "the entire program catalog" as the brief requires —
every new program would mean an enum constant, a switch case, and a
redeploy. Moving the URL into the data source removes that entirely.

## Project layout

```
src/main/java/com/ims/automation/
  pages/          ProgramPage, BatchDetails         (POM)
  tests/          FeeVerificationTest
  dataproviders/  FeeDataProvider                    (reads FeeData.xlsx)
  base/           BaseTest                           (generic browser lifecycle only)
  factory/        BrowserFactory, DriverFactory
  utilities/      ConfigReader, ExcelUtility, ScreenshotUtility, LoggerUtility
  listeners/      TestListener                       (Extent reporting + screenshots)
  reports/        ExtentManager, ExtentTestManager
  constants/      FrameworkConstants
src/test/resources/
  config/         config-qa.properties, config-staging.properties
  testData/       FeeData.xlsx
testng.xml
pom.xml
.github/workflows/fee-verification.yml
```

## Running the suite

```bash
# Default (QA) environment
mvn clean test

# Specific environment
mvn clean test -Denv=staging

# Directly via TestNG
mvn clean test -DsuiteXmlFile=testng.xml
```

Report: `reports/FeeVerificationReport.html`
Screenshots (failures only): `screenshots/`
Logs: `logs/automation.log`

## Extending to a new program

1. Open `testData/FeeData.xlsx`.
2. Add a row: `ProgramName | ProgramUrlPath | BatchName | ExpectedFee`.
3. Done — no Java changes.

## Known limitations / next steps

See the **Test Strategy Document** for the full discussion of validation
approach (UI vs API), release governance, and reporting strategy. In short:

- This suite is UI-only by design for now (see strategy doc for rationale
  and where an API layer would slot in).
- GST, start date, and batch type are captured by `ProgramPage` but not yet
  asserted — trivial to add via `BatchDetails` once requirements call for it.
- Locator/DOM recommendations for the IMS dev team are in the strategy doc's
  "Technical Improvements" section.
