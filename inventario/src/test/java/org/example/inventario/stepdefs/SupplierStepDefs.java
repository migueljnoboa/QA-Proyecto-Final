package org.example.inventario.stepdefs;

import io.cucumber.java.en.*;
import org.example.inventario.model.entity.inventory.Supplier;
import org.example.inventario.model.dto.inventory.ReturnList;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource("classpath:application-dev.properties")
public class SupplierStepDefs {

    @Autowired
    private TestRestTemplate restTemplate;

    private Supplier supplier;
    private Supplier responseSupplier;
    private Long lastSupplierId;
    private ResponseEntity<Supplier> responseEntity;
    private ResponseEntity<String> errorResponse;

    @Given("a new supplier with name {string}, contactInfo {string}, address {string}, email {string}, phoneNumber {string}")
    public void a_new_supplier(String name, String contactInfo, String address, String email, String phoneNumber) {
        supplier = new Supplier();
        supplier.setName(name);
        supplier.setContactInfo(contactInfo);
        supplier.setAddress(address);
        supplier.setEmail(email);
        supplier.setPhoneNumber(phoneNumber);
        supplier.setEnabled(true);
    }

    @When("I send a create supplier request")
    public void i_send_create_supplier_request() {
        responseEntity = restTemplate.postForEntity("/supplier/", supplier, Supplier.class);
        responseSupplier = responseEntity.getBody();
        if (responseSupplier != null) lastSupplierId = responseSupplier.getId();
    }

    @Then("the response supplier should have name {string}")
    public void the_response_supplier_should_have_name(String expectedName) {
        Assertions.assertNotNull(responseSupplier);
        Assertions.assertEquals(expectedName, responseSupplier.getName());
    }

    @Then("the supplier ID should not be null")
    public void the_supplier_id_should_not_be_null() {
        Assertions.assertNotNull(lastSupplierId);
    }

    @Given("a supplier exists with name {string}, contactInfo {string}, address {string}, email {string}, phoneNumber {string}")
    public void a_supplier_exists(String name, String contactInfo, String address, String email, String phoneNumber) {
        a_new_supplier(name, contactInfo, address, email, phoneNumber);
        i_send_create_supplier_request();
    }

    @When("I get the supplier by ID")
    public void i_get_supplier_by_id() {
        responseEntity = restTemplate.getForEntity("/supplier/" + lastSupplierId, Supplier.class);
        responseSupplier = responseEntity.getBody();
    }

    @When("I delete the supplier by ID")
    public void i_delete_supplier_by_id() {
        restTemplate.delete("/supplier/" + lastSupplierId);
    }

    @Then("getting the supplier by ID should return an error {string}")
    public void getting_supplier_by_id_should_return_error(String expectedMessage) {
        try {
            restTemplate.getForEntity("/supplier/" + lastSupplierId, Supplier.class);
            Assertions.fail("Expected error but got success");
        } catch (Exception e) {
            Assertions.assertTrue(e.getMessage().contains(expectedMessage));
        }
    }
}
