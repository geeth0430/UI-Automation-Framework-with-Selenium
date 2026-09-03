package tests;

import base.BaseTest;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.InventoryPage;
import utils.ConfigReader;

/**
 * Covers valid and invalid login scenarios on saucedemo.com.
 * Assertions live here — the LoginPage object only exposes actions/state.
 */
public class LoginTest extends BaseTest {

    @Test(description = "Valid credentials should land the user on the inventory page")
    public void validLoginNavigatesToInventoryPage() {
        InventoryPage inventoryPage = loginPage.loginAs(
                ConfigReader.get("valid.username"),
                ConfigReader.get("valid.password"));

        Assert.assertTrue(inventoryPage.getCurrentUrl().contains("inventory.html"),
                "Expected to land on the inventory page after a valid login");
        Assert.assertEquals(inventoryPage.getPageTitleText(), "Products");
    }

    @Test(description = "Invalid credentials should show an error and keep the user on the login page")
    public void invalidLoginShowsErrorMessage() {
        loginPage.attemptInvalidLogin(
                ConfigReader.get("invalid.username"),
                ConfigReader.get("invalid.password"));

        Assert.assertTrue(loginPage.isErrorDisplayed(), "Error message should be visible");
        Assert.assertTrue(loginPage.getErrorMessage().contains("do not match"),
                "Error message should indicate the username/password mismatch");
    }

    @Test(description = "A locked-out user should be blocked from logging in")
    public void lockedOutUserCannotLogin() {
        loginPage.attemptInvalidLogin(
                ConfigReader.get("locked.username"),
                ConfigReader.get("valid.password"));

        Assert.assertTrue(loginPage.isErrorDisplayed(), "Error message should be visible");
        Assert.assertTrue(loginPage.getErrorMessage().contains("locked out"),
                "Error message should indicate the account is locked out");
    }
}
