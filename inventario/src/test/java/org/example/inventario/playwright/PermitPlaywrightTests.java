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

@Profile("dev")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
@UsePlaywright
public class PermitPlaywrightTests {

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
    void permitPagetest(Page page) {

        login(page);

        page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Permit")).click();
        page.getByText("DASHBOARD_MENU").click();
        page.getByText("SUPPLIERS_MENU").click();
        page.getByText("SUPPLIER_CREATE").click();
        page.getByText("SUPPLIER_EDIT").click();
        page.getByText("SUPPLIER_DELETE").click();
        page.getByText("SUPPLIER_VIEW").click();
        page.getByText("PRODUCTS_MENU").click();
        page.getByText("PRODUCT_CREATE").click();
        page.getByText("PRODUCT_EDIT").click();
        page.getByText("PRODUCT_DELETE").click();
        page.getByText("PRODUCT_VIEW").click();
        page.getByText("USERS_MENU").click();
        page.getByText("USER_CREATE").click();
        page.getByText("USER_EDIT").click();
        page.getByText("USER_DELETE").click();
        page.getByText("ROLES_MENU").click();
        page.getByText("ROLE_CREATE").click();
        page.getByText("ROLE_EDIT").click();
        page.getByText("ROLE_DELETE").click();
        page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Name")).click();
        page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Name")).fill("Dash");
        page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Name")).press("Enter");
        page.getByText("DASHBOARD_MENU").click();
        page.locator("#clearButton").click();
        page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Name")).fill("suppl");
        page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Name")).press("Enter");
        page.getByText("SUPPLIERS_MENU").click();
        page.getByText("SUPPLIER_CREATE").click();
        page.getByText("SUPPLIER_EDIT").click();
        page.getByText("SUPPLIER_DELETE").click();
        page.getByText("SUPPLIER_VIEW").first().click();
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Clean Filter")).click();
        page.getByText("ROLE_DELETE").click();
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
