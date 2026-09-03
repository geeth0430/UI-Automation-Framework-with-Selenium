package tests;

import base.BaseTest;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import pages.CartPage;
import pages.InventoryPage;
import utils.ConfigReader;

import java.util.List;

/**
 * Covers the cart page: verifying contents and removing items from cart view.
 */
public class CartTest extends BaseTest {

    private InventoryPage inventoryPage;

    @BeforeMethod(alwaysRun = true)
    public void loginAndAddItems() {
        inventoryPage = loginPage.loginAs(
                ConfigReader.get("valid.username"),
                ConfigReader.get("valid.password"));
        inventoryPage.addItemToCartByName("Sauce Labs Backpack");
        inventoryPage.addItemToCartByName("Sauce Labs Bike Light");
    }

    @Test(description = "Cart page should list every item that was added from the inventory page")
    public void cartDisplaysAddedItems() {
        CartPage cartPage = inventoryPage.goToCart();

        List<String> cartItems = cartPage.getCartItemNames();

        Assert.assertEquals(cartPage.getCartItemCount(), 2);
        Assert.assertTrue(cartItems.contains("Sauce Labs Backpack"));
        Assert.assertTrue(cartItems.contains("Sauce Labs Bike Light"));
    }

    @Test(description = "Removing an item directly from the cart page should update the item list")
    public void removeItemFromCartPage() {
        CartPage cartPage = inventoryPage.goToCart();

        cartPage.removeItemByName("Sauce Labs Backpack");

        Assert.assertEquals(cartPage.getCartItemCount(), 1);
        Assert.assertFalse(cartPage.getCartItemNames().contains("Sauce Labs Backpack"));
    }

    @Test(description = "Continue Shopping button should return the user to the inventory page")
    public void continueShoppingReturnsToInventory() {
        CartPage cartPage = inventoryPage.goToCart();

        InventoryPage returnedPage = cartPage.continueShopping();

        Assert.assertTrue(returnedPage.getCurrentUrl().contains("inventory.html"));
    }
}
