package utils;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Captures a screenshot on demand (used by the TestNG listener on failure)
 * and returns the path so it can be embedded into the ExtentReports HTML.
 */
public class ScreenshotUtils {

    private ScreenshotUtils() {
        // utility class
    }

    public static String capture(WebDriver driver, String testName) {
        try {
            String dir = ConfigReader.get("screenshot.dir", "screenshots");
            Files.createDirectories(Paths.get(dir));

            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String fileName = testName + "_" + timestamp + ".png";
            Path destination = Paths.get(dir, fileName);

            File srcFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            Files.copy(srcFile.toPath(), destination);

            return destination.toString();
        } catch (IOException e) {
            System.err.println("Failed to capture screenshot for " + testName + ": " + e.getMessage());
            return null;
        }
    }
}
