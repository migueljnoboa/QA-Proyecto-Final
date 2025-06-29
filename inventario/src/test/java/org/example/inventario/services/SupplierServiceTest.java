package org.example.inventario.services;

import org.example.inventario.model.entity.inventory.Product;
import org.example.inventario.model.entity.inventory.Supplier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SupplierServiceTest {

    //@Autowired
    //private SupplierService supplierService;

    private Supplier supplier1;
    private Supplier supplier2;
    private Supplier supplier3;

    @BeforeEach
    public void init() {
        supplier1 = new Supplier();
        supplier2 = new Supplier();
        supplier3 = new Supplier();
    }

    @Test
    public void saveSupplier() {



    }

    @Test
    public void updateSupplier() {

    }

    @Test
    public void findSupplierById() {

    }

    @Test
    public void findAllSupplier() {

    }

    @Test
    public void deleteSupplier() {

    }
}
