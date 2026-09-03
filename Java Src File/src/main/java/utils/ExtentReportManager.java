package utils;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Owns a single ExtentReports instance for the whole test run and hands out
 * a per-test ExtentTest node. TestListener drives this class; test classes
 * never touch reporting directly.
 */
public class ExtentReportManager {

    private static ExtentReports extent;
    private static final ThreadLocal<ExtentTest> testThreadLocal = new ThreadLocal<>();

    private ExtentReportManager() {
        // utility class
    }

    public static ExtentReports getInstance() {
        if (extent == null) {
            String reportDir = ConfigReader.get("report.dir", "test-output/ExtentReport");
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String reportPath = reportDir + "/AutomationReport_" + timestamp + ".html";

            ExtentSparkReporter sparkReporter = new ExtentSparkReporter(reportPath);
            sparkReporter.config().setTheme(Theme.STANDARD);
            sparkReporter.config().setDocumentTitle("Selenium POM Framework - Test Report");
            sparkReporter.config().setReportName("SauceDemo UI Automation Results");

            extent = new ExtentReports();
            extent.attachReporter(sparkReporter);
            extent.setSystemInfo("Browser", ConfigReader.get("browser", "chrome"));
            extent.setSystemInfo("Base URL", ConfigReader.get("base.url"));
            extent.setSystemInfo("Environment", "QA");
        }
        return extent;
    }

    public static void createTest(String testName, String description) {
        ExtentTest test = getInstance().createTest(testName, description);
        testThreadLocal.set(test);
    }

    public static ExtentTest getTest() {
        return testThreadLocal.get();
    }

    public static void flush() {
        if (extent != null) {
            extent.flush();
        }
    }
}
