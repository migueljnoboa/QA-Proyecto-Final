package org.example.inventario.services.inventario;

import org.example.inventario.exception.MyException;
import org.example.inventario.model.dto.inventory.ReturnList;
import org.example.inventario.model.entity.inventory.Category;
import org.example.inventario.model.entity.inventory.Product;
import org.example.inventario.model.entity.inventory.ProductStockChange;
import org.example.inventario.model.entity.inventory.Supplier;
import org.example.inventario.service.inventory.ProductService;
import org.example.inventario.service.inventory.ProductStockChangeService;
import org.example.inventario.service.inventory.SupplierService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("dev")
@SpringBootTest
@Testcontainers
public class ProductStockChangeServiceTest {

    @Container
    public static MySQLContainer<?> mysqlContainer = new MySQLContainer<>("mysql:8.0.33")
            .withDatabaseName("testdb")
            .withUsername("testuser")
            .withPassword("testpass");

    // Override Spring properties to use the container's JDBC URL and credentials
    @DynamicPropertySource
    static void overrideDatasourceProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mysqlContainer::getJdbcUrl);
        registry.add("spring.datasource.username", mysqlContainer::getUsername);
        registry.add("spring.datasource.password", mysqlContainer::getPassword);
    }

    @Autowired private ProductService productService;
    @Autowired private SupplierService supplierService;
    @Autowired private ProductStockChangeService productStockChangeService;

    private Product product1;
    private Product product2;
    private Product product3;
    private Supplier supplier;

    @BeforeEach
    public void init() {

        supplier = new Supplier("supplier1","contact Info", "address", "email@email.com", "8099891333");
        product1 = new Product("product1","description", Category.BEAUTY_PRODUCTS, new BigDecimal("20.0"), 5, 2, null, supplier);
        product2 = new Product("product2","description", Category.BEAUTY_PRODUCTS, new BigDecimal("20.0"), 5, 2, null, supplier);
        product3 = new Product("product3","description", Category.BEAUTY_PRODUCTS, new BigDecimal("20.0"), 5, 2, null, supplier);

        supplierService.createSupplier(supplier);
        productService.createProduct(product1);
        productService.createProduct(product2);
        productService.createProduct(product3);
    }

    @Test
    public void testFindAllAndId() {

        var returnListOld = productStockChangeService.getAll(
                PageRequest.of(0, 1000, Sort.by("id").ascending())
        );

        product1.setStock(product1.getStock() + 5);
        product2.setStock(product2.getStock() - 5);
        product3.setStock(product3.getStock() + 5);
        productService.updateProduct(product1.getId(), product1);
        productService.updateProduct(product2.getId(), product2);
        productService.updateProduct(product3.getId(), product3);

        var returnListNew = productStockChangeService.getAll(
                PageRequest.of(0, 1000, Sort.by("id").ascending())
        );

        assertEquals(3, returnListNew.getTotalElements() - returnListOld.getTotalElements());

        ProductStockChange productStockChange = productStockChangeService.getById(returnListNew.getData().getLast().getId());

        assertNotNull(productStockChange);
        assertNotNull(productStockChange.getDate());
        assertEquals(5, productStockChange.getAmount());
        assertTrue(productStockChange.isIncreased());
        assertEquals(product3.getId(),productStockChange.getProduct().getId());
        assertNotEquals("", productStockChange.getCreatedBy());
    }
}
