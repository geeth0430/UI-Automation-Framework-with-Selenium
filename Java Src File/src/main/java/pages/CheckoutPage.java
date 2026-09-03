package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Page Object covering all three checkout steps:
 * checkout-step-one (info), checkout-step-two (overview), checkout-complete.
 */
public class CheckoutPage extends BasePage {

    // Step One: customer information
    private final By firstNameInput = By.id("first-name");
    private final By lastNameInput = By.id("last-name");
    private final By postalCodeInput = By.id("postal-code");
    private final By continueButton = By.id("continue");
    private final By errorMessage = By.cssSelector("h3[data-test='error']");

    // Step Two: order overview
    private final By finishButton = By.id("finish");
    private final By totalLabel = By.className("summary_total_label");
    private final By subtotalLabel = By.className("summary_subtotal_label");

    // Complete
    private final By completeHeader = By.className("complete-header");
    private final By backHomeButton = By.id("back-to-products");

    public CheckoutPage(WebDriver driver) {
        super(driver);
    }

    public void enterCustomerInfo(String firstName, String lastName, String postalCode) {
        type(firstNameInput, firstName);
        type(lastNameInput, lastName);
        type(postalCodeInput, postalCode);
    }

    public CheckoutPage clickContinue() {
        click(continueButton);
        return this;
    }

    public String getFormErrorMessage() {
        return getText(errorMessage);
    }

    public String getSubtotalText() {
        return getText(subtotalLabel);
    }

    public String getTotalText() {
        return getText(totalLabel);
    }

    public CheckoutPage clickFinish() {
        click(finishButton);
        return this;
    }

    public String getCompleteHeaderText() {
        return getText(completeHeader);
    }

    public InventoryPage backToProducts() {
        click(backHomeButton);
        return new InventoryPage(driver);
    }
}
