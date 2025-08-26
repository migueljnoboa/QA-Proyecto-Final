package org.example.inventario.controller.externalApi;

import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.example.inventario.model.dto.api.ProductApi;
import org.example.inventario.model.dto.inventory.ReturnList;
import org.example.inventario.model.entity.security.Permit;
import org.example.inventario.service.inventory.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/product")
@RequiredArgsConstructor
public class ProductApiController {
    private final ProductService productService;

    @Secured({Permit.PRODUCT_VIEW})
    @GetMapping("")
    public ReturnList<ProductApi> getProducts(Pageable pageable) {

        return ProductApi.from(productService.getAllProducts(pageable));
    }

    @Secured({Permit.PRODUCT_VIEW})
    @GetMapping("{id}")
    public ProductApi getProductById(@PathVariable(name = "id") @Parameter(description = "Product ID.", example = "1") Long id) {

        return ProductApi.from(productService.getProductById(id));
    }

    @Secured({Permit.PRODUCT_CREATE})
    @PostMapping("")
    public ProductApi createProduct(@RequestBody ProductApi product) {
        if (product == null) {
            throw new IllegalArgumentException("Product cannot be null");
        }
        return ProductApi.from(productService.createProduct(product));
    }

    @Secured({Permit.PRODUCT_EDIT})
    @PutMapping("{id}")
    public ProductApi updateProduct(@PathVariable(name = "id") @Parameter(description = "Product ID.", example = "1") Long id, @RequestBody ProductApi product) {
        if (product == null) {
            throw new IllegalArgumentException("Product cannot be null");
        }
        product.setId(id);
        return ProductApi.from(productService.updateProduct(product));
    }

    @Secured({Permit.PRODUCT_DELETE})
    @DeleteMapping("{id}")
    public ProductApi deleteProduct(@PathVariable(name = "id") @Parameter(description = "Product ID.", example = "1") Long id) {
        return ProductApi.from(productService.deleteProduct(id));
    }

    
}
