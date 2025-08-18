package org.example.inventario.services.inventario;

import org.example.inventario.exception.MyException;
import org.example.inventario.model.dto.inventory.ReturnList;
import org.example.inventario.model.entity.inventory.Supplier;
import org.example.inventario.model.entity.security.Permit;
import org.example.inventario.service.inventory.SupplierService;
import org.example.inventario.service.security.PermitService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("dev")
@SpringBootTest
@Transactional
@Testcontainers
public class PermitServiceTest {

    @Autowired
    private PermitService permitService;

    @Test
    public void findAllTest() {

        permitService.createDefaultPermitsIfNotExists();
        assertEquals(19, permitService.findAll().size());
    }

    @Test
    public void findByNameTest() {
        assertNotNull(permitService.findByName(Permit.USERS_MENU));
    }

    @Test
    public void searchPermitTest() {
        assertTrue(permitService.searchPermit(Permit.USERS_MENU, PageRequest.of(0, 10)).getTotalElements() > 0);
    }


}
