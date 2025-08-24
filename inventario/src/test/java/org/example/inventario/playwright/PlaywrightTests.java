package org.example.inventario.playwright;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.junit.UsePlaywright;
import com.microsoft.playwright.options.AriaRole;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Profile;
import org.springframework.transaction.annotation.Transactional;

import com.microsoft.playwright.Locator;

@Profile("dev")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
@UsePlaywright
public class PlaywrightTests {
  // Shared between all tests in this class.
  static Playwright playwright;
  static Browser browser;

  // New instance for each test method.
  BrowserContext context;
  Page page;

  @LocalServerPort
  private int port;

  @BeforeAll
  static void launchBrowser() {
    playwright = Playwright.create();
    browser = playwright.chromium().launch();
  }

  @AfterAll
  static void closeBrowser() {
    playwright.close();
  }

  @BeforeEach
  void createContextAndPage() {
    context = browser.newContext();
    page = context.newPage();
    page.setDefaultTimeout(5000);
  }

  @AfterEach
  void closeContext() {
    context.close();
  }

  @Test
  void loginAndLogOutTest() {
//    page.navigate("http://localhost:" + port + "/login");
//    page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Username")).click();
//    page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Username")).fill("admin");
//    page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Password")).click();
//    page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Password")).fill("admin");
//    page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Log in")).click();
//
//    // Wait for navigation to complete
//    page.waitForURL("**/");
//
//    page.getByText("Stock2u").click();
//    page.getByRole(AriaRole.MENUITEM, new Page.GetByRoleOptions().setName("admin")).click();
//    page.getByRole(AriaRole.MENUITEM, new Page.GetByRoleOptions().setName("Logout")).click();
  }

  @Test
  void productCrudTest() {
//    // Login first
//    loginAsAdmin();
//
//    // Navigate to product page
//    page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Product")).click();
//
//    // Wait for the product page to load
//    page.waitForSelector("text=Product");
//
//    // Test creating a new product
//    createNewProduct();
//
//    // Test editing the product
//    editProduct();
//
//    // Test viewing the product
//    viewProduct();
//
//    // Test deleting the product
//    deleteProduct();
  }

  private void loginAsAdmin() {
    page.navigate("http://localhost:" + port + "/login");
    page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Username")).fill("admin");
    page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Password")).fill("admin");
    page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Log in")).click();

    // Wait for successful login
    page.waitForURL("**/");
  }

  private void createNewProduct() {

    page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Product")).click();
    page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("New")).click();
    page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Product Name")).click();
    page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Product Name")).fill("New Product Name");
    page.getByRole(AriaRole.COMBOBOX, new Page.GetByRoleOptions().setName("Category")).click();

    page.locator("vaadin-combo-box-item")
            .filter(new Locator.FilterOptions().setHasText("ELECTRONICS"))
            .click();

    page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Price ($)")).fill("100");
    page.getByRole(AriaRole.SPINBUTTON, new Page.GetByRoleOptions().setName("Stock").setExact(true)).click();
    page.getByRole(AriaRole.SPINBUTTON, new Page.GetByRoleOptions().setName("Stock").setExact(true)).fill("15");
    page.getByRole(AriaRole.SPINBUTTON, new Page.GetByRoleOptions().setName("Min Stock")).click();
    page.getByRole(AriaRole.SPINBUTTON, new Page.GetByRoleOptions().setName("Min Stock")).fill("2");

    page.getByRole(AriaRole.COMBOBOX, new Page.GetByRoleOptions().setName("Supplier")).click();

    page.locator("vaadin-combo-box-item")
            .filter(new Locator.FilterOptions().setHasText("Tech Supply Co."))
            .click();

    page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Description")).click();
    page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Description")).fill("Text Description");
    page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Save (F10)")).click();

    page.waitForSelector("text=Product saved successfully");
  }

  private void editProduct() {

    page.getByText("New Product Name").first().click();
    page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Edit")).click();
    page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Product Name")).click();
    page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Product Name")).press("ArrowRight");
    page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Product Name")).press("ArrowRight");
    page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Product Name")).press("ArrowRight");
    page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Product Name")).press("ArrowRight");
    page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Product Name")).press("ArrowRight");
    page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Product Name")).press("ArrowRight");
    page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Product Name")).fill("New Product Name Test");
    page.getByRole(AriaRole.SPINBUTTON, new Page.GetByRoleOptions().setName("Stock").setExact(true)).click();
    page.getByRole(AriaRole.SPINBUTTON, new Page.GetByRoleOptions().setName("Stock").setExact(true)).press("ArrowRight");
    page.getByRole(AriaRole.SPINBUTTON, new Page.GetByRoleOptions().setName("Stock").setExact(true)).fill("16");
    page.getByRole(AriaRole.SPINBUTTON, new Page.GetByRoleOptions().setName("Min Stock")).click();
    page.getByRole(AriaRole.SPINBUTTON, new Page.GetByRoleOptions().setName("Min Stock")).fill("3");
    page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Save (F10)")).click();

    // Verify changes were saved
    page.waitForSelector("text=Product saved successfully");
  }

  private void viewProduct() {
    // Select the edited product
    page.locator("vaadin-grid-cell-content")
            .filter(new Locator.FilterOptions().setHasText("New Product Name Test"))
            .click();
    page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("View")).click();
    page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Exit (ESC)")).click();
    page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Confirm")).click();

  }

  private void deleteProduct() {
    // Select the product to delete
    page.locator("vaadin-grid-cell-content")
            .filter(new Locator.FilterOptions().setHasText("New Product Name Test"))
            .click();
    page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Cancel")).click();
    page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Confirm")).click();
  }
}