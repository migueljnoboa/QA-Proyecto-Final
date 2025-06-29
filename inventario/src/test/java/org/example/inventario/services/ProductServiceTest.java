package org.example.inventario.services;

import org.example.inventario.model.entity.inventory.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class ProductServiceTest {

    //@Autowired
    //private ProductService productService;

    private Product product1;
    private Product product2;
    private Product product3;

    @BeforeEach
    public void init() {
        product1 = new Product();
        product2 = new Product();
        product3 = new Product();
    }

    @Test
    public void saveProduct() {

    }

    @Test
    public void updateProduct() {

    }

    @Test
    public void findProductById() {

    }

    @Test
    public void findAllProducts() {

    }

    @Test
    public void deleteProduct() {

    }
}
