# Selenium POM Framework — SauceDemo UI Automation

A Java + Selenium WebDriver + TestNG automation framework built around the
**Page Object Model (POM)**, with ExtentReports HTML reporting, cross-browser
execution (Chrome + Firefox), and CI integration via GitHub Actions.

Target application under test: **https://www.saucedemo.com**

---

## Architecture Diagram

```
┌─────────────────────────────────────────────────────────────────────┐
│                         testng.xml (Test Runner)                     │
│   registers listeners.TestListener, defines <test> suites per class  │
└───────────────────────────────┬───────────────────────────────────---┘
                                 │ runs
                                 ▼
┌─────────────────────────────────────────────────────────────────────┐
│                    src/test/java/tests/*  (Test Layer)               │
│  LoginTest · InventoryTest · CartTest · CheckoutTest · LogoutTest     │
│  - Contains ALL Assert.* calls                                       │
│  - Calls Page Object methods only — never touches By/WebElement      │
└───────────┬─────────────────────────────────────────────┬───────────┘
            │ extends                                      │ uses
            ▼                                               ▼
┌───────────────────────────┐              ┌────────────────────────────┐
│  src/test/java/base/       │              │  src/main/java/pages/      │
│  BaseTest                  │              │  (Page Object Model)       │
│  - @BeforeMethod: init      │──creates────▶│  BasePage (abstract)       │
│    driver, open LoginPage   │   page       │   ├─ LoginPage             │
│  - @AfterMethod: quit       │   objects    │   ├─ InventoryPage         │
│    driver                   │              │   ├─ CartPage              │
│                             │              │   └─ CheckoutPage          │
└──────────────┬──────────────┘              │  Locators + action methods │
               │                              │  ONLY — no assertions      │
               │ uses                         └──────────────┬─────────────┘
               ▼                                              │ uses
┌─────────────────────────────────────────────────────────────▼──────────┐
│                     src/main/java/utils/  (Support Layer)               │
│  DriverFactory   → creates Chrome/Firefox WebDriver via WebDriverManager │
│  ConfigReader    → reads config.properties (system prop / env override) │
│  WaitUtils       → WebDriverWait wrappers — no Thread.sleep() anywhere   │
│  ScreenshotUtils → captures PNG on failure                              │
│  ExtentReportManager → owns the ExtentReports instance + per-test node  │
└───────────────────────────┬───────────────────────────────────────────-┘
                             │ read by
                             ▼
┌───────────────────────────────────────────────────────────────────────┐
│           src/main/java/listeners/TestListener (ITestListener)          │
│  onTestStart  → creates an ExtentTest node                              │
│  onTestSuccess→ logs PASS                                                │
│  onTestFailure→ logs FAIL + captures screenshot + embeds it in report    │
│  onFinish     → flushes ExtentReports HTML to test-output/ExtentReport/  │
└───────────────────────────────────────────────────────────────────────┘
```

**Data flow for one test run:**
`testng.xml` → TestNG engine loads `TestListener` → for each `@Test`,
`BaseTest.setUp()` builds a `WebDriver` via `DriverFactory` (browser +
headless flag pulled from `ConfigReader`/`config.properties`) → the test
calls Page Object methods (`LoginPage`, `InventoryPage`, `CartPage`,
`CheckoutPage`) which use `WaitUtils` for explicit waits → the test asserts
on data returned by the page objects → `TestListener` logs the result to
`ExtentReportManager` (with a screenshot from `ScreenshotUtils` on failure)
→ `BaseTest.tearDown()` quits the driver → after the whole suite,
`ExtentReportManager.flush()` writes the final HTML report.

---

## Project Structure

```
selenium-pom-framework/
├── pom.xml
├── src/
│   ├── main/java/
│   │   ├── pages/          # Page Object classes (locators + methods, no asserts)
│   │   │   ├── BasePage.java
│   │   │   ├── LoginPage.java
│   │   │   ├── InventoryPage.java
│   │   │   ├── CartPage.java
│   │   │   └── CheckoutPage.java
│   │   ├── utils/          # WebDriver setup, waits, config reader, reporting
│   │   │   ├── DriverFactory.java
│   │   │   ├── ConfigReader.java
│   │   │   ├── WaitUtils.java
│   │   │   ├── ScreenshotUtils.java
│   │   │   └── ExtentReportManager.java
│   │   ├── listeners/
│   │   │   └── TestListener.java   # TestNG ITestListener → ExtentReports + screenshots
│   │   └── resources/
│   │       └── config.properties   # base URL, browser, credentials — no hardcoding
│   └── test/java/
│       ├── base/
│       │   └── BaseTest.java       # @BeforeMethod / @AfterMethod driver lifecycle
│       ├── tests/
│       │   ├── LoginTest.java
│       │   ├── InventoryTest.java
│       │   ├── CartTest.java
│       │   ├── CheckoutTest.java
│       │   └── LogoutTest.java
│       └── resources/
│           └── testng.xml
├── .github/workflows/
│   └── selenium-ci.yml             # headless Chrome + Firefox matrix, on every push
├── sample-report/
│   └── sample-extent-report.html   # labeled sample of the report layout
└── README.md
```

---

## Design Decisions

- **Page Object Model, strictly separated from assertions.** Every page
  object under `src/main/java/pages` exposes only locators and action/state
  methods (`click`, `type`, `getText`, `loginAs`, …). All `Assert.*` calls
  live in `src/test/java/tests`. This means page objects are reusable across
  as many tests as needed without any test-specific logic leaking into them.
- **No `Thread.sleep()`.** `WaitUtils` wraps `WebDriverWait` with
  `ExpectedConditions` (`visibilityOfElementLocated`, `elementToBeClickable`,
  `urlContains`, etc.) and every page object action goes through it.
- **Zero hardcoded values.** Base URL, browser choice, headless flag, and
  test credentials all come from `config.properties`, which `ConfigReader`
  loads and overrides with `-D` system properties or environment variables —
  the same code runs locally and in CI without edits.
- **Cross-browser via `WebDriverManager`.** `DriverFactory` resolves the
  correct driver binary for Chrome or Firefox automatically (no manual
  driver downloads/paths to maintain) and is `ThreadLocal`-based so it's
  safe if parallel execution is enabled later.
- **Reporting is listener-driven, not test-driven.** `TestListener`
  (registered once in `testng.xml`) creates the ExtentReports node, logs
  pass/fail, and attaches a screenshot on failure — individual test methods
  never call reporting code directly.

---

## Running Locally

```bash
# Chrome, visible browser
mvn clean test

# Firefox, headless (e.g. for CI-like runs)
mvn clean test -Dbrowser=firefox -Dheadless=true
```

The HTML report is written to `test-output/ExtentReport/AutomationReport_<timestamp>.html`.
Failure screenshots are written to `screenshots/`.
A labeled example of the report's layout is included at
`sample-report/sample-extent-report.html` (this repo cannot launch a real
browser to generate a live run, so that file is a hand-built mock of the
actual output format — the real report is generated automatically the
first time you run the suite).

## Continuous Integration

`.github/workflows/selenium-ci.yml` runs on every push/PR to `main`:
- Matrix build across **Chrome** and **Firefox**, both headless.
- Uploads the ExtentReports HTML, failure screenshots, and Surefire XML
  results as workflow artifacts on every run (pass or fail).

## Test Coverage

| Test Class      | Scenarios                                                        |
|------------------|-------------------------------------------------------------------|
| `LoginTest`      | Valid login, invalid credentials, locked-out user                 |
| `InventoryTest`  | Add to cart, remove from cart, sort by price, sort by name         |
| `CartTest`       | Cart contents match added items, remove from cart, continue shopping |
| `CheckoutTest`   | Full end-to-end checkout (info → overview → confirmation), required-field validation |
| `LogoutTest`     | Logout returns user to the login page                             |

## Possible Extensions

- Switch `testng.xml` to `parallel="methods"` (driver layer already supports it via `ThreadLocal`).
- Add Allure as an alternative reporter alongside ExtentReports.
- Add a Dockerfile + Selenium Grid for containerized cross-browser runs.
