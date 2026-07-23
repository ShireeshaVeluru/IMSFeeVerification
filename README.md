# IMS Program Fee Verification — Automation Suite

Selenium + TestNG suite that verifies the fee displayed on each IMS program
page matches the expected fee, across the entire program catalog.

## Why this is structured the way it is

Designing the framework that a team can scale and maintain. The single design decision
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

## Tech Stack

- Java 17
- Selenium WebDriver 4
- TestNG
- Maven
- Apache POI
- Extent Reports
- Log4j2
- GitHub Actions


## Project layout

```
ims-fee-verification-suite/
|
├── .github/
│   └── workflows/
│       └── feeVerification.yml          CI/CD pipeline (PR smoke run, nightly
│                                        full-catalog run, manual dispatch)
│
├── src/main/java/com/ims/automation/
|   |
│   ├── pages/
│   │   ├── ProgramPage.java            Page Object — shared by every program in the catalog
│   │   └── BatchDetails.java           POJO — fee, GST, start date, batch type for one card
│   │
│   ├── dataproviders/
│   │   └── FeeDataProvider.java        Reads FeeData.xlsx into TestNG's @DataProvider
│   │
│   ├── factory/
│   │   ├── BrowserFactory.java         Builds the WebDriver (Chrome/Edge/Firefox, headless option)
│   │   └── DriverFactory.java          ThreadLocal<WebDriver> holder (safe for parallel runs)
│   │
│   ├── utilities/
│   │   ├── ConfigReader.java           Loads config-{env}.properties via -Denv
│   │   ├── ExcelUtility.java           Generic POI helper for reading .xlsx test data
│   │   ├── ScreenshotUtility.java      Full-page screenshot (CDP) + URL banner stamped onto the image
│   │   └── LoggerUtility.java          Thin wrapper around Log4j2's LogManager
│   │
│   ├── listeners/
│   │   └── TestListener.java           TestNG ITestListener — drives Extent reporting, screenshot
│   │                                   capture, Fee Discrepancy/Automation Defect categorization,
│   │                                   and generates the combined report (see below)
│   │
│   ├── reports/
│   │   ├── ExtentManager.java          Thread-safe singleton for the shared ExtentReports instance
│   │   └── ExtentTestManager.java      ThreadLocal<ExtentTest> so parallel threads don't collide
│   │
│   └── constants/
│       └── FrameworkConstants.java     Path constants (config dir, report/screenshot/log paths)
│
├── src/main/resources/
│   └── log4j2.xml                      Console + file logging config
│
|── src/test/java/com/ims/automation/
|   |
│   ├── base/
│   │   └── BaseTest.java               Generic browser lifecycle only — no program/URL logic
│   ├── tests/
│   │   └── FeeVerificationTest.java    The one data-driven test; program/batch/fee all come
│   │                                   from FeeData.xlsx
|    
└── src/test/resources/
|
|   ├── config/
|   │   ├── config-qa.properties        Default environment
|   │   └── config-staging.properties   Second environment, selected via -Denv=staging
|   └── testData/
|       └── FeeData.xlsx                ProgramName | ProgramUrlPath | BatchName | ExpectedFee
|                                       — the entire catalog lives here, not in 
├── pom.xml
├── testng.xml                                         


```

### Generated at runtime (git-ignored — not checked in)

```
reports/
IMS_Fee_Verification_<timestamp>.html          Generated Extent report — pass/fail, categories, screenshots, logs
                                               (Program, Batch, Expected, Displayed),
screenshots/                                   Failure screenshots captured during execution
logs/
automation.log                                 Log4j2 output
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

## Extending to a new program

1. Open `testData/FeeData.xlsx`.
2. Add a row: `ProgramName | ProgramUrlPath | BatchName | ExpectedFee`.
3. Done — no Java changes.
