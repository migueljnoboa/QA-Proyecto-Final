package org.example.inventario.stepdefs;

import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@CucumberContextConfiguration
@SpringBootTest
@TestPropertySource("classpath:application-dev.properties")
public class CucumberSpringConfiguration { }
