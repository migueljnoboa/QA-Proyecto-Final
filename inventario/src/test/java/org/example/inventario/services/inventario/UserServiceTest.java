package org.example.inventario.services.inventario;

import io.cucumber.java.eo.Se;
import org.example.inventario.configuration.PasswordEncodingConfig;
import org.example.inventario.exception.MyException;
import org.example.inventario.model.dto.inventory.ReturnList;
import org.example.inventario.model.entity.inventory.Supplier;
import org.example.inventario.model.entity.security.Role;
import org.example.inventario.model.entity.security.User;
import org.example.inventario.service.inventory.SupplierService;
import org.example.inventario.service.security.PermitService;
import org.example.inventario.service.security.RoleService;
import org.example.inventario.service.security.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("dev")
@SpringBootTest
@Transactional
@Testcontainers
public class UserServiceTest {

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

    @Autowired private UserService userService;
    @Autowired private RoleService roleService;
    @Autowired private PermitService permitService;
    @Autowired private PasswordEncodingConfig passwordEncodingConfig;

    private Role adminRole;
    private Role userRole;

    private User u1;
    private User u2;

    @BeforeEach
    public void init() {

        adminRole = roleService.findByName(Role.ADMIN_ROLE);
        userRole  = roleService.findByName(Role.USER_ROLE);

        u1 = new User();
        u1.setUsername("alice");
        u1.setPassword("secret");
        u1.setEmail("alice@example.com");
        u1.setRoles(Set.of(userRole));

        u2 = new User();
        u2.setUsername("bob");
        u2.setPassword("hunter2");
        u2.setEmail("bob@example.com");
        u2.setRoles(Set.of(userRole));
    }

    @Test
    void createUser_success_validation_and_duplicates() {
        var saved = userService.createUser(u1);
        assertNotNull(saved.getId());
        assertEquals("alice", saved.getUsername());

        var encoder = passwordEncodingConfig.passwordEncoder();
        assertNotEquals("secret", saved.getPassword());
        assertTrue(encoder.matches("secret", saved.getPassword()));

        assertThrows(MyException.class, () -> userService.createUser(null));

        var bad1 = new User();
        bad1.setUsername("  ");
        bad1.setPassword("p");
        bad1.setRoles(Set.of(userRole));
        assertThrows(MyException.class, () -> userService.createUser(bad1));

        var bad2 = new User();
        bad2.setUsername("someone");
        bad2.setPassword("  ");
        bad2.setRoles(Set.of(userRole));
        assertThrows(MyException.class, () -> userService.createUser(bad2));

        var dup = new User();
        dup.setUsername("alice");
        dup.setPassword("x");
        dup.setEmail("dup@example.com");
        dup.setRoles(Set.of(userRole));
        assertThrows(MyException.class, () -> userService.createUser(dup));
    }

    @Test
    void findById_success_and_errors() {
        var saved = userService.createUser(u1);
        var fetched = userService.findById(saved.getId());
        assertEquals(saved.getUsername(), fetched.getUsername());

        assertThrows(MyException.class, () -> userService.findById(null));
        assertThrows(MyException.class, () -> userService.findById(-1L));
    }

    @Test
    public void findAllUsersTest() {
        userService.createUser(u1);
        userService.createUser(u2);

        var page = userService.findAllUsers(PageRequest.of(0, 100));
        var ids = page.getData().stream().map(User::getId).collect(Collectors.toSet());
        var expected = Set.of(u1.getId(), u2.getId());

        assertTrue(ids.containsAll(expected));
        assertTrue(page.getTotalElements() >= 2);
    }

    @Test
    void findByUsername_success_and_errors() {
        userService.createUser(u1);
        var found = userService.findByUsername("alice");
        assertEquals("alice", found.getUsername());

        assertThrows(MyException.class, () -> userService.findByUsername(null));
        assertThrows(MyException.class, () -> userService.findByUsername("  "));
        assertThrows(MyException.class, () -> userService.findByUsername("nope"));
    }

    @Test
    void updateUser_success_and_conflicts() {
        var saved = userService.createUser(u1);
        userService.createUser(u2);

        var patch = new User();
        patch.setUsername("charlie");
        patch.setPassword("newpass");
        patch.setEmail("charlie@example.com");
        patch.setRoles(Set.of(adminRole));
        patch.setEnabled(true);

        var updated = userService.updateUser(saved.getId(), patch);
        assertEquals("charlie", updated.getUsername());
        assertTrue(passwordEncodingConfig.passwordEncoder().matches("newpass", updated.getPassword()));
        assertEquals(1, updated.getRoles().size());
        assertEquals(Role.ADMIN_ROLE, updated.getRoles().stream().toList().getFirst().getName());
        assertTrue(updated.isEnabled());

        assertThrows(MyException.class, () -> userService.updateUser(saved.getId(), null));

        var badUsername = new User();
        badUsername.setUsername("  ");
        badUsername.setPassword("x");
        badUsername.setRoles(Set.of(userRole));
        assertThrows(MyException.class, () -> userService.updateUser(saved.getId(), badUsername));

        var oldPassword = saved.getPassword();
        var emptyPassword = new User();
        emptyPassword.setUsername(saved.getUsername());
        emptyPassword.setPassword("");
        emptyPassword.setRoles(Set.of(userRole));
        assertEquals(oldPassword, userService.updateUser(saved.getId(), emptyPassword).getPassword());

        var ok = new User();
        ok.setUsername("ok2");
        ok.setPassword("ok2");
        ok.setRoles(Set.of(userRole));
        assertThrows(MyException.class, () -> userService.updateUser(-1L, ok));

        var conflict = new User();
        conflict.setUsername("bob");
        conflict.setPassword("new");
        conflict.setRoles(Set.of(userRole));
        conflict.setEnabled(true);
        assertThrows(MyException.class, () -> userService.updateUser(updated.getId(), conflict));
    }

    @Test
    void deleteUser_soft_delete() {
        var saved = userService.createUser(u1);
        userService.deleteUser(saved.getId());

        var reloaded = userService.findById(saved.getId());
        assertFalse(reloaded.isEnabled());

        assertThrows(MyException.class, () -> userService.findByUsername("alice"));

        assertThrows(MyException.class, () -> userService.deleteUser(null));
        assertThrows(MyException.class, () -> userService.deleteUser(-1L));
    }

    @Test
    void enableUser_sets_enabled_true() {
        var saved = userService.createUser(u1);
        userService.deleteUser(saved.getId()); // set disabled
        var disabled = userService.findById(saved.getId());
        assertFalse(disabled.isEnabled());

        userService.enableUser(saved.getId());
        var enabled = userService.findById(saved.getId());
        assertTrue(enabled.isEnabled());

        assertThrows(MyException.class, () -> userService.enableUser(null));
        assertThrows(MyException.class, () -> userService.enableUser(-99L));
    }

    @Test
    void createDefaultUserIfNotExists_idempotent_and_has_admin_role() {
        userService.createDefaultUserIfNotExists();
        userService.createDefaultUserIfNotExists(); // idempotent

        var admin = userService.findByUsername("admin");
        assertNotNull(admin);
        assertTrue(passwordEncodingConfig.passwordEncoder().matches("admin", admin.getPassword()));
        assertTrue(admin.getRoles().stream().anyMatch(r -> Role.ADMIN_ROLE.equals(r.getName())));
    }

    @Test
    void createTestUser_idempotent_and_assigns_user_role() {
        userService.createTestUser();
        userService.createTestUser(); // idempotent

        var miguel = userService.findByUsername("miguel");
        var carlos = userService.findByUsername("carlos");
        assertNotNull(miguel);
        assertNotNull(carlos);
        assertTrue(miguel.getRoles().stream().anyMatch(r -> Role.USER_ROLE.equals(r.getName())));
        assertTrue(carlos.getRoles().stream().anyMatch(r -> Role.USER_ROLE.equals(r.getName())));
    }
}
