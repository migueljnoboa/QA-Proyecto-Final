package org.example.inventario.model.dto.api;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.inventario.model.entity.inventory.Category;
import org.example.inventario.model.entity.inventory.Product;
import org.example.inventario.model.entity.inventory.Supplier;

import java.math.BigDecimal;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ProductApi{
    private Long id;
    private String name;
    private String description;
    private Category category;
    private BigDecimal price;
    private Integer stock;
    private Integer minStock;
    private String image;
    private SupplierApi supplier;

    public static ProductApi from(Product productApi){
        if (productApi == null) {
            return null;
        }
        ProductApi product = new ProductApi();
        product.setId(productApi.getId());
        product.setName(productApi.getName());
        product.setDescription(productApi.getDescription());
        product.setCategory(productApi.getCategory());
        product.setPrice(productApi.getPrice());
        product.setStock(productApi.getStock());
        product.setMinStock(productApi.getMinStock());
        product.setImage(productApi.getImage());
        product.setSupplier(SupplierApi.from(productApi.getSupplier()));
        return product;
    }

}
