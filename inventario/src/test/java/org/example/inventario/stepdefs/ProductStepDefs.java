package org.example.inventario.stepdefs;

import io.cucumber.java.Before;
import io.cucumber.java.en.*;
import org.example.inventario.model.dto.api.ProductApi;
import org.example.inventario.model.dto.api.SupplierApi;
import org.example.inventario.model.dto.scurity.ReturnAuthentication;
import org.example.inventario.model.dto.scurity.UserAuthentication;
import org.example.inventario.model.entity.inventory.Category;
import org.example.inventario.model.entity.inventory.Product;
import org.example.inventario.model.entity.inventory.Supplier;
import org.example.inventario.service.inventory.ProductService;
import org.example.inventario.service.inventory.SupplierService;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ProductStepDefs {

    private ProductApi responseProductApi;
    private Long lastProductId;
    private ResponseEntity<ProductApi> lastDtoResponse;
    private ResponseEntity<Map<String, Object>> lastListResponse;
    private String jwtToken;

    @Autowired
    private ProductService productService;
    @Autowired
    private SupplierService supplierService;
    @Autowired
    private TestRestTemplate restTemplate;

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

    private Supplier ensureSupplierExists(Long requestedId) {
        try {
            Supplier existing = supplierService.getSupplierById(requestedId);
            if (existing != null) return existing;
        } catch (Exception ignored) { /* not found */ }

        Supplier s = new Supplier();
        s.setName("Test Supplier");
        s.setEmail("supplier@example.com");
        s.setAddress("Somewhere 123");
        s.setPhoneNumber("000-000-0000");
        s.setContactInfo("QA contact");
        return supplierService.createSupplier(s); // adjust if your service method name differs
    }

    @Given("a supplier exists with id {int}")
    public void a_supplier_exists_with_id(Integer supplierId) {
        ensureSupplierExists(supplierId.longValue());
    }

    @Given("a product exists with name {string}, description {string}, category {string}, price {double}, stock {int}, minStock {int}, supplier Id {int}")
    public void a_product_exists(String name, String description, String category, Double price, Integer stock, Integer minStock, Integer supplierId) {
        Supplier ensured = ensureSupplierExists(supplierId.longValue());

        ProductApi toCreate = new ProductApi();
        toCreate.setName(name);
        toCreate.setDescription(description);
        toCreate.setCategory(category);
        toCreate.setPrice(BigDecimal.valueOf(price));
        toCreate.setStock(stock);
        toCreate.setMinStock(minStock);

        SupplierApi s = new SupplierApi();
        s.setId(ensured.getId());
        toCreate.setSupplier(s);

        HttpEntity<ProductApi> request = new HttpEntity<>(toCreate, authHeadersJson());
        ResponseEntity<ProductApi> postResponse = restTemplate.exchange(
                "/api/product", HttpMethod.POST, request, ProductApi.class
        );

        Assertions.assertEquals(HttpStatus.OK, postResponse.getStatusCode(), "Create product failed");
        Assertions.assertNotNull(postResponse.getBody(), "Create product returned null body");

        responseProductApi = postResponse.getBody();
        lastProductId = responseProductApi.getId();
    }

    @When("I create a product with name {string}, description {string}, category {string}, price {double}, stock {int}, minStock {int}, supplier Id {int}")
    public void i_create_a_product(String name, String description, String category, Double price, Integer stock, Integer minStock, Integer supplierId) {

        Supplier ensured = ensureSupplierExists(supplierId.longValue());

        ProductApi toCreate = new ProductApi();
        toCreate.setName(name);
        toCreate.setDescription(description);
        toCreate.setCategory(category);
        toCreate.setPrice(BigDecimal.valueOf(price));
        toCreate.setStock(stock);
        toCreate.setMinStock(minStock);

        SupplierApi s = new SupplierApi();
        s.setId(ensured.getId());
        toCreate.setSupplier(s);

        HttpEntity<ProductApi> request = new HttpEntity<>(toCreate, authHeadersJson());
        lastDtoResponse = restTemplate.exchange("/api/product", HttpMethod.POST, request, ProductApi.class);

        Assertions.assertEquals(HttpStatus.OK, lastDtoResponse.getStatusCode());
        Assertions.assertNotNull(lastDtoResponse.getBody());

        responseProductApi = lastDtoResponse.getBody();
        lastProductId = responseProductApi.getId();
    }

    @When("I get the product by ID")
    public void i_get_product_by_id() {
        HttpEntity<Void> request = new HttpEntity<>(authHeaders());
        lastDtoResponse = restTemplate.exchange(
                "/api/product/" + lastProductId, HttpMethod.GET, request, ProductApi.class
        );
        Assertions.assertEquals(HttpStatus.OK, lastDtoResponse.getStatusCode());
        responseProductApi = lastDtoResponse.getBody();
    }

    @When("I update the product name to {string}")
    public void i_update_the_product_name_to(String newName) {
        // Build a full DTO including supplier
        ProductApi toUpdate = new ProductApi();
        toUpdate.setId(lastProductId);
        toUpdate.setName(newName);
        toUpdate.setDescription(responseProductApi.getDescription());
        toUpdate.setCategory(responseProductApi.getCategory());
        toUpdate.setPrice(responseProductApi.getPrice());
        toUpdate.setStock(responseProductApi.getStock());
        toUpdate.setMinStock(responseProductApi.getMinStock());
        toUpdate.setImage(responseProductApi.getImage());
        toUpdate.setSupplier(responseProductApi.getSupplier());

        HttpEntity<ProductApi> request = new HttpEntity<>(toUpdate, authHeadersJson());
        lastDtoResponse = restTemplate.exchange(
                "/api/product/" + lastProductId, HttpMethod.PUT, request, ProductApi.class
        );
        Assertions.assertEquals(HttpStatus.OK, lastDtoResponse.getStatusCode());
        responseProductApi = lastDtoResponse.getBody();
    }

    @When("I delete the product")
    public void i_delete_the_product() {
        HttpEntity<Void> request = new HttpEntity<>(authHeaders());
        lastDtoResponse = restTemplate.exchange(
                "/api/product/" + lastProductId, HttpMethod.DELETE, request, ProductApi.class
        );
        Assertions.assertEquals(HttpStatus.OK, lastDtoResponse.getStatusCode());
        responseProductApi = lastDtoResponse.getBody();
    }

    @When("I list products page {int} size {int}")
    public void i_list_products_page_size(Integer page, Integer size) {
        HttpEntity<Void> request = new HttpEntity<>(authHeaders());

        String url = String.format("/api/product?page=%d&size=%d", page, size);
        lastListResponse = restTemplate.exchange(
                url, HttpMethod.GET, request, new ParameterizedTypeReference<Map<String, Object>>() {}
        );
        Assertions.assertEquals(HttpStatus.OK, lastListResponse.getStatusCode());
    }

    @Then("the response product should have name {string}")
    public void the_response_product_should_have_name(String expectedName) {
        Assertions.assertNotNull(responseProductApi, "Response ProductApi is null");
        Assertions.assertEquals(expectedName, responseProductApi.getName(), "Name mismatch");
        Assertions.assertEquals(lastProductId, responseProductApi.getId(), "ID mismatch");
    }

    @Then("the product should be marked as disabled")
    public void the_product_should_be_marked_as_disabled() {
        Product persisted = productService.getProductById(lastProductId);
        Assertions.assertNotNull(persisted, "Deleted product not found");
        Assertions.assertFalse(persisted.isEnabled(), "Product was not soft-deleted (enabled should be false)");
    }

    @Then("the list response should contain at least {int} elements")
    public void the_list_response_should_contain_at_least_n(Integer n) {
        Map<String, Object> body = lastListResponse.getBody();
        Assertions.assertNotNull(body, "List response body null");

        Object totalElements = body.get("totalElements");
        Assertions.assertNotNull(totalElements, "totalElements missing");
        long total = (totalElements instanceof Integer i) ? i.longValue()
                : (totalElements instanceof Long l) ? l
                : Long.parseLong(totalElements.toString());

        Assertions.assertTrue(total >= n, "totalElements < expected");

        Object data = body.get("data");
        Assertions.assertNotNull(data, "data missing");
        Assertions.assertInstanceOf(List.class, data, "data is not a List");
    }

    @Then("the list page should be {int} and pageSize should be {int}")
    public void the_list_page_should_be_and_page_size_should_be(Integer page, Integer size) {
        Map<String, Object> body = lastListResponse.getBody();
        Assertions.assertNotNull(body);

        Assertions.assertEquals(page, ((Number) body.get("page")).intValue(), "page mismatch");
        Assertions.assertEquals(size, ((Number) body.get("pageSize")).intValue(), "pageSize mismatch");
    }


}
