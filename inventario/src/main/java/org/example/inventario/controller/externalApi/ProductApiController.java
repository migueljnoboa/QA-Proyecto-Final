package org.example.inventario.controller.externalApi;

import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.example.inventario.model.dto.api.ProductApi;
import org.example.inventario.model.dto.inventory.ReturnList;
import org.example.inventario.model.entity.inventory.Product;
import org.example.inventario.service.api.inventory.ProductApiService;
import org.example.inventario.service.inventory.ProductService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api-product")
@RequiredArgsConstructor
public class ProductApiController {
    private final ProductApiService productApiService;

    @GetMapping("")
    public ReturnList<ProductApi> getProducts(@RequestParam(name = "page", defaultValue = "0") @Parameter(description = "Page Number.", example = "0") int page,
                                              @RequestParam(name = "size", defaultValue = "50") @Parameter(description = "Page Size.", example = "50") int size ){

        return productApiService.getAllProducts(page, size);
    }

    @GetMapping("{id}")
    public ProductApi getProductById(@PathVariable(name = "id") @Parameter(description = "Product ID.", example = "1") Long id) {
        return productApiService.getProductById(id);
    }

    @PostMapping("")
    public ProductApi createProduct(@RequestBody Product product) {
        if (product == null) {
            throw new IllegalArgumentException("Product cannot be null");
        }
        return productApiService.createProduct(product);
    }

    @PutMapping("{id}")
    public ProductApi updateProduct(@PathVariable(name = "id") @Parameter(description = "Product ID.", example = "1") Long id, @RequestBody Product product) {
        if (product == null) {
            throw new IllegalArgumentException("Product cannot be null");
        }
        return productApiService.updateProduct(id, product);
    }

    @DeleteMapping("{id}")
    public ProductApi deleteProduct(@PathVariable(name = "id") @Parameter(description = "Product ID.", example = "1") Long id) {
        return productApiService.deleteProduct(id);
    }

    
}
