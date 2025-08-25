package org.example.inventario.playwright.firefox;

import com.microsoft.playwright.*;
import com.microsoft.playwright.junit.UsePlaywright;
import com.microsoft.playwright.options.AriaRole;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Profile;
import org.springframework.transaction.annotation.Transactional;

@Profile("dev")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
@UsePlaywright
public class SupplierPlaywrightTests {

    static Playwright playwright;
    static Browser browser;

    BrowserContext context;
    Page page;

    @LocalServerPort
    private int port;

    @BeforeAll
    static void launchBrowser() {
        playwright = Playwright.create();
        browser = playwright.firefox().launch();
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
    public void supplierSearchTest(Page page) {

        login(page);

        page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Supplier")).click();

        page.locator("#sup-filter-name").click();
        page.locator("#sup-filter-name").locator("input").fill("Tech");
        page.locator("#sup-filter-name").locator("input").press("Enter");

        page.getByText("Tech Supply Co.").click();
    }

    @Test
    void supplierCreateEditDeleteTest(Page page) {

        login(page);

        page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Supplier")).click();

        page.locator("#sup-btn-new").click();

        page.locator("#sup-form-name").click();
        page.locator("#sup-form-name").locator("input").fill("TestSupplier");

        page.locator("#sup-form-contact").click();
        page.locator("#sup-form-contact").locator("input").fill("Contact Supplier");

        page.locator("#sup-form-email").click();
        page.locator("#sup-form-email").locator("input").fill("supplier@supplier.com");

        page.locator("#sup-form-address").click();
        page.locator("#sup-form-address").locator("input").fill("Supplier Address");

        page.locator("#sup-form-phone").click();
        page.locator("#sup-form-phone").locator("input").fill("8908098890");

        page.locator("#sup-form-phone").click();
        page.locator("#sup-form-phone").locator("input").fill("8908098890");

        page.locator("#sup-form-btn-save").click();

        page.getByText("TestSupplier", new Page.GetByTextOptions().setExact(true)).click();

        page.locator("#sup-btn-view").click();

        page.locator("#sup-form-btn-exit").click();

        page.getByText("TestSupplier", new Page.GetByTextOptions().setExact(true)).click();

        page.locator("#sup-btn-edit").click();

        page.locator("#sup-form-name").click();
        page.locator("#sup-form-name").locator("input").fill("TestSupplierUpdate");

        page.locator("#sup-form-btn-save").click();

        page.getByText("TestSupplierUpdate", new Page.GetByTextOptions().setExact(true)).click();

        page.locator("#sup-btn-cancel").click();

        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Confirm")).click();
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
