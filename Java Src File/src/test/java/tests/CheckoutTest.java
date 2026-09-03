package tests;

import base.BaseTest;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import pages.CartPage;
import pages.CheckoutPage;
import pages.InventoryPage;
import utils.ConfigReader;

/**
 * End-to-end checkout flow: inventory -> cart -> checkout info ->
 * overview -> order confirmation.
 */
public class CheckoutTest extends BaseTest {

    private InventoryPage inventoryPage;

    @BeforeMethod(alwaysRun = true)
    public void loginAndAddItem() {
        inventoryPage = loginPage.loginAs(
                ConfigReader.get("valid.username"),
                ConfigReader.get("valid.password"));
        inventoryPage.addItemToCartByName("Sauce Labs Backpack");
    }

    @Test(description = "Full end-to-end checkout: add item, fill info, review order, complete purchase")
    public void completeCheckoutFlowSucceeds() {
        CartPage cartPage = inventoryPage.goToCart();
        CheckoutPage checkoutPage = cartPage.clickCheckout();

        checkoutPage.enterCustomerInfo("Jane", "Doe", "10100");
        checkoutPage.clickContinue();

        Assert.assertTrue(checkoutPage.getSubtotalText().contains("Item total"),
                "Overview page should display the item subtotal");

        checkoutPage.clickFinish();

        Assert.assertEquals(checkoutPage.getCompleteHeaderText(), "Thank you for your order!",
                "Order confirmation header should be displayed after finishing checkout");
    }

    @Test(description = "Checkout should block progress and show an error when required fields are missing")
    public void checkoutWithMissingFieldsShowsError() {
        CartPage cartPage = inventoryPage.goToCart();
        CheckoutPage checkoutPage = cartPage.clickCheckout();

        checkoutPage.enterCustomerInfo("", "", "");
        checkoutPage.clickContinue();

        Assert.assertTrue(checkoutPage.getFormErrorMessage().contains("First Name is required"),
                "Form should require first name before continuing");
    }
}
