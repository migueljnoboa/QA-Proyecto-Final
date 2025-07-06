package org.example.inventario.controller;

import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.example.inventario.model.dto.inventory.ReturnList;
import org.example.inventario.model.entity.inventory.Product;
import org.example.inventario.service.inventory.ProductService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/product")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @GetMapping("")
    public ReturnList<Product> getAllProducts(@RequestParam(name = "page", defaultValue = "0") @Parameter(description = "Page Number.", example = "0") int page,
                                                @RequestParam(name = "size", defaultValue = "50") @Parameter(description = "Page Size.", example = "50") int size ){
        return productService.getAllProducts(page, size);

    }

    @GetMapping("/{id}")
    public Product getProductById(@PathVariable(name = "id") @Parameter(description = "Product ID.", example = "1") Long id) {
        return productService.getProductById(id);
    }

    @PostMapping("/")
    public Product createProduct(@RequestBody Product product) {
        if (product == null) {
            throw new IllegalArgumentException("Product cannot be null");
        }
        return productService.createProduct(product);
    }

    @PutMapping("/{id}")
    public Product updateProduct(
            @PathVariable(name = "id")
            @Parameter(description = "Product ID.", example = "1") Long id,
            @RequestBody Product product) {
        if (product == null) {
            throw new IllegalArgumentException("Product cannot be null");
        }
        return productService.updateProduct(id, product);
    }

    @DeleteMapping("/{id}")
    public Product deleteProduct(
            @PathVariable(name = "id")
            @Parameter(description = "updateProduct ID.", example = "1") Long id) {
       return productService.deleteProduct(id);
    }



}
