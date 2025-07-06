package org.example.inventario;

import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static io.cucumber.junit.platform.engine.Constants.GLUE_PROPERTY_NAME;

@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("org/example/inventario/features")
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "org.example.inventario.stepdefs")
@SpringBootTest
@TestPropertySource("classpath:application-dev.properties")
public class CucumberTest { }