package org.example.inventario.stepdefs;

import io.cucumber.java.After;
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
import org.springframework.data.domain.PageRequest;
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

    private String jwtToken;

    private Supplier lastSupplier;
    private Long lastProductId;

    private Long lastChangeId;
    private ProductStockChangeApi lastChangeDto;

    private ResponseEntity<ProductStockChangeApi> lastChangeByIdResponse;
    private ResponseEntity<ReturnList<ProductStockChangeApi>> lastListResponse;
    private ResponseEntity<ReturnList<ProductStockChangeApi>> lastSearchResponse;

    private LocalDateTime windowStart;
    private LocalDateTime windowEnd;

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

    private HttpHeaders authHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(jwtToken);
        return headers;
    }

    private static String iso(LocalDateTime dt) { return dt == null ? null : dt.toString(); }

    @Given("a test supplier exists")
    public void a_test_supplier_exists() {
        Supplier s = new Supplier();
        s.setName("QA Supplier " + UUID.randomUUID());
        s.setContactInfo("QA Contact");
        s.setAddress("123 QA Street");
        s.setEmail("qa@example.com");
        s.setPhoneNumber("555-0000");
        s.setEnabled(true);
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

    @When("I start a time window")
    public void i_start_a_time_window() {
        windowStart = LocalDateTime.now();
    }

    @When("I end the time window")
    public void i_end_the_time_window() {
        windowEnd = LocalDateTime.now();
    }

    private String buildSearchUrl(PageRequest page, Long productId, Boolean increased,
                                  Integer minAmount, Integer maxAmount,
                                  LocalDateTime from, LocalDateTime to,
                                  String createdBy) {
        StringBuilder sb = new StringBuilder("/api/stockchange/search");
        sb.append("?page=").append(page.getPageNumber()).append("&size=").append(page.getPageSize());
        if (productId != null) sb.append("&productId=").append(productId);
        if (increased != null) sb.append("&increased=").append(increased);
        if (minAmount != null) sb.append("&minAmount=").append(minAmount);
        if (maxAmount != null) sb.append("&maxAmount=").append(maxAmount);
        if (from != null) sb.append("&fromDate=").append(iso(from));
        if (to != null) sb.append("&toDate=").append(iso(to));
        if (createdBy != null && !createdBy.isBlank()) sb.append("&createdBy=").append(createdBy);
        return sb.toString();
    }

    @When("I SEARCH stock changes page {int} size {int} with increased true and minAmount {int} and maxAmount {int} for the last product")
    public void i_search_changes_basic_bounds_no_window(int page, int size, int minAmount, int maxAmount) {
        String url = buildSearchUrl(PageRequest.of(page, size),
                lastProductId, true, minAmount, maxAmount, null, null, null);

        lastSearchResponse = restTemplate.exchange(
                url, HttpMethod.GET, new HttpEntity<>(authHeaders()),
                new ParameterizedTypeReference<ReturnList<ProductStockChangeApi>>() {}
        );
        Assertions.assertEquals(HttpStatus.OK, lastSearchResponse.getStatusCode(), "GET /api/stockchange/search failed");
        Assertions.assertNotNull(lastSearchResponse.getBody(), "Search response body is null");
    }

    @When("I SEARCH stock changes page {int} size {int} for the last product within the captured window")
    public void i_search_changes_window_only(int page, int size) {
        LocalDateTime from = windowStart == null ? null : windowStart.minusSeconds(2);
        LocalDateTime to   = windowEnd   == null ? null : windowEnd.plusSeconds(2);

        String url = buildSearchUrl(PageRequest.of(page, size),
                lastProductId, null, null, null, from, to, null);

        lastSearchResponse = restTemplate.exchange(
                url, HttpMethod.GET, new HttpEntity<>(authHeaders()),
                new ParameterizedTypeReference<ReturnList<ProductStockChangeApi>>() {}
        );
        Assertions.assertEquals(HttpStatus.OK, lastSearchResponse.getStatusCode(), "GET /api/stockchange/search failed");
        Assertions.assertNotNull(lastSearchResponse.getBody(), "Search response body is null");
    }

    @When("I SEARCH stock changes page {int} size {int} with increased true and minAmount {int} and maxAmount {int} for the last product within the captured window")
    public void i_search_changes_bounds_with_window(int page, int size, int minAmount, int maxAmount) {
        LocalDateTime from = windowStart == null ? null : windowStart.minusSeconds(2);
        LocalDateTime to   = windowEnd   == null ? null : windowEnd.plusSeconds(2);

        String url = buildSearchUrl(PageRequest.of(page, size),
                lastProductId, true, minAmount, maxAmount, from, to, null);

        lastSearchResponse = restTemplate.exchange(
                url, HttpMethod.GET, new HttpEntity<>(authHeaders()),
                new ParameterizedTypeReference<ReturnList<ProductStockChangeApi>>() {}
        );
        Assertions.assertEquals(HttpStatus.OK, lastSearchResponse.getStatusCode(), "GET /api/stockchange/search failed");
        Assertions.assertNotNull(lastSearchResponse.getBody(), "Search response body is null");
    }

    @When("I SEARCH stock changes page {int} size {int} with increased true and minAmount null and maxAmount {int} for the last product within the captured window")
    public void i_search_changes_max_only_with_window(int page, int size, int maxAmount) {
        LocalDateTime from = windowStart == null ? null : windowStart.minusSeconds(2);
        LocalDateTime to   = windowEnd   == null ? null : windowEnd.plusSeconds(2);

        String url = buildSearchUrl(PageRequest.of(page, size),
                lastProductId, true, null, maxAmount, from, to, null);

        lastSearchResponse = restTemplate.exchange(
                url, HttpMethod.GET, new HttpEntity<>(authHeaders()),
                new ParameterizedTypeReference<ReturnList<ProductStockChangeApi>>() {}
        );
        Assertions.assertEquals(HttpStatus.OK, lastSearchResponse.getStatusCode(), "GET /api/stockchange/search failed");
        Assertions.assertNotNull(lastSearchResponse.getBody(), "Search response body is null");
    }

    @When("I SEARCH stock changes page {int} size {int} with increased true and minAmount {int} and maxAmount null for the last product within the captured window")
    public void i_search_changes_min_only_with_window(int page, int size, int minAmount) {
        LocalDateTime from = windowStart == null ? null : windowStart.minusSeconds(2);
        LocalDateTime to   = windowEnd   == null ? null : windowEnd.plusSeconds(2);

        String url = buildSearchUrl(PageRequest.of(page, size),
                lastProductId, true, minAmount, null, from, to, null);

        lastSearchResponse = restTemplate.exchange(
                url, HttpMethod.GET, new HttpEntity<>(authHeaders()),
                new ParameterizedTypeReference<ReturnList<ProductStockChangeApi>>() {}
        );
        Assertions.assertEquals(HttpStatus.OK, lastSearchResponse.getStatusCode(), "GET /api/stockchange/search failed");
        Assertions.assertNotNull(lastSearchResponse.getBody(), "Search response body is null");
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

    @Then("the search result should contain exactly {int} changes for the last product")
    public void search_result_exact_count(int expected) {
        ReturnList<ProductStockChangeApi> body = lastSearchResponse.getBody();
        Assertions.assertNotNull(body, "Search body null");
        long count = body.getData().stream()
                .filter(x -> x.getProduct() != null && Objects.equals(x.getProduct().getId(), lastProductId))
                .count();
        Assertions.assertEquals(expected, count, "Unexpected count for last product in search result");
    }

    @Then("the search result should contain at least {int} changes for the last product")
    public void search_result_at_least_count(int minExpected) {
        ReturnList<ProductStockChangeApi> body = lastSearchResponse.getBody();
        Assertions.assertNotNull(body, "Search body null");
        long count = body.getData().stream()
                .filter(x -> x.getProduct() != null && Objects.equals(x.getProduct().getId(), lastProductId))
                .count();
        Assertions.assertTrue(count >= minExpected,
                "Expected at least " + minExpected + " but got " + count + " for last product");
    }

    @And("the search result should include amounts {string}")
    public void search_result_should_include_amounts(String csv) {
        Set<Integer> expected = Arrays.stream(csv.split(","))
                .map(String::trim)
                .map(Integer::valueOf)
                .collect(Collectors.toSet());

        ReturnList<ProductStockChangeApi> body = lastSearchResponse.getBody();
        Assertions.assertNotNull(body, "Search body null");

        Set<Integer> actual = body.getData().stream()
                .filter(x -> x.getProduct() != null && Objects.equals(x.getProduct().getId(), lastProductId))
                .map(ProductStockChangeApi::getAmount)
                .collect(Collectors.toSet());

        Assertions.assertTrue(actual.containsAll(expected),
                "Expected amounts " + expected + " not all present in " + actual);
    }

    private @NotNull Category resolveCategory(String token) {
        try { return Category.valueOf(token); }
        catch (Exception e) { return Category.values()[0]; }
    }

    @After
    public void afterEachScenario() {
        lastSupplier = null;
        lastProductId = null;
        lastChangeId = null;
        lastChangeDto = null;
        lastChangeByIdResponse = null;
        lastListResponse = null;
        lastSearchResponse = null;
        windowStart = null;
        windowEnd = null;
    }
}
