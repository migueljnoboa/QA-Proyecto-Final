package org.example.inventario.services.inventario;

import org.example.inventario.exception.MyException;
import org.example.inventario.model.dto.inventory.ReturnList;
import org.example.inventario.model.entity.inventory.Supplier;
import org.example.inventario.model.entity.security.Permit;
import org.example.inventario.model.entity.security.Role;
import org.example.inventario.service.inventory.SupplierService;
import org.example.inventario.service.security.PermitService;
import org.example.inventario.service.security.RoleService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("dev")
@SpringBootTest
@Transactional
@Testcontainers
public class RoleServiceTest {

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
    private RoleService roleService;

    @Autowired
    private PermitService permitService;

    private Role role1, role2, role3;
    private final Set<Permit> permitList1 = new HashSet<>();
    private final Set<Permit> permitList2 = new HashSet<>();
    private final Set<Permit> permitList3 = new HashSet<>();

    @BeforeEach
    public void init() {

        permitList1.add(permitService.findByName(Permit.SUPPLIERS_MENU));
        permitList1.add(permitService.findByName(Permit.SUPPLIER_CREATE));
        permitList1.add(permitService.findByName(Permit.SUPPLIER_EDIT));
        permitList1.add(permitService.findByName(Permit.SUPPLIER_DELETE));
        permitList1.add(permitService.findByName(Permit.SUPPLIER_VIEW));

        permitList2.add(permitService.findByName(Permit.PRODUCTS_MENU));
        permitList2.add(permitService.findByName(Permit.PRODUCT_CREATE));
        permitList2.add(permitService.findByName(Permit.PRODUCT_EDIT));
        permitList2.add(permitService.findByName(Permit.PRODUCT_DELETE));
        permitList2.add(permitService.findByName(Permit.PRODUCT_VIEW));

        permitList3.add(permitService.findByName(Permit.USERS_MENU));
        permitList3.add(permitService.findByName(Permit.USER_CREATE));
        permitList3.add(permitService.findByName(Permit.USER_EDIT));
        permitList3.add(permitService.findByName(Permit.USER_DELETE));

        role1 = new Role("role1", "role1 description", permitList1, new HashSet<>());
        role2 = new Role("role2", "role2 description", permitList2, new HashSet<>());
        role3 = new Role("role3", "role2 description", permitList3, new HashSet<>());
    }

    @Test
    public void listAllRoleTest() {
        // Make sure default permits exist before using findByName in @BeforeEach
        permitService.createDefaultPermitsIfNotExists();

        roleService.createRole(role1);
        roleService.createRole(role2);
        roleService.createRole(role3);

        var page = roleService.listAllRole(PageRequest.of(0, 10));

        var expectedIds = Set.of(role1.getId(), role2.getId(), role3.getId());
        var actualIds = page.getData().stream().map(Role::getId).collect(Collectors.toSet());

        assertTrue(actualIds.containsAll(expectedIds), "Page must contain all created roles");
    }
}
