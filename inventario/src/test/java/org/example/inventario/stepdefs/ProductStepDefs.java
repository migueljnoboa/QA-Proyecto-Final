package org.example.inventario.stepdefs;

import io.cucumber.java.en.*;
import org.example.inventario.model.entity.inventory.Category;
import org.example.inventario.model.entity.inventory.Product;
import org.example.inventario.service.inventory.ProductService;
import org.example.inventario.service.inventory.SupplierService;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import java.math.BigDecimal;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ProductStepDefs {

    private Product product;

    @Autowired
    private ProductService productService;

    @Autowired
    private SupplierService supplierService;

    @Autowired
    private TestRestTemplate restTemplate;

    private Product responseProduct;
    private Long lastProductId;
    private ResponseEntity<Product> responseEntity;

    // Get product by Id
    @Given("a product exists with name {string}, description {string}, category {string}, price {double}, stock {int}, minStock {int}, supplier Id {int}")
    public void a_product_exists(String name, String description, String category, Double price, Integer stock, Integer minStock, Integer supplierId) {

        product = new Product();
        product.setName(name);
        product.setDescription(description);
        product.setCategory(Category.valueOf(category));
        product.setPrice(BigDecimal.valueOf(price));
        product.setStock(stock);
        product.setMinStock(minStock);
        product.setSupplier(supplierService.getSupplierById(Long.valueOf(supplierId)));

        productService.createProduct(product);

        lastProductId = product.getId();
    }

    @When("I get the product by ID")
    public void i_get_product_by_id() {
        String url = "/api/product/" + lastProductId;
        responseEntity = restTemplate.getForEntity(url, Product.class);
        responseProduct = responseEntity.getBody();

        System.out.println("[DEBUG] Response product: " + responseProduct);
        System.out.println("[DEBUG] Status code: " + responseEntity.getStatusCode());
    }

    @Then("the response product should have name {string}")
    public void the_response_product_should_have_name(String expectedName) {
        Assertions.assertNotNull(responseProduct);
        Assertions.assertEquals(expectedName, responseProduct.getName());
    }

    @When("I update the product name to {string}")
    public void iUpdateTheProductNameTo(String arg0) {

        product.setName(arg0);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Product> request = new HttpEntity<>(product, headers);

        responseEntity = restTemplate.exchange(
                "/api/product/" + lastProductId,
                HttpMethod.PUT,
                request,
                Product.class
        );

        responseProduct = responseEntity.getBody();
    }

    @When("I delete the product")
    public void i_delete_the_product() {
        restTemplate.delete("/api/product/" + lastProductId);
        responseProduct = productService.getProductById(lastProductId); // should be disabled
    }

    @Then("the product should be marked as disabled")
    public void the_product_should_be_marked_as_disabled() {
        Assertions.assertFalse(responseProduct.isEnabled());
    }


}
