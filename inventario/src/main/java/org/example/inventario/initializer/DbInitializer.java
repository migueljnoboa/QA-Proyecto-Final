package org.example.inventario.initializer;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.inventario.service.security.PermitService;
import org.example.inventario.service.security.RoleService;
import org.example.inventario.service.security.UserService;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;


@Component
@Order(value = 1)
@RequiredArgsConstructor
@Slf4j
public  class DbInitializer implements ApplicationRunner {
    private final UserService userService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        userService.createDefaultUserIfNotExists();
    }
}

