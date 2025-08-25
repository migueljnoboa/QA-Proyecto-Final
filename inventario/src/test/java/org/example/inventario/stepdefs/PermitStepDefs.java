package org.example.inventario.stepdefs;

import io.cucumber.java.Before;
import io.cucumber.java.en.*;
import org.example.inventario.model.dto.api.permit.PermitApi;
import org.example.inventario.model.dto.scurity.ReturnAuthentication;
import org.example.inventario.model.dto.scurity.UserAuthentication;
import org.example.inventario.service.security.PermitService;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PermitStepDefs {

    @Autowired private TestRestTemplate restTemplate;
    @Autowired private PermitService permitService;

    private String jwtToken;

    private ResponseEntity<List<PermitApi>> listResponse;
    private ResponseEntity<String> rawResponseWithoutAuth;

    @Before
    public void authenticate() {
        var credentials = new UserAuthentication("admin", "admin");
        var loginResponse = restTemplate.postForEntity(
                "/api/security/authenticate", credentials, ReturnAuthentication.class
        );

        Assertions.assertEquals(HttpStatus.OK, loginResponse.getStatusCode(), "Login failed");
        Assertions.assertNotNull(loginResponse.getBody(), "Login response body is null");
        jwtToken = loginResponse.getBody().token();
        Assertions.assertNotNull(jwtToken, "JWT token must not be null");
    }

    private HttpHeaders authHeaders() {
        HttpHeaders h = new HttpHeaders();
        h.setBearerAuth(jwtToken);
        return h;
    }

    @Given("default permits exist")
    public void default_permits_exist() {
        // idempotent seeding
        permitService.createDefaultPermitsIfNotExists();
    }

    @When("I list permits")
    public void i_list_permits() {
        HttpEntity<Void> req = new HttpEntity<>(authHeaders());
        listResponse = restTemplate.exchange(
                "/api/permit",
                HttpMethod.GET,
                req,
                new ParameterizedTypeReference<List<PermitApi>>() {}
        );
        Assertions.assertEquals(HttpStatus.OK, listResponse.getStatusCode(), "List permits failed");
        Assertions.assertNotNull(listResponse.getBody(), "List body is null");
    }

    @When("I list permits without authentication")
    public void i_list_permits_without_authentication() {
        rawResponseWithoutAuth = restTemplate.exchange(
                "/api/permit",
                HttpMethod.GET,
                HttpEntity.EMPTY,
                String.class
        );
    }

    @Then("the permits list should contain at least {int} elements")
    public void the_permits_list_should_contain_at_least_n(int n) {
        List<PermitApi> body = listResponse.getBody();
        Assertions.assertNotNull(body, "Permit list is null");
        Assertions.assertTrue(body.size() >= n,
                "Expected at least " + n + " permits but got " + body.size());
    }

    @Then("the permits should include {string}")
    public void the_permits_should_include(String permitName) {
        List<PermitApi> body = listResponse.getBody();
        Assertions.assertNotNull(body, "Permit list is null");
        Set<String> names = body.stream().map(PermitApi::getName).collect(Collectors.toSet());
        Assertions.assertTrue(names.contains(permitName),
                "Expected permit name not found in response: " + permitName);
    }

    @Then("each permit should have a non-null id and a non-empty name")
    public void each_permit_has_id_and_name() {
        List<PermitApi> body = listResponse.getBody();
        Assertions.assertNotNull(body, "Permit list is null");
        Assertions.assertFalse(body.isEmpty(), "Permit list is empty");

        for (PermitApi p : body) {
            Assertions.assertNotNull(p.getId(), "Permit id is null for: " + p);
            Assertions.assertNotNull(p.getName(), "Permit name is null for id=" + p.getId());
            Assertions.assertFalse(p.getName().isBlank(), "Permit name is blank for id=" + p.getId());
            Assertions.assertNotNull(p.getRoles(), "Roles list is null for id=" + p.getId());
        }
    }

    @Then("the response status should be {int}")
    public void the_response_status_should_be(Integer expectedStatus) {
        Assertions.assertNotNull(rawResponseWithoutAuth, "No unauthenticated response captured");
        Assertions.assertEquals(expectedStatus.intValue(),
                rawResponseWithoutAuth.getStatusCode().value(),
                "Unexpected HTTP status for unauthenticated request");
    }
}
