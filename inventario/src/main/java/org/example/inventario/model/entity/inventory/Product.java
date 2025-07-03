package org.example.inventario.model.entity.inventory;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.inventario.model.dto.api.ProductApi;
import org.example.inventario.model.entity.Base;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Product extends Base {
    private String name;
    @Lob
    private String description;

    @Enumerated(EnumType.STRING)
    private Category category;

    private BigDecimal price;

    private Integer stock;

    private Integer minStock;
    @Lob
    private String image;

    @ManyToOne(fetch = FetchType.EAGER)
    private Supplier supplier;


    public static Product from(ProductApi product) {
        if (product == null) {
            return null;
        }
        Product newProduct = new Product();
        newProduct.setId(product.getId());
        newProduct.setName(product.getName());
        newProduct.setDescription(product.getDescription());
        newProduct.setCategory(product.getCategory());
        newProduct.setPrice(product.getPrice());
        newProduct.setStock(product.getStock());
        newProduct.setMinStock(product.getMinStock());
        newProduct.setImage(product.getImage());
        newProduct.setSupplier(product.getSupplier());
        return newProduct;
    }

}
