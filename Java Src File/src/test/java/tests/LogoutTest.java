package tests;

import base.BaseTest;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.InventoryPage;
import utils.ConfigReader;

/**
 * Covers logging out via the burger menu and landing back on the login page.
 */
public class LogoutTest extends BaseTest {

    @Test(description = "Logging out should return the user to the login page")
    public void logoutReturnsToLoginPage() {
        InventoryPage inventoryPage = loginPage.loginAs(
                ConfigReader.get("valid.username"),
                ConfigReader.get("valid.password"));

        inventoryPage.logout();

        Assert.assertEquals(driver.getCurrentUrl(), ConfigReader.get("base.url") + "/",
                "User should be redirected back to the login page after logout");
        Assert.assertTrue(loginPage.getCurrentUrl().contains("saucedemo.com"));
    }
}
