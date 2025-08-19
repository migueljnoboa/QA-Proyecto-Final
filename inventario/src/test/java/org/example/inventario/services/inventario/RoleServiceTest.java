package org.example.inventario.services.inventario;

import org.example.inventario.exception.MyException;
import org.example.inventario.model.dto.inventory.ReturnList;
import org.example.inventario.model.entity.inventory.Supplier;
import org.example.inventario.model.entity.security.Permit;
import org.example.inventario.model.entity.security.Role;
import org.example.inventario.service.inventory.ProductService;
import org.example.inventario.service.inventory.SupplierService;
import org.example.inventario.service.security.PermitService;
import org.example.inventario.service.security.RoleService;
import org.example.inventario.service.security.UserService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.*;
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
    @Autowired
    private UserService userService;

    private Role role1, role2, role3;

    @BeforeEach
    void setUp() {
        permitService.createDefaultPermitsIfNotExists();

        var p = permitService;
        var set1 = new LinkedHashSet<>(List.of(
                p.findByName(Permit.SUPPLIERS_MENU),
                p.findByName(Permit.SUPPLIER_CREATE),
                p.findByName(Permit.SUPPLIER_EDIT),
                p.findByName(Permit.SUPPLIER_DELETE),
                p.findByName(Permit.SUPPLIER_VIEW)
        ));
        var set2 = new LinkedHashSet<>(List.of(
                p.findByName(Permit.PRODUCTS_MENU),
                p.findByName(Permit.PRODUCT_CREATE),
                p.findByName(Permit.PRODUCT_EDIT),
                p.findByName(Permit.PRODUCT_DELETE),
                p.findByName(Permit.PRODUCT_VIEW)
        ));
        var set3 = new LinkedHashSet<>(List.of(
                p.findByName(Permit.USERS_MENU),
                p.findByName(Permit.USER_CREATE),
                p.findByName(Permit.USER_EDIT),
                p.findByName(Permit.USER_DELETE)
        ));

        role1 = new Role("role1", "role1 description", set1, new LinkedHashSet<>());
        role2 = new Role("role2", "role2 description", set2, new LinkedHashSet<>());
        role3 = new Role("role3", "role3 description", set3, new LinkedHashSet<>());
    }

    @Test
    public void listAllRoleTest() {
        roleService.createRole(role1);
        roleService.createRole(role2);
        roleService.createRole(role3);

        var page = roleService.listAllRole(PageRequest.of(0, 10));
        var ids = page.getData().stream().map(Role::getId).collect(Collectors.toSet());
        var expected = Set.of(role1.getId(), role2.getId(), role3.getId());

        assertTrue(ids.containsAll(expected));
        assertTrue(page.getTotalElements() >= 3);
    }

    @Test
    void findById_success_and_not_found() {
        var saved = roleService.createRole(role1);
        var fetched = roleService.findById(saved.getId());

        assertEquals(saved.getId(), fetched.getId());
        assertThrows(MyException.class, () -> roleService.findById(null));
        assertThrows(MyException.class, () -> roleService.findById(-1L));
    }

    @Test
    void createDefaultRolesIfNotExists_idempotent_and_creates_admin_user_roles() {
        roleService.createDefaultRolesIfNotExists();
        assertEquals(Role.ADMIN_ROLE, roleService.findByName(Role.ADMIN_ROLE).getName());
        assertEquals(Role.USER_ROLE, roleService.findByName(Role.USER_ROLE).getName());
    }

    @Test
    void findByName_success_and_validation() {
        roleService.createRole(role1);
        var found = roleService.findByName(role1.getName());
        assertNotNull(found);
        assertEquals(role1.getName(), found.getName());

        assertThrows(MyException.class, () -> roleService.findByName(null));
        assertThrows(MyException.class, () -> roleService.findByName(""));
        assertNull(roleService.findByName("does-not-exist"));
    }

    @Test
    void createRole_success_and_validation() {
        var saved = roleService.createRole(role1);
        assertNotNull(saved.getId());
        assertEquals(role1.getName(), saved.getName());
        assertFalse(saved.getPermits().isEmpty());

        assertThrows(MyException.class, () -> roleService.createRole(null));

        var bad = new Role();
        bad.setName("  ");
        bad.setPermits(role1.getPermits());
        assertThrows(MyException.class, () -> roleService.createRole(bad));

        var noPermits = new Role("x", "x", new LinkedHashSet<>(), new LinkedHashSet<>());
        assertThrows(MyException.class, () -> roleService.createRole(noPermits));

        var dup = new Role(role1.getName(), "dup", role1.getPermits(), new LinkedHashSet<>());
        assertThrows(MyException.class, () -> roleService.createRole(dup));
    }

    @Test
    void updateRole_success_and_errors() {
        var saved = roleService.createRole(role1);

        role2.setEnabled(true);
        var updated = roleService.updateRole(saved.getId(), role2);
        assertEquals(role2.getName(), updated.getName());
        assertEquals(role2.getDescription(), updated.getDescription());
        assertEquals(role2.isEnabled(), updated.isEnabled());
        assertFalse(updated.getPermits().isEmpty());

        assertThrows(MyException.class, () -> roleService.updateRole(saved.getId(), null));

        var blank = new Role(" ", "desc", role2.getPermits(), new LinkedHashSet<>());
        assertThrows(MyException.class, () -> roleService.updateRole(saved.getId(), blank));

        assertThrows(MyException.class, () -> roleService.updateRole(-1L, role2));

        var createdOther = roleService.createRole(role3);
        var conflict = new Role(createdOther.getName(), "desc", role2.getPermits(), new LinkedHashSet<>());
        assertThrows(MyException.class, () -> roleService.updateRole(updated.getId(), conflict));
    }

    @Test
    void extractPermits_success_and_not_found() {
        roleService.createRole(role1);

        var permits = roleService.extractPermits(role1);
        assertEquals(role1.getPermits().size(), permits.size());

        var bogus = new Permit();
        bogus.setName("NO_SUCH_PERMIT");
        var tmp = new Role("tmp", "tmp", new LinkedHashSet<>(role1.getPermits()), new LinkedHashSet<>());
        tmp.getPermits().add(bogus);
        assertThrows(MyException.class, () -> roleService.extractPermits(tmp));
    }

    @Test
    void deleteRole_soft_delete() {
        var saved = roleService.createRole(role1);
        var deleted = roleService.deleteRole(saved.getId());

        assertFalse(deleted.isEnabled());

        ReturnList<Role> page = roleService.listAllRole(PageRequest.of(0, 50));
        var ids = page.getData().stream().map(Role::getId).collect(Collectors.toSet());
        assertFalse(ids.contains(saved.getId()));

        assertThrows(MyException.class, () -> roleService.deleteRole(null));
        assertThrows(MyException.class, () -> roleService.deleteRole(-1L));
    }

    @Test
    void searchRole_by_name_and_permit() {
        var saved1 = roleService.createRole(role1);
        var saved2 = roleService.createRole(role2);
        roleService.createRole(role3);

        Page<Role> byName = roleService.searchRole(saved1.getName(), null, PageRequest.of(0, 10));
        assertTrue(byName.getContent().stream().anyMatch(r -> r.getId().equals(saved1.getId())));

        var onePermitFromRole2 = role2.getPermits().iterator().next();
        Page<Role> byPermit = roleService.searchRole(null, onePermitFromRole2, PageRequest.of(0, 10));
        assertTrue(byPermit.getContent().stream().anyMatch(r -> r.getId().equals(saved2.getId())));

        roleService.deleteRole(saved2.getId());
        Page<Role> afterDelete = roleService.searchRole(null, onePermitFromRole2, PageRequest.of(0, 10));
        assertFalse(afterDelete.getContent().stream().anyMatch(r -> r.getId().equals(saved2.getId())));
    }




}
