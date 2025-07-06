package org.example.inventario.security;

import org.example.inventario.model.entity.security.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@TestPropertySource("classpath:application-dev.properties")
@Transactional
public class UserServiceTest {

    //@Autowired
    //private SupplierService productService;

    private User user1;
    private User user2;
    private User user3;

    @BeforeEach
    public void init() {
        user1 = new User();
        user2 = new User();
        user3 = new User();
    }

    @Test
    public void saveUser() {

    }

    @Test
    public void updateUser() {

    }

    @Test
    public void findUserById() {

    }

    @Test
    public void findAllUser() {

    }

    @Test
    public void deleteUser() {

    }
}
