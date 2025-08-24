package org.example.inventario.playwright;

import com.microsoft.playwright.*;
import com.microsoft.playwright.junit.UsePlaywright;
import com.microsoft.playwright.options.AriaRole;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Profile;
import org.springframework.transaction.annotation.Transactional;

@Profile("dev")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
@UsePlaywright
public class RolePlaywrightTests {

    private static final Logger log = LoggerFactory.getLogger(RolePlaywrightTests.class);
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
    void roleFilterSearchTest(Page page) {

        login(page);

        page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Role")).click();
        page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Name")).click();
        page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Name")).fill("Use");
        page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Name")).press("Enter");
        page.getByText("USER").nth(1).click();
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Clean Filter")).click();
        page.getByText("ADMIN", new Page.GetByTextOptions().setExact(true)).click();
        page.getByText("USER", new Page.GetByTextOptions().setExact(true)).click();
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Refresh")).click();

        page.locator("#role-filter-permit").click();
        page.waitForSelector("vaadin-combo-box-overlay[opened]");

        page.locator("vaadin-combo-box-overlay[opened] vaadin-combo-box-item")
                .filter(new Locator.FilterOptions().setHasText("DASHBOARD_MENU"))
                .first()
                .click();

        page.waitForSelector(
                "vaadin-combo-box-overlay[opened]",
                new Page.WaitForSelectorOptions()
                        .setState(com.microsoft.playwright.options.WaitForSelectorState.DETACHED)
        );

        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Clean Filter")).click();
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
