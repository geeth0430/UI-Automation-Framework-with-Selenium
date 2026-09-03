package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Page Object for the /inventory.html product listing page.
 */
public class InventoryPage extends BasePage {

    private final By pageTitle = By.className("title");
    private final By inventoryItems = By.className("inventory_item");
    private final By itemNames = By.className("inventory_item_name");
    private final By itemPrices = By.className("inventory_item_price");
    private final By addToCartButtons = By.cssSelector("button[data-test^='add-to-cart']");
    private final By cartBadge = By.className("shopping_cart_badge");
    private final By cartLink = By.className("shopping_cart_link");
    private final By sortDropdown = By.className("product_sort_container");
    private final By burgerMenuButton = By.id("react-burger-menu-btn");
    private final By logoutLink = By.id("logout_sidebar_link");

    public InventoryPage(WebDriver driver) {
        super(driver);
    }

    public String getPageTitleText() {
        return getText(pageTitle);
    }

    public void addItemToCartByName(String itemName) {
        String testId = "add-to-cart-" + itemName.toLowerCase()
                .replace(" ", "-")
                .replace(".", "");
        click(By.id(testId));
    }

    public void removeItemFromCartByName(String itemName) {
        String testId = "remove-" + itemName.toLowerCase()
                .replace(" ", "-")
                .replace(".", "");
        click(By.id(testId));
    }

    public int getCartItemCount() {
        if (!isDisplayed(cartBadge)) {
            return 0;
        }
        return Integer.parseInt(getText(cartBadge));
    }

    public CartPage goToCart() {
        click(cartLink);
        return new CartPage(driver);
    }

    public void sortBy(String visibleOptionText) {
        wait.waitForVisible(sortDropdown);
        new org.openqa.selenium.support.ui.Select(driver.findElement(sortDropdown))
                .selectByVisibleText(visibleOptionText);
    }

    public List<String> getDisplayedItemNames() {
        return driver.findElements(itemNames).stream()
                .map(WebElement::getText)
                .collect(Collectors.toList());
    }

    public List<Double> getDisplayedItemPrices() {
        return driver.findElements(itemPrices).stream()
                .map(e -> Double.parseDouble(e.getText().replace("$", "")))
                .collect(Collectors.toList());
    }

    public int getInventoryItemCount() {
        return driver.findElements(inventoryItems).size();
    }

    public void logout() {
        click(burgerMenuButton);
        click(logoutLink);
    }
}
