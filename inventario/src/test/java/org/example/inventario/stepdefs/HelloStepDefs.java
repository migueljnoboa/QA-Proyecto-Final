package org.example.inventario.stepdefs;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;

import static org.junit.jupiter.api.Assertions.assertEquals;

// imports...
public class HelloStepDefs {
    private String message;

    @Given("I say hello")
    public void iSayHello() {
        message = "Hello from Cucumber!";
    }

    @Then("I should see {string}")
    public void iShouldSee(String expected) {
        assertEquals(expected, message);
    }
}
