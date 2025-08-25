package org.example.inventario.services.inventario;

import com.vaadin.hilla.mappedtypes.Pageable;
import org.example.inventario.exception.MyException;
import org.example.inventario.model.dto.inventory.ReturnList;
import org.example.inventario.model.entity.Base;
import org.example.inventario.model.entity.inventory.Category;
import org.example.inventario.model.entity.inventory.Product;
import org.example.inventario.model.entity.inventory.Supplier;
import org.example.inventario.repository.inventory.ProductRepository;
import org.example.inventario.service.inventory.ProductService;
import org.example.inventario.service.inventory.SupplierService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.*;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ActiveProfiles("dev")
@SpringBootTest
@Transactional
@Testcontainers
public class ProductServiceTest {

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

    @Autowired
    private SupplierService supplierService;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private ProductService productService;

    private Supplier supplier;

    @BeforeEach
    public void setUp() {}

    private Product product1;
    private Product product2;
    private Product product3;

    @BeforeEach
    public void init() {

        supplier = new Supplier("supplier1","contact Info", "address", "email@email.com", "8099891333");
        product1 = new Product("product1","description", Category.BEAUTY_PRODUCTS, new BigDecimal("20.0"), 5, 2, null, supplier);
        product2 = new Product("product2","description", Category.BEAUTY_PRODUCTS, new BigDecimal("20.0"), 5, 2, null, supplier);
        product3 = new Product("product3","description", Category.BEAUTY_PRODUCTS, new BigDecimal("20.0"), 5, 2, null, supplier);

        productRepository.deleteAll();
    }

    @Test
    public void createProductTest() {

        supplierService.createSupplier(supplier);

        assertThrows(MyException.class, () -> productService.createProduct(null));

        // Check if you can create a product
        assertEquals(product1, productService.createProduct(product1));
        assertEquals(product1, productService.getProductById(product1.getId()));
    }

    @Test
    public void createProductNameTest() {

        // Check if name is null
        product1.setName(null);
        assertThrows(MyException.class, () -> productService.createProduct(null));

        // Check if name is empty
        product1.setName("");
        assertThrows(MyException.class, () -> productService.createProduct(product1));

    }

    @Test
    public void createProductCategoryTest() {

        // Check if name is null
        product1.setCategory(null);
        assertThrows(MyException.class, () -> productService.createProduct(null));

        // Check if name is empty
        product1.setName("");
        assertThrows(MyException.class, () -> productService.createProduct(product1));

    }

    @Test
    public void createProductPriceTest() {

        // Check if name is null
        product1.setPrice(null);
        assertThrows(MyException.class, () -> productService.createProduct(null));

        // Check if name is empty
        product1.setPrice(new BigDecimal("-1.0"));
        assertThrows(MyException.class, () -> productService.createProduct(product1));

    }

    @Test
    public void createProductStockTest() {

        // Check if name is null
        product1.setStock(null);
        assertThrows(MyException.class, () -> productService.createProduct(null));

        // Check if name is empty
        product1.setStock(-1);
        assertThrows(MyException.class, () -> productService.createProduct(product1));

    }

    @Test
    public void createProductMinStockTest() {

        // Check if name is null
        product1.setMinStock(null);
        assertThrows(MyException.class, () -> productService.createProduct(null));

        // Check if name is empty
        product1.setMinStock(-1);
        assertThrows(MyException.class, () -> productService.createProduct(product1));

    }

    @Test
    public void createProductSupplierTest() {

        // Check if name is null
        product1.setSupplier(null);
        assertThrows(MyException.class, () -> productService.createProduct(null));

        // Check if name is empty

        Supplier badSupplier = new  Supplier();
        badSupplier.setId(-1L);

        product1.setSupplier(badSupplier);
        assertThrows(MyException.class, () -> productService.createProduct(product1));

    }

    @Test
    public void updateProduct() {

        supplierService.createSupplier(supplier);
        productService.createProduct(product1);
        product1.setName("test");

        productService.updateProduct(product1);
        assertEquals(product1, productService.getProductById(product1.getId()));

        var oldProduct1Id = product1.getId();
        product1.setId(null);
        assertThrows(MyException.class, () -> productService.updateProduct(product1));
        product1.setId(oldProduct1Id);

        assertThrows(MyException.class, () -> productService.updateProduct(null));

        product1.setName("");
        assertThrows(MyException.class, () -> productService.updateProduct(product1));
        product1.setName(product2.getName());

        product1.setCategory(null);
        assertThrows(MyException.class, () -> productService.updateProduct(product1));
        product1.setCategory(product2.getCategory());

        product1.setPrice(null);
        assertThrows(MyException.class, () -> productService.updateProduct(product1));
        product1.setPrice(product2.getPrice());

        product1.setStock(null);
        assertThrows(MyException.class, () -> productService.updateProduct(product1));
        product1.setStock(product2.getStock());

        product1.setMinStock(null);
        assertThrows(MyException.class, () -> productService.updateProduct(product1));
        product1.setMinStock(product2.getStock());

        product1.setMinStock(null);
        assertThrows(MyException.class, () -> productService.updateProduct(product1));
        product1.setMinStock(product2.getStock());

        product1.setSupplier(null);
        assertThrows(MyException.class, () -> productService.updateProduct(product1));
    }

    @Test
    public void findProductById() {

        supplierService.createSupplier(supplier);

        productService.createProduct(product1);

        assertEquals(product1, productService.getProductById(product1.getId()));

    }

    @Test
    public void findAllProducts() {

        supplierService.createSupplier(supplier);
        productService.createProduct(product1);
        productService.createProduct(product2);
        productService.createProduct(product3);

        ReturnList<Product> productReturnList = productService.getAllProducts(PageRequest.of(0, 20));

        assertTrue(productReturnList.getData().contains(product1));
        assertTrue(productReturnList.getData().contains(product2));
        assertTrue(productReturnList.getData().contains(product3));

    }

    @Test
    public void deleteProduct() {

        assertThrows(MyException.class, () -> productService.deleteProduct(null));
        assertThrows(MyException.class, () -> productService.deleteProduct(0L));

        supplierService.createSupplier(supplier);
        productService.createProduct(product1);
        productService.createProduct(product2);
        productService.createProduct(product3);

        supplierService.deleteSupplier(product1.getId());

        assertFalse(supplierService.getSupplierById(product1.getId()).isEnabled());

    }

    @Test
    public void searchProductsTest() {

        supplierService.createSupplier(supplier);
        productService.createProduct(product1);
        productService.createProduct(product2);
        productService.createProduct(product3);

        List<Product> productList = productService.searchProducts(product1.getName(), null, null, null, null, null, PageRequest.of(0, 10)).stream().toList();
        assertEquals(1, productList.size());

        productList = productService.searchProducts(null, product1.getCategory(), null, null, null, null, PageRequest.of(0, 10)).stream().toList();
        assertEquals(3, productList.size());

        productList = productService.searchProducts(null, null, product1.getPrice(), null, null, null, PageRequest.of(0, 10)).stream().toList();
        assertEquals(3, productList.size());

        productList = productService.searchProducts(null, null, null, product1.getMinStock(), null, null, PageRequest.of(0, 10)).stream().toList();
        assertEquals(3, productList.size());

        productList = productService.searchProducts(null, null, null, null, product1.getStock(), null, PageRequest.of(0, 10)).stream().toList();
        assertEquals(3, productList.size());

    }

    @Test
    public void countAllProductsTest() {

        supplierService.createSupplier(supplier);
        productService.createProduct(product1);
        productService.createProduct(product2);
        productService.createProduct(product3);

        assertEquals(3, productService.countAllProducts());

    }

    @Test
    public void getTotalStockValueTest() {

        supplierService.createSupplier(supplier);
        productService.createProduct(product1);
        productService.createProduct(product2);
        productService.createProduct(product3);

        var total = product1.getStock() * product1.getPrice().doubleValue();
        total += product2.getStock() * product2.getPrice().doubleValue();
        total += product3.getStock() * product3.getPrice().doubleValue();

        assertEquals(0, BigDecimal.valueOf(total).compareTo(productService.getTotalStockValue()));
    }

    @Test
    public void productLowStockTest() {

        supplierService.createSupplier(supplier);
        product1.setStock(1);
        productService.createProduct(product1);
        productService.createProduct(product2);
        productService.createProduct(product3);

        assertEquals(1, productService.productLowStock().size());
    }

    @Test
    public void countProductsByCategoryTest() {

        supplierService.createSupplier(supplier);
        productService.createProduct(product1);
        productService.createProduct(product2);
        productService.createProduct(product3);

        assertEquals(3, productService.countProductsByCategory(product1.getCategory()));
        assertEquals(0, productService.countProductsByCategory(Category.AUTOMOTIVE));

    }

    @Test
    public void getTotalStockValueByCategoryTest() {

        supplierService.createSupplier(supplier);
        productService.createProduct(product1);
        productService.createProduct(product2);
        productService.createProduct(product3);

        var total = product1.getStock() * product1.getPrice().doubleValue();
        total += product2.getStock() * product2.getPrice().doubleValue();
        total += product3.getStock() * product3.getPrice().doubleValue();

        System.out.println("TOTAL VALUE: " +productService.getTotalStockValueByCategory(Category.BEAUTY_PRODUCTS));

        assertEquals(0, BigDecimal.valueOf(total).compareTo(productService.getTotalStockValueByCategory(Category.BEAUTY_PRODUCTS)));
    }

    @Test
    public void getTotalStockValuePercentByCategoryTest() {

        supplierService.createSupplier(supplier);
        productService.createProduct(product1);
        productService.createProduct(product2);
        productService.createProduct(product3);

        assertEquals(Double.valueOf(100), productService.getTotalStockValuePercentByCategory(Category.BEAUTY_PRODUCTS));
    }



}
