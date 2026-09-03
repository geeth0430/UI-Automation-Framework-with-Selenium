package listeners;

import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.Status;
import org.openqa.selenium.WebDriver;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;
import utils.DriverFactory;
import utils.ExtentReportManager;
import utils.ScreenshotUtils;

/**
 * Wires TestNG's lifecycle events into ExtentReports and takes an automatic
 * screenshot on any test failure, which is then embedded in the HTML report.
 *
 * Registered globally via testng.xml <listeners> so no test class needs to
 * reference it directly.
 */
public class TestListener implements ITestListener {

    @Override
    public void onStart(ITestContext context) {
        ExtentReportManager.getInstance();
    }

    @Override
    public void onTestStart(ITestResult result) {
        ExtentReportManager.createTest(
                result.getMethod().getMethodName(),
                result.getMethod().getDescription());
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        ExtentReportManager.getTest().log(Status.PASS, "Test passed.");
    }

    @Override
    public void onTestFailure(ITestResult result) {
        String testName = result.getMethod().getMethodName();
        WebDriver driver = DriverFactory.getDriver();

        ExtentReportManager.getTest().log(Status.FAIL, "Test failed: " + result.getThrowable());

        if (driver != null) {
            String screenshotPath = ScreenshotUtils.capture(driver, testName);
            if (screenshotPath != null) {
                try {
                    ExtentReportManager.getTest().fail(
                            "Screenshot on failure:",
                            MediaEntityBuilder.createScreenCaptureFromPath(screenshotPath).build());
                } catch (Exception e) {
                    ExtentReportManager.getTest().log(Status.WARNING,
                            "Could not attach screenshot: " + e.getMessage());
                }
            }
        }
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        ExtentReportManager.getTest().log(Status.SKIP, "Test skipped: " + result.getThrowable());
    }

    @Override
    public void onFinish(ITestContext context) {
        ExtentReportManager.flush();
    }
}
