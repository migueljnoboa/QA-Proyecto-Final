package org.example.inventario;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@TestPropertySource("classpath:application-dev.properties")
@Transactional
class InventarioApplicationTests {

    @Test
    void contextLoads() {

    }

}
