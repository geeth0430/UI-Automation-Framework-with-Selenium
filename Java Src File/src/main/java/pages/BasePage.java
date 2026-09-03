package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import utils.WaitUtils;

/**
 * Common plumbing shared by every page object: driver access, the explicit
 * wait helper, and PageFactory initialization for @FindBy locators.
 *
 * Page objects hold ONLY locators + action methods (click/type/read text).
 * They never contain Assert.* calls — assertions live exclusively in the
 * test classes, keeping pages reusable across many different tests.
 */
public abstract class BasePage {

    protected WebDriver driver;
    protected WaitUtils wait;

    protected BasePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WaitUtils(driver);
        PageFactory.initElements(driver, this);
    }

    protected void click(By locator) {
        wait.waitForClickable(locator).click();
    }

    protected void type(By locator, String text) {
        WebElement element = wait.waitForVisible(locator);
        element.clear();
        element.sendKeys(text);
    }

    protected String getText(By locator) {
        return wait.waitForVisible(locator).getText();
    }

    protected boolean isDisplayed(By locator) {
        try {
            return wait.waitForVisible(locator).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public String getPageTitle() {
        return driver.getTitle();
    }

    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }
}
