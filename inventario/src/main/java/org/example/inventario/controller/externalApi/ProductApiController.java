package org.example.inventario.controller.externalApi;

import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.example.inventario.model.dto.api.ProductApi;
import org.example.inventario.model.dto.inventory.ReturnList;
import org.example.inventario.model.entity.inventory.Product;
import org.example.inventario.service.inventory.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/product")
@RequiredArgsConstructor
public class ProductApiController {
    private final ProductService productService;

    @GetMapping("")
    public ReturnList<ProductApi> getProducts(Pageable pageable) {

        return ProductApi.from(productService.getAllProducts(pageable));
    }

    @GetMapping("{id}")
    public ProductApi getProductById(@PathVariable(name = "id") @Parameter(description = "Product ID.", example = "1") Long id) {

        return ProductApi.from(productService.getProductById(id));
    }

    @PostMapping("")
    public ProductApi createProduct(@RequestBody Product product) {
        if (product == null) {
            throw new IllegalArgumentException("Product cannot be null");
        }
        return ProductApi.from(productService.createProduct(product));
    }

    @PutMapping("{id}")
    public ProductApi updateProduct(@PathVariable(name = "id") @Parameter(description = "Product ID.", example = "1") Long id, @RequestBody Product product) {
        if (product == null) {
            throw new IllegalArgumentException("Product cannot be null");
        }
        return ProductApi.from(productService.updateProduct(id, product));
    }

    @DeleteMapping("{id}")
    public ProductApi deleteProduct(@PathVariable(name = "id") @Parameter(description = "Product ID.", example = "1") Long id) {
        return ProductApi.from(productService.deleteProduct(id));
    }

    
}
