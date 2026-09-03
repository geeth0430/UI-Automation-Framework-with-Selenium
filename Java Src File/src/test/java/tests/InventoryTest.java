package tests;

import base.BaseTest;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import pages.InventoryPage;
import utils.ConfigReader;

import java.util.List;

/**
 * Covers add/remove-to-cart and product sorting on the inventory page.
 */
public class InventoryTest extends BaseTest {

    private InventoryPage inventoryPage;

    // TestNG runs superclass @BeforeMethod (BaseTest#setUp, which opens the
    // browser) before this subclass @BeforeMethod automatically — no
    // explicit dependsOnMethods needed.
    @BeforeMethod(alwaysRun = true)
    public void loginBeforeEachTest() {
        inventoryPage = loginPage.loginAs(
                ConfigReader.get("valid.username"),
                ConfigReader.get("valid.password"));
    }

    @Test(description = "Adding an item to the cart should update the cart badge count")
    public void addItemToCartUpdatesBadge() {
        Assert.assertEquals(inventoryPage.getCartItemCount(), 0, "Cart should start empty");

        inventoryPage.addItemToCartByName("Sauce Labs Backpack");

        Assert.assertEquals(inventoryPage.getCartItemCount(), 1,
                "Cart badge should show 1 item after adding one product");
    }

    @Test(description = "Removing an item from the cart should decrement the cart badge count")
    public void removeItemFromCartUpdatesBadge() {
        inventoryPage.addItemToCartByName("Sauce Labs Backpack");
        inventoryPage.addItemToCartByName("Sauce Labs Bike Light");
        Assert.assertEquals(inventoryPage.getCartItemCount(), 2);

        inventoryPage.removeItemFromCartByName("Sauce Labs Backpack");

        Assert.assertEquals(inventoryPage.getCartItemCount(), 1,
                "Cart badge should decrement after removing a product");
    }

    @Test(description = "Sorting by 'Price (low to high)' should order products ascending by price")
    public void sortByPriceLowToHigh() {
        inventoryPage.sortBy("Price (low to high)");

        List<Double> prices = inventoryPage.getDisplayedItemPrices();
        List<Double> sortedCopy = prices.stream().sorted().toList();

        Assert.assertEquals(prices, sortedCopy, "Products should be sorted ascending by price");
    }

    @Test(description = "Sorting by 'Name (Z to A)' should order products descending alphabetically")
    public void sortByNameZToA() {
        inventoryPage.sortBy("Name (Z to A)");

        List<String> names = inventoryPage.getDisplayedItemNames();
        List<String> sortedCopy = names.stream()
                .sorted((a, b) -> b.compareToIgnoreCase(a))
                .toList();

        Assert.assertEquals(names, sortedCopy, "Products should be sorted descending by name");
    }
}
