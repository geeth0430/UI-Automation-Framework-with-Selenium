package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Page Object for the /cart.html page.
 */
public class CartPage extends BasePage {

    private final By cartItems = By.className("cart_item");
    private final By itemNames = By.className("inventory_item_name");
    private final By checkoutButton = By.id("checkout");
    private final By continueShoppingButton = By.id("continue-shopping");

    public CartPage(WebDriver driver) {
        super(driver);
    }

    public List<String> getCartItemNames() {
        return driver.findElements(itemNames).stream()
                .map(WebElement::getText)
                .collect(Collectors.toList());
    }

    public int getCartItemCount() {
        return driver.findElements(cartItems).size();
    }

    public void removeItemByName(String itemName) {
        String testId = "remove-" + itemName.toLowerCase()
                .replace(" ", "-")
                .replace(".", "");
        click(By.id(testId));
    }

    public CheckoutPage clickCheckout() {
        click(checkoutButton);
        return new CheckoutPage(driver);
    }

    public InventoryPage continueShopping() {
        click(continueShoppingButton);
        return new InventoryPage(driver);
    }
}
