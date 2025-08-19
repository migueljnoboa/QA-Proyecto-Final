package org.example.inventario.stepdefs;

import io.cucumber.java.Before;
import io.cucumber.java.en.*;
import org.example.inventario.model.dto.api.role.RoleApi;
import org.example.inventario.model.dto.inventory.ReturnList;
import org.example.inventario.model.dto.scurity.ReturnAuthentication;
import org.example.inventario.model.dto.scurity.UserAuthentication;
import org.example.inventario.model.entity.security.Permit;
import org.example.inventario.model.entity.security.Role;
import org.example.inventario.service.security.PermitService;
import org.example.inventario.service.security.RoleService;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;

import java.util.*;
import java.util.stream.Collectors;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RoleStepDefs {

    @Autowired private TestRestTemplate restTemplate;
    @Autowired private PermitService permitService;
    @Autowired private RoleService roleService;

    private String jwtToken;

    private Role rolePayload;
    private ResponseEntity<RoleApi> lastDtoResponse;
    private ResponseEntity<ReturnList<RoleApi>> lastListResp;
    private RoleApi lastRoleDto;
    private Long lastRoleId;

    @Before
    public void authenticate() {
        var credentials = new UserAuthentication("admin", "admin");
        var loginResponse = restTemplate.postForEntity(
                "/api/security/authenticate", credentials, ReturnAuthentication.class
        );
        Assertions.assertEquals(HttpStatus.OK, loginResponse.getStatusCode(), "Login failed");
        Assertions.assertNotNull(loginResponse.getBody(), "Login response is null");
        jwtToken = loginResponse.getBody().token();
        Assertions.assertNotNull(jwtToken, "JWT token must not be null");
    }

    private HttpHeaders authJson() {
        var h = new HttpHeaders();
        h.setBearerAuth(jwtToken);
        h.setContentType(MediaType.APPLICATION_JSON);
        return h;
    }
    private HttpEntity<Void> authOnly() { return new HttpEntity<>(authJson()); }

    @Given("a role payload with name {string}, description {string}, permits {string}")
    public void role_payload(String name, String description, String permitsString) {
        Set<Permit> permits = Arrays.stream(permitsString.split(";"))
                .map(String::trim)
                .filter(s -> !s.isBlank())
                .map(pn -> {
                    Permit p = new Permit();
                    p.setName(pn);
                    return p;
                })
                .collect(Collectors.toCollection(LinkedHashSet::new));

        rolePayload = new Role();
        rolePayload.setName(name);
        rolePayload.setDescription(description);
        rolePayload.setPermits(permits);
        rolePayload.setEnabled(true);
    }

    @When("I create the role")
    public void create_role() {
        HttpEntity<Role> req = new HttpEntity<>(rolePayload, authJson());
        lastDtoResponse = restTemplate.exchange(
                "/api/role/", HttpMethod.POST, req, RoleApi.class
        );
        Assertions.assertEquals(HttpStatus.OK, lastDtoResponse.getStatusCode(), "Create role failed");
        Assertions.assertNotNull(lastDtoResponse.getBody(), "Create role returned null body");
        lastRoleDto = lastDtoResponse.getBody();
        lastRoleId = lastRoleDto.getId();
        Assertions.assertNotNull(lastRoleId, "Created role ID is null");
    }

    @When("I list roles page {int} size {int}")
    public void list_roles(int page, int size) {
        String url = String.format("/api/role?page=%d&size=%d", page, size);
        lastListResp = restTemplate.exchange(
                url, HttpMethod.GET, authOnly(),
                new ParameterizedTypeReference<ReturnList<RoleApi>>() {}
        );
        Assertions.assertEquals(HttpStatus.OK, lastListResp.getStatusCode(), "List roles failed");
        Assertions.assertNotNull(lastListResp.getBody(), "List roles body is null");
    }

    @When("I get the role by the last created ID")
    public void get_role_by_last_id() {
        String url = "/api/role/" + lastRoleId;
        lastDtoResponse = restTemplate.exchange(url, HttpMethod.GET, authOnly(), RoleApi.class);
        Assertions.assertEquals(HttpStatus.OK, lastDtoResponse.getStatusCode(), "Get role by id failed");
        lastRoleDto = lastDtoResponse.getBody();
        Assertions.assertNotNull(lastRoleDto, "Get role by id body is null");
    }

    @When("I update the role name to {string}")
    public void update_role_name(String newName) {
        Role toUpdate = new Role();
        toUpdate.setId(lastRoleId);
        toUpdate.setName(newName);
        toUpdate.setDescription(
                rolePayload != null ? rolePayload.getDescription() : "Updated description"
        );
        toUpdate.setEnabled(true);
        Set<Permit> permitsCopy = (rolePayload != null ? rolePayload.getPermits() : Set.<Permit>of())
                .stream()
                .map(p -> {
                    Permit np = new Permit();
                    np.setName(p.getName());
                    return np;
                })
                .collect(Collectors.toCollection(LinkedHashSet::new));
        toUpdate.setPermits(permitsCopy);

        HttpEntity<Role> req = new HttpEntity<>(toUpdate, authJson());
        lastDtoResponse = restTemplate.exchange(
                "/api/role/" + lastRoleId, HttpMethod.PUT, req, RoleApi.class
        );
        Assertions.assertEquals(HttpStatus.OK, lastDtoResponse.getStatusCode(), "Update role failed");
        lastRoleDto = lastDtoResponse.getBody();
        Assertions.assertNotNull(lastRoleDto, "Update role body is null");
    }

    @When("I delete the role by the last created ID")
    public void delete_role_by_last_id() {
        lastDtoResponse = restTemplate.exchange(
                "/api/role/" + lastRoleId, HttpMethod.DELETE, authOnly(), RoleApi.class
        );
        Assertions.assertEquals(HttpStatus.OK, lastDtoResponse.getStatusCode(), "Delete role failed");
        Assertions.assertNotNull(lastDtoResponse.getBody(), "Delete role body is null");
        lastRoleDto = lastDtoResponse.getBody();
    }

    @Then("the response role should have name {string}")
    public void response_role_has_name(String expected) {
        Assertions.assertNotNull(lastRoleDto, "Role DTO is null");
        Assertions.assertEquals(expected, lastRoleDto.getName(), "Role name mismatch");
        Assertions.assertEquals(lastRoleId, lastRoleDto.getId(), "Role ID mismatch");
        Assertions.assertNotNull(lastRoleDto.getPermits(), "Role permits list is null");
    }

    @Then("the roles list should have page {int} and pageSize {int}")
    public void roles_list_has_paging(int expectedPage, int expectedSize) {
        ReturnList<RoleApi> body = lastListResp.getBody();
        Assertions.assertNotNull(body);
        Assertions.assertEquals(expectedPage, body.getPage(), "page mismatch");
        Assertions.assertEquals(expectedSize, body.getPageSize(), "pageSize mismatch");
    }

    @Then("the roles list should contain at least {int} elements")
    public void roles_list_has_at_least(int n) {
        ReturnList<RoleApi> body = lastListResp.getBody();
        Assertions.assertNotNull(body);
        Assertions.assertTrue(body.getTotalElements() >= n, "totalElements < expected");
        Assertions.assertNotNull(body.getData(), "data is null");
        Assertions.assertFalse(body.getData().isEmpty(), "data list empty");
    }

    @Then("the role should be marked as disabled")
    public void role_is_soft_deleted() {
        var persisted = roleService.findById(lastRoleId);
        Assertions.assertNotNull(persisted, "Persisted role not found");
        Assertions.assertFalse(persisted.isEnabled(), "Role not soft-deleted (enabled should be false)");
    }
}
