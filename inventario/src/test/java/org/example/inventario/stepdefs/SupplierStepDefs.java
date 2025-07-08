package org.example.inventario.stepdefs;

import io.cucumber.java.Before;
import io.cucumber.java.en.*;
import org.example.inventario.model.dto.scurity.ReturnAuthentication;
import org.example.inventario.model.dto.scurity.UserAuthentication;
import org.example.inventario.model.entity.inventory.Supplier;
import org.example.inventario.service.inventory.SupplierService;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SupplierStepDefs {

    private Supplier supplier;
    private String jwtToken;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private SupplierService supplierService;

    private Supplier responseSupplier;
    private Long lastSupplierId;
    private ResponseEntity<Supplier> responseEntity;

    // 🔐 Authenticate once before each scenario
    @Before
    public void authenticate() {
        UserAuthentication credentials = new UserAuthentication("admin", "admin");

        ResponseEntity<ReturnAuthentication> loginResponse = restTemplate.postForEntity(
                "/api/security/authenticate",
                credentials,
                ReturnAuthentication.class
        );

        Assertions.assertEquals(HttpStatus.OK, loginResponse.getStatusCode(), "Login failed");
        Assertions.assertNotNull(loginResponse.getBody());
        jwtToken = loginResponse.getBody().token();
        Assertions.assertNotNull(jwtToken, "JWT token must not be null");
    }

    private HttpHeaders authHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(jwtToken);
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    @Given("a supplier with name {string}, contact info {string}, address {string}, email {string}, and phone {string}")
    public void aSupplierWithNameContactInfoAddressEmailAndPhone(String name, String contactInfo, String address, String email, String phone) {
        supplier = new Supplier();
        supplier.setName(name);
        supplier.setContactInfo(contactInfo);
        supplier.setAddress(address);
        supplier.setEmail(email);
        supplier.setPhoneNumber(phone);
        supplier.setEnabled(true);
    }

    @When("the supplier is created")
    public void createSupplier() {
        HttpEntity<Supplier> request = new HttpEntity<>(supplier, authHeaders());
        responseEntity = restTemplate.postForEntity("/api/supplier/", request, Supplier.class);
        if (responseEntity.getBody() != null) {
            lastSupplierId = responseEntity.getBody().getId();
        }
    }

    @Then("the supplier should be saved and returned with name {string}")
    public void assertSupplierCreated(String expectedName) {
        Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        Assertions.assertNotNull(responseEntity.getBody());
        Assertions.assertEquals(expectedName, responseEntity.getBody().getName());
    }

    @Given("suppliers exist in the database")
    public void suppliersExist() {
        Supplier supplier = new Supplier();
        supplier.setName("Test");
        supplier.setContactInfo("Contact");
        supplier.setAddress("Address");
        supplier.setEmail("test@example.com");
        supplier.setPhoneNumber("00000000");
        supplier.setEnabled(true);
        supplierService.createSupplier(supplier);
    }

    @When("I request all suppliers")
    public void getAllSuppliers() {
        HttpEntity<Void> request = new HttpEntity<>(authHeaders());
        restTemplate.exchange("/api/supplier", HttpMethod.GET, request, String.class);
    }

    @Then("I should receive a list of suppliers")
    public void assertSupplierListReturned() {
        HttpEntity<Void> request = new HttpEntity<>(authHeaders());
        ResponseEntity<String> response = restTemplate.exchange("/api/supplier", HttpMethod.GET, request, String.class);
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertTrue(response.getBody().contains("data"));
    }

    @Given("a supplier with ID {int} exists")
    public void supplierWithIdExists(int id) {
        Supplier supplier = new Supplier();
        supplier.setName("Supplier " + id);
        supplier.setContactInfo("Contact " + id);
        supplier.setAddress("Address " + id);
        supplier.setEmail("email" + id + "@example.com");
        supplier.setPhoneNumber("123456789");
        supplier.setEnabled(true);
        supplierService.createSupplier(supplier);
        lastSupplierId = supplier.getId();
    }

    @When("I request the supplier by ID {int}")
    public void getSupplierById(int id) {
        HttpEntity<Void> request = new HttpEntity<>(authHeaders());
        responseEntity = restTemplate.exchange("/api/supplier/" + id, HttpMethod.GET, request, Supplier.class);
    }

    @Then("I should get the supplier with ID {int}")
    public void assertSupplierByIdReturned(int id) {
        Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        Assertions.assertEquals(id, responseEntity.getBody().getId());
    }

    @When("I delete the supplier with ID {int}")
    public void deleteSupplierById(int id) {
        HttpEntity<Void> request = new HttpEntity<>(authHeaders());
        restTemplate.exchange("/api/supplier/" + id, HttpMethod.DELETE, request, Void.class);
        lastSupplierId = (long) id;
    }

    @Then("the supplier should be marked as disabled")
    public void assertSupplierIsDisabled() {
        Supplier supplier = supplierService.getSupplierById(lastSupplierId);
        Assertions.assertNotNull(supplier);
        Assertions.assertFalse(supplier.isEnabled());
    }
}
