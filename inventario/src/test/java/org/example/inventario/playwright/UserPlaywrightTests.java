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
public class UserPlaywrightTests {

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
    public void userSearchTest(Page page) {

        login(page);

        page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Users")).click();
        page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Username")).click();
        page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Username")).fill("adm");
        page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Email")).click();
        page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Email")).fill("@gmail.com");
        page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Email")).press("Enter");
        page.getByRole(AriaRole.COMBOBOX, new Page.GetByRoleOptions().setName("Roles")).click();
        page.getByRole(AriaRole.OPTION, new Page.GetByRoleOptions().setName("ADMIN")).locator("div").click();
        page.getByRole(AriaRole.COMBOBOX, new Page.GetByRoleOptions().setName("Roles")).press("Enter");
        page.locator("html").click();
        page.getByText("admin", new Page.GetByTextOptions().setExact(true)).click();
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Clean Filter")).click();
    }

    @Test
    public void userCreateEditDeleteTest(Page page){

        login(page);

        page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Users")).click();

        page.locator("#user-btn-new").click();

        page.locator("#user-form-username").click();
        page.locator("#user-form-username").locator("input").fill("TestUsername");

        page.locator("#user-form-email").click();
        page.locator("#user-form-email").locator("input").fill("test@test.com");

        page.locator("#user-form-password").click();
        page.locator("#user-form-password").locator("input").fill("12345");

        page.locator("#user-form-password-confirm").click();
        page.locator("#user-form-password-confirm").locator("input").fill("12345");

        page.locator("#user-form-password-confirm").click();

        page.locator("#user-form-roles").locator("#toggleButton").click();

        page.getByRole(AriaRole.OPTION, new Page.GetByRoleOptions().setName("ADMIN")).click();

        page.locator("html").click();

        page.locator("#user-form-btn-save").click();

        page.getByText("TestUsername", new Page.GetByTextOptions().setExact(true)).click();

        page.locator("#user-btn-edit").click();

        page.locator("#user-form-username").click();
        page.locator("#user-form-username").locator("input").fill("TestUsernameEdit");

        page.locator("#user-form-btn-save").click();

        page.getByText("TestUsernameEdit", new Page.GetByTextOptions().setExact(true)).click();

        page.locator("#user-btn-view").click();

        page.locator("#user-form-username").click();

        page.locator("#user-form-btn-exit").click();

        page.getByText("TestUsernameEdit", new Page.GetByTextOptions().setExact(true)).click();

        page.locator("#user-btn-delete").click();

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
