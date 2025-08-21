package org.example.inventario.stepdefs;

import io.cucumber.java.Before;
import io.cucumber.java.en.*;
import org.example.inventario.model.dto.api.ProductStockChangeApi;
import org.example.inventario.model.dto.inventory.ReturnList;
import org.example.inventario.model.dto.scurity.ReturnAuthentication;
import org.example.inventario.model.dto.scurity.UserAuthentication;
import org.example.inventario.model.entity.inventory.Category;
import org.example.inventario.model.entity.inventory.Product;
import org.example.inventario.model.entity.inventory.ProductStockChange;
import org.example.inventario.model.entity.inventory.Supplier;
import org.example.inventario.repository.inventory.ProductStockChangeRepository;
import org.example.inventario.service.inventory.ProductService;
import org.example.inventario.service.inventory.ProductStockChangeService;
import org.example.inventario.service.inventory.SupplierService;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class StockChangeStepDefs {

    @Autowired private TestRestTemplate restTemplate;

    @Autowired private SupplierService supplierService;
    @Autowired private ProductService productService;
    @Autowired private ProductStockChangeRepository stockChangeRepository;
    @Autowired private ProductStockChangeService stockChangeService;

    private String jwtToken;

    private Supplier lastSupplier;
    private Long lastProductId;

    private Long lastChangeId;
    private ProductStockChangeApi lastChangeDto;

    private ResponseEntity<ProductStockChangeApi> lastChangeByIdResponse;
    private ResponseEntity<ReturnList<ProductStockChangeApi>> lastListResponse;

    @Before
    public void authenticate() {
        UserAuthentication credentials = new UserAuthentication("admin", "admin");

        ResponseEntity<ReturnAuthentication> loginResponse = restTemplate.postForEntity(
                "/api/security/authenticate", credentials, ReturnAuthentication.class
        );

        Assertions.assertEquals(HttpStatus.OK, loginResponse.getStatusCode(), "Login failed");
        Assertions.assertNotNull(loginResponse.getBody(), "Login response body is null");
        jwtToken = loginResponse.getBody().token();
        Assertions.assertNotNull(jwtToken, "JWT token must not be null");
    }

    private HttpHeaders authHeadersJson() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(jwtToken);
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    private HttpHeaders authHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(jwtToken);
        return headers;
    }

    @Given("a test supplier exists")
    public void a_test_supplier_exists() {
        Supplier s = new Supplier();
        s.setName("QA Supplier " + UUID.randomUUID());
        s.setContactInfo("QA Contact");
        s.setAddress("123 QA Street");
        s.setEmail("qa@example.com");
        s.setPhoneNumber("555-0000");
        s.setEnabled(true);
        s.setLastModifiedBy("test");
        lastSupplier = supplierService.createSupplier(s);
        Assertions.assertNotNull(lastSupplier.getId(), "Supplier ID is null");
    }

    @Given("a product named {string} priced {double} in category {string} with initial stock {int} using that supplier")
    public void a_product_named_priced_category_stock_using_supplier(String name, double price, String category, int stock) {
        Product p = new Product();
        p.setName(name);
        p.setDescription("Test product");
        p.setCategory(resolveCategory(category));
        p.setPrice(BigDecimal.valueOf(price));
        p.setStock(stock);
        p.setMinStock(0);
        p.setImage(null);
        p.setSupplier(lastSupplier);
        p.setEnabled(true);
        p.setLastModifiedBy("test");

        Product created = productService.createProduct(p);
        Assertions.assertNotNull(created, "createProduct returned null");
        Assertions.assertNotNull(created.getId(), "Created product ID is null");
        lastProductId = created.getId();
    }

    @When("I service-update the last product stock to {int}")
    public void i_service_update_last_product_stock_to(int newStock) {
        Product update = new Product();
        update.setName("updated-" + UUID.randomUUID());
        update.setDescription("updated-desc");
        update.setCategory(Category.values()[0]);
        update.setPrice(BigDecimal.ONE);
        update.setStock(newStock);
        update.setMinStock(0);
        update.setImage(null);
        update.setSupplier(lastSupplier);
        update.setEnabled(true);
        update.setLastModifiedBy("test-update");

        Product updated = productService.updateProduct(lastProductId, update);
        Assertions.assertNotNull(updated, "updateProduct returned null");
        Assertions.assertEquals(newStock, updated.getStock(), "Stock not updated to expected value");
    }

    @When("I capture the latest stock change id for the last product")
    public void i_capture_latest_change_id_for_last_product() {
        List<ProductStockChange> all = stockChangeRepository.findAll();
        Optional<ProductStockChange> latest = all.stream()
                .filter(sc -> sc.getProduct() != null && Objects.equals(sc.getProduct().getId(), lastProductId))
                .max(Comparator.comparing(ProductStockChange::getDate, Comparator.nullsLast(LocalDateTime::compareTo)));

        Assertions.assertTrue(latest.isPresent(), "No stock change found for product " + lastProductId);
        lastChangeId = latest.get().getId();
    }

    @When("I GET the stock change by that id")
    public void i_get_the_stock_change_by_that_id() {
        lastChangeByIdResponse = restTemplate.exchange(
                "/api/stockchange/" + lastChangeId, HttpMethod.GET,
                new HttpEntity<>(authHeaders()), ProductStockChangeApi.class
        );
        Assertions.assertEquals(HttpStatus.OK, lastChangeByIdResponse.getStatusCode(), "GET /api/stockchange/{id} failed");
        lastChangeDto = lastChangeByIdResponse.getBody();
        Assertions.assertNotNull(lastChangeDto, "GET-by-id body is null");
    }

    @When("I list stock changes page {int} size {int}")
    public void i_list_stock_changes_page_size(int page, int size) {
        String url = String.format("/api/stockchange?page=%d&size=%d", page, size);
        lastListResponse = restTemplate.exchange(
                url, HttpMethod.GET, new HttpEntity<>(authHeaders()),
                new ParameterizedTypeReference<ReturnList<ProductStockChangeApi>>() {}
        );
        Assertions.assertEquals(HttpStatus.OK, lastListResponse.getStatusCode(), "GET /api/stockchange failed");
        Assertions.assertNotNull(lastListResponse.getBody(), "List response body is null");
    }

    @Then("the response stock change should have increased {word} and amount {int}")
    public void the_response_stock_change_should_have(String increasedStr, int expectedAmount) {
        boolean expectedIncreased = Boolean.parseBoolean(increasedStr);
        Assertions.assertNotNull(lastChangeDto, "No change DTO loaded");
        Assertions.assertEquals(expectedIncreased, lastChangeDto.isIncreased(), "increased mismatch");
        Assertions.assertEquals(expectedAmount, lastChangeDto.getAmount(), "amount mismatch");
    }

    @Then("the response stock change should reference the last product")
    public void the_response_stock_change_should_reference_last_product() {
        Assertions.assertNotNull(lastChangeDto.getProduct(), "Nested product DTO is null");
        Assertions.assertEquals(lastProductId, lastChangeDto.getProduct().getId(), "product id mismatch");
    }

    @Then("the list should contain a change for the last product with increased {word} and amount {int}")
    public void the_list_should_contain_expected_change(String incStr, int amount) {
        boolean inc = Boolean.parseBoolean(incStr);
        ReturnList<ProductStockChangeApi> body = lastListResponse.getBody();
        Assertions.assertNotNull(body, "List body null");
        boolean found = body.getData().stream()
                .filter(x -> x.getProduct() != null && Objects.equals(x.getProduct().getId(), lastProductId))
                .anyMatch(x -> x.isIncreased() == inc && x.getAmount() == amount);
        Assertions.assertTrue(found,
                "Expected change not found in list for productId=" + lastProductId +
                        ", increased=" + inc + ", amount=" + amount);
    }

    private @NotNull Category resolveCategory(String token) {
        try {
            return Category.valueOf(token);
        } catch (Exception e) {
            return Category.values()[0];
        }
    }
}
