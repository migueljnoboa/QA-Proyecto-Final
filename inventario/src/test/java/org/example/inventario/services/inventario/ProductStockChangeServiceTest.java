package org.example.inventario.services.inventario;

import org.example.inventario.model.entity.inventory.Category;
import org.example.inventario.model.entity.inventory.Product;
import org.example.inventario.model.entity.inventory.ProductStockChange;
import org.example.inventario.model.entity.inventory.Supplier;
import org.example.inventario.service.inventory.ProductService;
import org.example.inventario.service.inventory.ProductStockChangeService;
import org.example.inventario.service.inventory.SupplierService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
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
        productService.updateProduct(product1);
        productService.updateProduct(product2);
        productService.updateProduct(product3);

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

    private Product bump(Product p, int delta) {
        Product updated = new Product(
                p.getName(), p.getDescription(), p.getCategory(), p.getPrice(),
                p.getStock() + delta, p.getMinStock(), p.getImage(), p.getSupplier()
        );
        updated.setId(p.getId());
        return productService.updateProduct(updated);
    }

    @Test
    public void testSearchByProductIdAndIncreasedAndAmountRange() {
        // Arrange: generate stock changes
        bump(product1, +5);   // increased, amount=5
        bump(product1, -2);   // decreased, amount=2
        bump(product2, +10);  // increased, amount=10

        // Act: search for product1, increased=true, amount between [3,6]
        Page<ProductStockChange> page = productStockChangeService.searchProductStockChanges(
                PageRequest.of(0, 20),
                product1.getId(),
                true,
                3, 6,
                null, null,
                null
        );

        // Assert: only the +5 change for product1 should match
        assertEquals(1, page.getTotalElements());
        ProductStockChange match = page.getContent().get(0);
        assertEquals(product1.getId(), match.getProduct().getId());
        assertTrue(match.isIncreased());
        assertEquals(5, match.getAmount());
    }

    @Test
    public void testSearchByDateRange() {
        // Arrange
        LocalDateTime t0 = LocalDateTime.now();
        bump(product1, +3);   // should be inside the window
        LocalDateTime t1 = LocalDateTime.now();

        // Act: include window around change
        Page<ProductStockChange> page = productStockChangeService.searchProductStockChanges(
                PageRequest.of(0, 20),
                product1.getId(),
                null,
                null, null,
                t0.minusSeconds(2),
                t1.plusSeconds(2),
                null
        );

        // Assert: found at least one record for product1 with amount=3
        assertTrue(page.getTotalElements() >= 1);
        assertTrue(page.getContent().stream().anyMatch(psc ->
                psc.getProduct().getId().equals(product1.getId()) && psc.getAmount() == 3
        ));
    }

    @Test
    public void testSearchAmountBoundsNullsAndSwap() {
        // Capture a window just for the rows created in this test
        LocalDateTime start = LocalDateTime.now();

        // Arrange: create 3 different (increased) changes for product1 via updateProduct
        bump(product1, +1);
        bump(product1, +4);
        bump(product1, +10);

        LocalDateTime end = LocalDateTime.now();

        // Small guard around DB timestamp precision (DATETIME vs DATETIME(6))
        LocalDateTime from = start.minusSeconds(1);
        LocalDateTime to   = end.plusSeconds(1);

        // 1) Only max (<= 5) within time window
        Page<ProductStockChange> maxOnly = productStockChangeService.searchProductStockChanges(
                PageRequest.of(0, 50),
                product1.getId(),              // productId
                true,                          // increased
                null, 5,                       // min=null, max=5
                from, to,                      // date window to isolate this test
                null                           // createdBy
        );
        assertEquals(2, maxOnly.getTotalElements(), "Expected exactly amounts 1 and 4 for this window");
        assertTrue(maxOnly.getContent().stream().allMatch(psc -> psc.getAmount() <= 5));

        // 2) Only min (>= 5) within time window
        Page<ProductStockChange> minOnly = productStockChangeService.searchProductStockChanges(
                PageRequest.of(0, 50),
                product1.getId(),
                true,
                5, null,                       // min=5, max=null
                from, to,
                null
        );
        assertEquals(1, minOnly.getTotalElements(), "Expected exactly amount 10 for this window");
        assertTrue(minOnly.getContent().stream().allMatch(psc -> psc.getAmount() >= 5));

        // 3) Swapped bounds (10..1) → spec should swap internally; expect [1,4,10]
        Page<ProductStockChange> swapped = productStockChangeService.searchProductStockChanges(
                PageRequest.of(0, 50),
                product1.getId(),
                true,
                10, 1,                         // swapped bounds
                from, to,
                null
        );
        assertEquals(3, swapped.getTotalElements(), "Expected exactly amounts 1, 4, 10 for this window");
        assertTrue(swapped.getContent().stream()
                .allMatch(psc -> psc.getAmount() >= 1 && psc.getAmount() <= 10));
    }
}
