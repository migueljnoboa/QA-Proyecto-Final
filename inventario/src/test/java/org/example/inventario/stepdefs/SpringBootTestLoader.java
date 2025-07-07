package org.example.inventario.stepdefs;

import io.cucumber.java.AfterAll;
import io.cucumber.java.BeforeAll;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;

@CucumberContextConfiguration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("dev")
public class SpringBootTestLoader {

    static MySQLContainer mysqlContainer;

    @BeforeAll
    public static void init() {
        mysqlContainer = new MySQLContainer<>("mysql:8.0.33")
                .withDatabaseName("testdb")
                .withUsername("testuser")
                .withPassword("testpass");
        mysqlContainer.start();
        System.out.println(mysqlContainer.getJdbcUrl());
    }

    // Override Spring properties to use the container's JDBC URL and credentials
    @BeforeAll
    @DynamicPropertySource
    static void overrideDatasourceProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mysqlContainer::getJdbcUrl);
        registry.add("spring.datasource.username", mysqlContainer::getUsername);
        registry.add("spring.datasource.password", mysqlContainer::getPassword);
    }

    @AfterAll
    public static void tearDown() {
        System.out.println("closing DB connection");
        mysqlContainer.stop();
    }

}
