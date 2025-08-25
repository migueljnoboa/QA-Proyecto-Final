package org.example.inventario.playwright;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.junit.UsePlaywright;
import com.microsoft.playwright.options.AriaRole;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Profile;
import org.springframework.transaction.annotation.Transactional;

import com.microsoft.playwright.Locator;

@Profile("dev")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
@UsePlaywright
public class ProductPlaywrightTests {
    private static final Logger log = LoggerFactory.getLogger(ProductPlaywrightTests.class);
    static Playwright playwright;
    static Browser browser;

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
    void productCreateEditDeleteTest(Page page) {

        login(page);

        page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Product")).click();

        page.locator("#prod-btn-new").click();

        page.locator("#prod-form-name").click();
        page.locator("#prod-form-name").locator("input").fill("NewProductTest");

        page.locator("#prod-form-category").click();
        page.getByRole(AriaRole.OPTION, new Page.GetByRoleOptions().setName("ELECTRONICS")).click();

        page.locator("#prod-form-price").click();
        page.locator("#prod-form-price").locator("input").fill("1000.00");

        page.locator("#prod-form-stock").click();
        page.locator("#prod-form-stock").locator("input").fill("10");

        page.locator("#prod-form-minstock").click();
        page.locator("#prod-form-minstock").locator("input").fill("10");

        page.locator("#prod-form-supplier").click();
        page.getByRole(AriaRole.OPTION, new Page.GetByRoleOptions().setName("Tech Supply Co.")).click();

        page.locator("#prod-form-description").click();
        page.locator("#prod-form-description").locator("textarea").fill("Test Product");

        page.locator("#prod-form-btn-save").click();

        page.locator("#prod-grid").getByText("NewProductTest").click();

        page.locator("#prod-btn-edit").click();

        page.locator("#prod-form-name").click();
        page.locator("#prod-form-name").locator("input").fill("EditProductTest");

        page.locator("#prod-form-stock").click();
        page.locator("#prod-form-stock").locator("input").fill("15");

        page.locator("#prod-form-btn-save").click();

        page.getByText("EditProductTest", new Page.GetByTextOptions().setExact(true)).click();

        page.locator("#prod-btn-delete").click();

        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Confirm")).click();

        page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Stock Changes")).click();

        page.locator("#psc-grid").getByText("EditProductTest").click();
    }

    private void login(Page page){
        page.navigate("http://localhost:" + port + "/login");
        page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Username")).click();
        page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Username")).fill("admin");
        page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Password")).click();
        page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Password")).fill("admin");
        page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Password")).press("Enter");
    }
}