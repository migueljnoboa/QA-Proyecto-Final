package org.example.inventario.stepdefs;

import io.cucumber.java.Before;
import io.cucumber.java.en.*;
import org.example.inventario.model.dto.api.SupplierApi;
import org.example.inventario.model.dto.inventory.ReturnList;
import org.example.inventario.model.dto.scurity.ReturnAuthentication;
import org.example.inventario.model.dto.scurity.UserAuthentication;
import org.example.inventario.model.entity.inventory.Supplier;
import org.example.inventario.service.inventory.SupplierService;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;

import java.util.Objects;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SupplierStepDefs {

    @Autowired private TestRestTemplate restTemplate;
    @Autowired private SupplierService supplierService;

    private String jwtToken;

    private Supplier supplierPayload;
    private ResponseEntity<SupplierApi> lastDtoResponse;
    private ResponseEntity<ReturnList<SupplierApi>> lastListResp;
    private SupplierApi lastSupplierDto;
    private Long lastSupplierId;

    @Before
    public void authenticate() {
        var credentials = new UserAuthentication("admin", "admin");
        var loginResponse = restTemplate.postForEntity(
                "/api/security/authenticate", credentials, ReturnAuthentication.class
        );
        Assertions.assertEquals(HttpStatus.OK, loginResponse.getStatusCode(), "Login failed");
        Assertions.assertNotNull(loginResponse.getBody(), "Login response is null");
        jwtToken = loginResponse.getBody().token();
        Assertions.assertNotNull(jwtToken, "JWT token must not be null");
    }

    private HttpHeaders authJson() {
        var h = new HttpHeaders();
        h.setBearerAuth(jwtToken);
        h.setContentType(MediaType.APPLICATION_JSON);
        return h;
    }
    private HttpEntity<Void> authOnly() { return new HttpEntity<>(authJson()); }

    @Given("a supplier payload with name {string}, contact info {string}, address {string}, email {string}, and phone {string}")
    public void supplier_payload(String name, String contactInfo, String address, String email, String phone) {
        supplierPayload = new Supplier();
        supplierPayload.setName(name);
        supplierPayload.setContactInfo(contactInfo);
        supplierPayload.setAddress(address);
        supplierPayload.setEmail(email);
        supplierPayload.setPhoneNumber(phone);
        supplierPayload.setEnabled(true);
    }

    @When("I create the supplier")
    public void create_supplier() {
        HttpEntity<Supplier> req = new HttpEntity<>(supplierPayload, authJson());
        lastDtoResponse = restTemplate.exchange(
                "/api/supplier/", HttpMethod.POST, req, SupplierApi.class
        );
        Assertions.assertEquals(HttpStatus.OK, lastDtoResponse.getStatusCode(), "Create failed");
        Assertions.assertNotNull(lastDtoResponse.getBody(), "Create returned null body");
        lastSupplierDto = lastDtoResponse.getBody();
        lastSupplierId = lastSupplierDto.getId();
        Assertions.assertNotNull(lastSupplierId, "Created supplier ID is null");
    }

    @When("I list suppliers page {int} size {int}")
    public void list_suppliers(int page, int size) {
        String url = String.format("/api/supplier?page=%d&size=%d", page, size);
        lastListResp = restTemplate.exchange(
                url, HttpMethod.GET, authOnly(),
                new ParameterizedTypeReference<ReturnList<SupplierApi>>() {}
        );
        Assertions.assertEquals(HttpStatus.OK, lastListResp.getStatusCode(), "List failed");
        Assertions.assertNotNull(lastListResp.getBody(), "List body is null");
    }

    @When("I get the supplier by the last created ID")
    public void get_supplier_by_last_id() {
        String url = "/api/supplier/" + lastSupplierId;
        lastDtoResponse = restTemplate.exchange(url, HttpMethod.GET, authOnly(), SupplierApi.class);
        Assertions.assertEquals(HttpStatus.OK, lastDtoResponse.getStatusCode(), "Get by ID failed");
        lastSupplierDto = lastDtoResponse.getBody();
        Assertions.assertNotNull(lastSupplierDto, "Get by ID body is null");
    }

    @When("I update the supplier name to {string}")
    public void update_supplier_name(String newName) {
        Supplier toUpdate = new Supplier();
        toUpdate.setId(lastSupplierId);
        toUpdate.setName(newName);
        toUpdate.setContactInfo(
                supplierPayload != null ? supplierPayload.getContactInfo() : "Updated Contact"
        );
        toUpdate.setAddress(
                supplierPayload != null ? supplierPayload.getAddress() : "Updated Address"
        );
        toUpdate.setEmail(
                supplierPayload != null ? supplierPayload.getEmail() : "updated@example.com"
        );
        toUpdate.setPhoneNumber(
                supplierPayload != null ? supplierPayload.getPhoneNumber() : "999999"
        );
        toUpdate.setEnabled(true);

        HttpEntity<Supplier> req = new HttpEntity<>(toUpdate, authJson());
        lastDtoResponse = restTemplate.exchange(
                "/api/supplier/" + lastSupplierId, HttpMethod.PUT, req, SupplierApi.class
        );
        Assertions.assertEquals(HttpStatus.OK, lastDtoResponse.getStatusCode(), "Update failed");
        lastSupplierDto = lastDtoResponse.getBody();
        Assertions.assertNotNull(lastSupplierDto, "Update body is null");
    }

    @When("I delete the supplier by the last created ID")
    public void delete_supplier_by_last_id() {
        lastDtoResponse = restTemplate.exchange(
                "/api/supplier/" + lastSupplierId, HttpMethod.DELETE, authOnly(), SupplierApi.class
        );
        Assertions.assertEquals(HttpStatus.OK, lastDtoResponse.getStatusCode(), "Delete failed");
        Assertions.assertNotNull(lastDtoResponse.getBody(), "Delete body is null");
        lastSupplierDto = lastDtoResponse.getBody();
    }

    @Then("the response supplier should have name {string}")
    public void response_supplier_has_name(String expected) {
        Assertions.assertNotNull(lastSupplierDto, "Supplier DTO is null");
        Assertions.assertEquals(expected, lastSupplierDto.getName(), "Name mismatch");
        Assertions.assertEquals(lastSupplierId, lastSupplierDto.getId(), "ID mismatch");
    }

    @Then("the suppliers list should have page {int} and pageSize {int}")
    public void list_has_paging(int expectedPage, int expectedSize) {
        ReturnList<SupplierApi> body = lastListResp.getBody();
        Assertions.assertNotNull(body);
        Assertions.assertEquals(expectedPage, body.getPage(), "page mismatch");
        Assertions.assertEquals(expectedSize, body.getPageSize(), "pageSize mismatch");
    }

    @Then("the suppliers list should contain at least {int} elements")
    public void list_has_at_least(int n) {
        ReturnList<SupplierApi> body = lastListResp.getBody();
        Assertions.assertNotNull(body);
        Assertions.assertTrue(body.getTotalElements() >= n, "totalElements < expected");
        Assertions.assertNotNull(body.getData(), "data is null");
        Assertions.assertFalse(body.getData().isEmpty(), "data list empty");
    }

    @Then("the supplier should be marked as disabled")
    public void supplier_is_soft_deleted() {
        var persisted = supplierService.getSupplierById(lastSupplierId);
        Assertions.assertNotNull(persisted, "Persisted supplier not found");
        Assertions.assertFalse(persisted.isEnabled(), "Supplier not soft-deleted (enabled should be false)");

        Assertions.assertFalse(
                Objects.requireNonNull(lastSupplierDto).isEnabled(),
                "Delete response DTO still enabled=true"
        );
    }
}
