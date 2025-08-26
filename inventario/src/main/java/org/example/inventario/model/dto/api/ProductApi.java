package org.example.inventario.model.dto.api;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.inventario.exception.MyException;
import org.example.inventario.model.dto.inventory.ReturnList;
import org.example.inventario.model.entity.inventory.Category;
import org.example.inventario.model.entity.inventory.Product;
import org.example.inventario.model.entity.inventory.Supplier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ProductApi{
    private Long id;
    private String name;
    private String description;
    private String category;
    private BigDecimal price;
    private Integer stock;
    private Integer minStock;
    private String image;
    private SupplierApi supplier;

    public static ProductApi from(Product product){
        if (product == null) {
            return null;
        }
        ProductApi productApi = new ProductApi();
        productApi.setId(product.getId());
        productApi.setName(product.getName());
        productApi.setDescription(product.getDescription());
        productApi.setCategory(product.getCategory().name());
        productApi.setPrice(product.getPrice());
        productApi.setStock(product.getStock());
        productApi.setMinStock(product.getMinStock());
        productApi.setImage(product.getImage());
        productApi.setSupplier(SupplierApi.from(product.getSupplier()));
        return productApi;
    }

    public static List<ProductApi> from(List<Product> productList){
        List<ProductApi> productApis = new ArrayList<>();
        for (Product product : productList) {
            productApis.add(from(product));
        }
        return productApis;
    }

    public static ReturnList<ProductApi> from(ReturnList<Product> returnList){
        ReturnList<ProductApi> result = new ReturnList<>();
        result.setPage(returnList.getPage());
        result.setPageSize(returnList.getPageSize());
        result.setTotalElements(returnList.getTotalElements());
        result.setTotalPages(returnList.getTotalPages());
        result.setData(from(returnList.getData()));
        return result;
    }


    public static Product from(ProductApi productApi){
        if (productApi == null) return null;

        if (productApi.getCategory() == null) {
            throw new MyException(400, "Category cannot be null");
        }
        if (productApi.getSupplier() == null || productApi.getSupplier().getId() == null) {
            throw new MyException(400, "Supplier id is required");
        }

        Product product = new Product();
        product.setId(productApi.getId());
        product.setName(productApi.getName());
        product.setDescription(productApi.getDescription());
        product.setCategory(Category.valueOf(productApi.getCategory())); // will throw if invalid enum
        product.setPrice(productApi.getPrice());
        product.setStock(productApi.getStock());
        product.setMinStock(productApi.getMinStock());
        product.setImage(productApi.getImage());

        // Rememeber to add supplier after

        return product;
    }
}
