package org.example.inventario.stepdefs;

import io.cucumber.java.Before;
import io.cucumber.java.en.*;
import org.example.inventario.model.dto.api.user.UserApi;
import org.example.inventario.model.dto.inventory.ReturnList;
import org.example.inventario.model.dto.scurity.ReturnAuthentication;
import org.example.inventario.model.dto.scurity.UserAuthentication;
import org.example.inventario.model.entity.security.User;
import org.example.inventario.service.security.UserService;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;

import java.util.List;
import java.util.Set;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserStepDefs {

    @Autowired private TestRestTemplate restTemplate;
    @Autowired private UserService userService;

    private String jwtToken;

    private User userPayload;
    private ResponseEntity<UserApi> lastDtoResponse;
    private ResponseEntity<ReturnList<UserApi>> lastListResponse;
    private UserApi lastUserDto;
    private Long lastUserId;

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
        HttpHeaders h = new HttpHeaders();
        h.setBearerAuth(jwtToken);
        h.setContentType(MediaType.APPLICATION_JSON);
        h.setAccept(List.of(MediaType.APPLICATION_JSON));
        return h;
    }
    private HttpEntity<Void> authOnly() { return new HttpEntity<>(authJson()); }

    @Given("a user payload with username {string}, password {string}, email {string}")
    public void user_payload(String username, String password, String email) {
        userPayload = new User();
        userPayload.setUsername(username);
        userPayload.setPassword(password);
        userPayload.setEmail(email);
        userPayload.setEnabled(true);
        userPayload.setRoles(Set.of());
    }

    @When("I create the user")
    public void create_user() {
        HttpEntity<User> req = new HttpEntity<>(userPayload, authJson());
        lastDtoResponse = restTemplate.exchange(
                "/api/user", HttpMethod.POST, req, UserApi.class
        );
        Assertions.assertEquals(HttpStatus.OK, lastDtoResponse.getStatusCode(), "Create user failed");
        Assertions.assertNotNull(lastDtoResponse.getBody(), "Create user returned null body");
        lastUserDto = lastDtoResponse.getBody();
        lastUserId = lastUserDto.getId();
        Assertions.assertNotNull(lastUserId, "Created user ID is null");
    }

    @When("I list users page {int} size {int}")
    public void list_users(int page, int size) {
        String url = String.format("/api/user?page=%d&size=%d", page, size);
        lastListResponse = restTemplate.exchange(
                url, HttpMethod.GET, authOnly(),
                new ParameterizedTypeReference<ReturnList<UserApi>>() {}
        );
        Assertions.assertEquals(HttpStatus.OK, lastListResponse.getStatusCode(), "List users failed");
        Assertions.assertNotNull(lastListResponse.getBody(), "List users body is null");
    }

    @When("I get the user by the last created ID")
    public void get_user_by_last_id() {
        String url = "/api/user/" + lastUserId;
        lastDtoResponse = restTemplate.exchange(url, HttpMethod.GET, authOnly(), UserApi.class);
        Assertions.assertEquals(HttpStatus.OK, lastDtoResponse.getStatusCode(), "Get user by id failed");
        lastUserDto = lastDtoResponse.getBody();
        Assertions.assertNotNull(lastUserDto, "Get user by id body is null");
    }

    @When("I get the user by username {string}")
    public void get_user_by_username(String username) {
        String url = "/api/user/username/" + username;
        lastDtoResponse = restTemplate.exchange(url, HttpMethod.GET, authOnly(), UserApi.class);
        Assertions.assertEquals(HttpStatus.OK, lastDtoResponse.getStatusCode(), "Get user by username failed");
        lastUserDto = lastDtoResponse.getBody();
        Assertions.assertNotNull(lastUserDto, "Get user by username body is null");
    }

    @When("I update the user to username {string} password {string} email {string}")
    public void update_user(String newUsername, String newPassword, String newEmail) {
        User toUpdate = new User();
        toUpdate.setId(lastUserId);
        toUpdate.setUsername(newUsername);
        toUpdate.setPassword(newPassword);
        toUpdate.setEmail(newEmail);
        toUpdate.setEnabled(true);
        toUpdate.setRoles(Set.of());

        HttpEntity<User> req = new HttpEntity<>(toUpdate, authJson());
        lastDtoResponse = restTemplate.exchange(
                "/api/user/" + lastUserId, HttpMethod.PUT, req, UserApi.class
        );
        Assertions.assertEquals(HttpStatus.OK, lastDtoResponse.getStatusCode(), "Update user failed");
        lastUserDto = lastDtoResponse.getBody();
        Assertions.assertNotNull(lastUserDto, "Update user body is null");
    }

    @When("I delete the user by the last created ID")
    public void delete_user_by_last_id() {
        lastDtoResponse = restTemplate.exchange(
                "/api/user/" + lastUserId, HttpMethod.DELETE, authOnly(), UserApi.class
        );
        Assertions.assertEquals(HttpStatus.OK, lastDtoResponse.getStatusCode(), "Delete user failed");
        Assertions.assertNotNull(lastDtoResponse.getBody(), "Delete user body is null");
        lastUserDto = lastDtoResponse.getBody();
    }

    @When("I enable the user by the last created ID")
    public void enable_user_by_last_id() {
        lastDtoResponse = restTemplate.exchange(
                "/api/user/" + lastUserId + "/enable", HttpMethod.PUT, authOnly(), UserApi.class
        );
        Assertions.assertEquals(HttpStatus.OK, lastDtoResponse.getStatusCode(), "Enable user failed");
        Assertions.assertNotNull(lastDtoResponse.getBody(), "Enable user body is null");
        lastUserDto = lastDtoResponse.getBody();
    }

    @Then("the response user should have username {string}")
    public void response_user_has_username(String expectedUsername) {
        Assertions.assertNotNull(lastUserDto, "User DTO is null");
        Assertions.assertEquals(expectedUsername, lastUserDto.getUsername(), "Username mismatch");
        Assertions.assertEquals(lastUserId, lastUserDto.getId(), "User ID mismatch");
    }

    @Then("the users list should have page {int} and pageSize {int}")
    public void users_list_has_paging(int expectedPage, int expectedSize) {
        ReturnList<UserApi> body = lastListResponse.getBody();
        Assertions.assertNotNull(body);
        Assertions.assertEquals(expectedPage, body.getPage(), "page mismatch");
        Assertions.assertEquals(expectedSize, body.getPageSize(), "pageSize mismatch");
    }

    @Then("the users list should contain at least {int} elements")
    public void users_list_has_at_least(int n) {
        ReturnList<UserApi> body = lastListResponse.getBody();
        Assertions.assertNotNull(body);
        Assertions.assertTrue(body.getTotalElements() >= n, "totalElements < expected");
        Assertions.assertNotNull(body.getData(), "data is null");
        Assertions.assertFalse(body.getData().isEmpty(), "data list empty");
    }

    @Then("the user should be disabled")
    public void user_should_be_disabled() {
        var persisted = userService.findById(lastUserId);
        Assertions.assertNotNull(persisted, "Persisted user not found");
        Assertions.assertFalse(persisted.isEnabled(), "User not disabled (enabled should be false)");
    }

    @Then("the user should be enabled")
    public void user_should_be_enabled() {
        var persisted = userService.findById(lastUserId);
        Assertions.assertNotNull(persisted, "Persisted user not found");
        Assertions.assertTrue(persisted.isEnabled(), "User not enabled (enabled should be true)");
    }
}
