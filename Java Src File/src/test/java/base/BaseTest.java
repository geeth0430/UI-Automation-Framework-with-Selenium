package base;

import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import pages.LoginPage;
import utils.ConfigReader;
import utils.DriverFactory;

/**
 * Every test class extends this. Handles driver setup before each test
 * method and teardown after, so individual test classes never manage the
 * WebDriver lifecycle themselves.
 */
public class BaseTest {

    protected WebDriver driver;
    protected LoginPage loginPage;

    @BeforeMethod(alwaysRun = true)
    public void setUp() {
        driver = DriverFactory.initDriver();
        loginPage = new LoginPage(driver);
        loginPage.open(ConfigReader.get("base.url"));
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        DriverFactory.quitDriver();
    }
}
