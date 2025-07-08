package org.example.inventario.playwright;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.options.AriaRole;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Profile("dev")
@SpringBootTest
@Transactional
public class PlaywrightTests {
  // Shared between all tests in this class.
  static Playwright playwright;
  static Browser browser;

  // New instance for each test method.
  BrowserContext context;
  Page page;

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
  }

  @AfterEach
  void closeContext() {
    context.close();
  }

  @Test
  void loginAndLogOutTest() {
    page.navigate("http://localhost:8080/login");
    page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Username")).click();
    page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Username")).fill("admin");
    page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Password")).click();
    page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Password")).fill("admin");
    page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Log in")).click();
    page.getByText("Stock2u").click();
    page.locator("vaadin-menu-bar-item div slot").click();
    page.getByRole(AriaRole.MENUITEM, new Page.GetByRoleOptions().setName("Logout")).locator("slot").click();
  }

  @Test
  void ProductTest() {

    // Login
    page.navigate("http://localhost:8080/login");
    page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Username")).click();
    page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Username")).fill("admin");
    page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Password")).click();
    page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Password")).fill("admin");
    page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Show password")).click();
    page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Show password")).click();
    page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Log in")).click();

    // Filtered Search
    page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Product")).click();
    page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Name")).click();
    page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Name")).fill("NVIDIA");
    page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Name")).press("Enter");
    page.getByText("NVIDIA RTX").click();

    // Create Product

    // Edit Product

    // Delete Product

    // View Product


  }
}