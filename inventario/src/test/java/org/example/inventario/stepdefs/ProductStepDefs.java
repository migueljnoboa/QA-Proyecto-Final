package org.example.inventario.stepdefs;

import io.cucumber.java.en.*;
import org.example.inventario.model.entity.inventory.Category;
import org.example.inventario.model.entity.inventory.Product;
import org.example.inventario.model.entity.inventory.Supplier;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource("classpath:application-dev.properties")
public class ProductStepDefs {

    @Autowired
    private TestRestTemplate restTemplate;

    private Product product;
    private Product responseProduct;
    private Long lastProductId;
    private ResponseEntity<Product> responseEntity;

    private Supplier createSupplier(Long id) {
        Supplier supplier = new Supplier();
        supplier.setId(id);
        return supplier;
    }

    @Given("a new product with name {string}, description {string}, price {double}, stock {int}, minStock {int}, supplier id {long}")
    public void a_new_product(String name, String description, Double price, Integer stock, Integer minStock, Long supplierId) {
        product = new Product();
        product.setName(name);
        product.setDescription(description);
        product.setCategory(Category.BEAUTY_PRODUCTS);
        product.setPrice(BigDecimal.valueOf(price));
        product.setStock(stock);
        product.setMinStock(minStock);
        product.setSupplier(createSupplier(supplierId));
        product.setEnabled(true);
    }

    @When("I send a create product request")
    public void i_send_create_product_request() {
        responseEntity = restTemplate.postForEntity("/product/", product, Product.class);
        responseProduct = responseEntity.getBody();
        if (responseProduct != null) lastProductId = responseProduct.getId();
    }

    @Then("the response product should have name {string}")
    public void the_response_product_should_have_name(String expectedName) {
        Assertions.assertNotNull(responseProduct);
        Assertions.assertEquals(expectedName, responseProduct.getName());
    }

    @Then("the product ID should not be null")
    public void the_product_id_should_not_be_null() {
        Assertions.assertNotNull(lastProductId);
    }

    @Given("a product exists with name {string}, description {string}, price {double}, stock {int}, minStock {int}, supplier id {long}")
    public void a_product_exists(String name, String description, Double price, Integer stock, Integer minStock, Long supplierId) {
        a_new_product(name, description, price, stock, minStock, supplierId);
        i_send_create_product_request();
    }

    @When("I get the product by ID")
    public void i_get_product_by_id() {
        responseEntity = restTemplate.getForEntity("/product/" + lastProductId, Product.class);
        responseProduct = responseEntity.getBody();
    }

    @When("I delete the product by ID")
    public void i_delete_product_by_id() {
        restTemplate.delete("/product/" + lastProductId);
    }

    @Then("getting the product by ID should return an error {string}")
    public void getting_product_by_id_should_return_error(String expectedMessage) {
        try {
            restTemplate.getForEntity("/product/" + lastProductId, Product.class);
            Assertions.fail("Expected error but got success");
        } catch (Exception e) {
            Assertions.assertTrue(e.getMessage().contains(expectedMessage));
        }
    }
}
