package org.example.inventario.stepdefs;

import io.cucumber.java.en.*;
import org.example.inventario.model.entity.inventory.Category;
import org.example.inventario.model.entity.inventory.Product;
import org.example.inventario.model.entity.inventory.Supplier;
import org.example.inventario.model.dto.inventory.ReturnList;
import org.example.inventario.service.inventory.SupplierService;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SupplierStepDefs {

    private Supplier supplier;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private SupplierService supplierService;

    private Supplier responseSupplier;
    private Long lastSupplierId;
    private ResponseEntity<Supplier> responseEntity;


    @Given("a supplier with name {string}, contact info {string}, address {string}, email {string}, and phone {string}")
    public void aSupplierWithNameContactInfoAddressEmailAndPhone(String name, String contactInfo, String address, String email, String phone) {

        supplier = new Supplier();
        supplier.setName(name);
        supplier.setName(contactInfo);
        supplier.setName(address);
        supplier.setName(email);
        supplier.setName(phone);
        supplier.setEnabled(true);
    }

    @When("the supplier is created")
    public void createSupplier() {
        responseEntity = restTemplate.postForEntity("/api/supplier/", supplier, Supplier.class);
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
        restTemplate.getForEntity("/api/supplier", String.class); // just calls it, no need to store now
    }

    @Then("I should receive a list of suppliers")
    public void assertSupplierListReturned() {
        ResponseEntity<String> response = restTemplate.getForEntity("/api/supplier", String.class);
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
        responseEntity = restTemplate.getForEntity("/api/supplier/" + id, Supplier.class);
    }

    @Then("I should get the supplier with ID {int}")
    public void assertSupplierByIdReturned(int id) {
        Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        Assertions.assertEquals(id, responseEntity.getBody().getId());
    }

    @When("I delete the supplier with ID {int}")
    public void deleteSupplierById(int id) {
        restTemplate.delete("/api/supplier/" + id);
    }

    @Then("the supplier should be marked as disabled")
    public void assertSupplierIsDisabled() {
        Supplier supplier = supplierService.getSupplierById(lastSupplierId);
        Assertions.assertNotNull(supplier);
        Assertions.assertFalse(supplier.isEnabled());
    }
}
