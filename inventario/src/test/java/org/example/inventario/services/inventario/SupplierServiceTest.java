package org.example.inventario.services.inventario;

import com.vaadin.hilla.mappedtypes.Pageable;
import org.example.inventario.exception.MyException;
import org.example.inventario.model.dto.inventory.ReturnList;
import org.example.inventario.model.entity.inventory.Supplier;
import org.example.inventario.repository.inventory.SupplierRepository;
import org.example.inventario.service.inventory.SupplierService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ActiveProfiles("dev")
@SpringBootTest
@Transactional
@Testcontainers
public class SupplierServiceTest {

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

    private Supplier supplier1;
    private Supplier supplier2;
    private Supplier supplier3;

    @BeforeEach
    public void init() {
        supplier1 = new Supplier("supplier1","contact Info", "address", "email@email.com", "8099891333");
        supplier2 = new Supplier("supplier2","contact Info", "address", "email@email.com", "8099891333");
        supplier3 = new Supplier("supplier3","contact Info", "address", "email@email.com", "8099891333");
    }

    @Test
    public void createSupplierTest() {

        // Check if supplier is null
        assertThrows(MyException.class, () -> supplierService.createSupplier(null));

        // Check if you can create a supplier
        assertEquals(supplier1, supplierService.createSupplier(supplier1));
        assertEquals(supplier1, supplierService.getSupplierById(supplier1.getId()));
    }

    @Test
    public void createSupplierNameTest() {

        // Check if name is null
        supplier1.setName(null);
        assertThrows(MyException.class, () -> supplierService.createSupplier(null));

        // Check if name is empty
        supplier1.setName("");
        assertThrows(MyException.class, () -> supplierService.createSupplier(supplier1));

    }

    @Test
    public void createSupplierContactInfoTest() {

        // Check if name is null
        supplier1.setContactInfo(null);
        assertThrows(MyException.class, () -> supplierService.createSupplier(null));

        // Check if name is empty
        supplier1.setContactInfo("");
        assertThrows(MyException.class, () -> supplierService.createSupplier(supplier1));

    }

    @Test
    public void createSupplierAddressTest() {

        // Check if name is null
        supplier1.setAddress(null);
        assertThrows(MyException.class, () -> supplierService.createSupplier(null));

        // Check if name is empty
        supplier1.setAddress("");
        assertThrows(MyException.class, () -> supplierService.createSupplier(supplier1));

    }

    @Test
    public void createSupplierEmailTest() {

        // Check if name is null
        supplier1.setEmail(null);
        assertThrows(MyException.class, () -> supplierService.createSupplier(null));

        // Check if name is empty
        supplier1.setEmail("");
        assertThrows(MyException.class, () -> supplierService.createSupplier(supplier1));

    }

    @Test
    public void createSupplierPhoneNumberTest() {

        // Check if name is null
        supplier1.setPhoneNumber(null);
        assertThrows(MyException.class, () -> supplierService.createSupplier(null));

        // Check if name is empty
        supplier1.setPhoneNumber("");
        assertThrows(MyException.class, () -> supplierService.createSupplier(supplier1));

    }

    @Test
    public void updateSupplier() {

        // Check if supplier is null
        assertThrows(MyException.class, () -> supplierService.updateSupplier(null, null));

        supplierService.createSupplier(supplier1);

        // Check the correct functionality
        supplier1.setName("New name");
        assertEquals(supplier1, supplierService.updateSupplier(supplier1.getId(), supplier1));

        assertThrows(MyException.class, () -> supplierService.updateSupplier(supplier1.getId(), null));
        assertThrows(MyException.class, () -> supplierService.updateSupplier(null, supplier1));

        supplier1.setName("");
        assertThrows(MyException.class, () -> supplierService.updateSupplier(supplier1.getId(), supplier1));
        supplier1.setName(supplier2.getName());

        supplier1.setContactInfo("");
        assertThrows(MyException.class, () -> supplierService.updateSupplier(supplier1.getId(), supplier1));
        supplier1.setContactInfo(supplier2.getContactInfo());

        supplier1.setAddress("");
        assertThrows(MyException.class, () -> supplierService.updateSupplier(supplier1.getId(), supplier1));
        supplier1.setAddress(supplier2.getAddress());

        supplier1.setEmail("");
        assertThrows(MyException.class, () -> supplierService.updateSupplier(supplier1.getId(), supplier1));
        supplier1.setEmail(supplier2.getEmail());

        supplier1.setPhoneNumber("");
        assertThrows(MyException.class, () -> supplierService.updateSupplier(supplier1.getId(), supplier1));


    }

    @Test
    public void findSupplierById() {

        supplierService.createSupplier(supplier1);
        supplierService.createSupplier(supplier2);
        supplierService.createSupplier(supplier3);

        assertEquals(supplier2, supplierService.getSupplierById(supplier2.getId()));

        assertThrows(MyException.class, () -> supplierService.getSupplierById(null));

    }

    @Test
    public void findAllSupplierTest() {

        supplierService.createSupplier(supplier1);
        supplierService.createSupplier(supplier2);
        supplierService.createSupplier(supplier3);

        ReturnList<Supplier> supplierReturnList = supplierService.getAllSuppliers(PageRequest.of(0, 20));

        var ids = supplierReturnList.getData().stream().map(Supplier::getId).toList();
        assertTrue(ids.containsAll(List.of(supplier1.getId(), supplier2.getId(), supplier3.getId())));
    }

    @Test
    public void deleteSupplier() {

        assertThrows(MyException.class, () -> supplierService.deleteSupplier(null));
        assertThrows(MyException.class, () -> supplierService.deleteSupplier(0L));

        supplierService.createSupplier(supplier1);
        supplierService.createSupplier(supplier2);
        supplierService.createSupplier(supplier3);

        supplierService.deleteSupplier(supplier1.getId());

        assertFalse(supplierService.getSupplierById(supplier1.getId()).isEnabled());

    }
}
