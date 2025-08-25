package org.example.inventario.model.dto.api;

import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.inventario.model.dto.inventory.ReturnList;
import org.example.inventario.model.entity.inventory.Category;
import org.example.inventario.model.entity.inventory.Product;
import org.example.inventario.model.entity.inventory.ProductStockChange;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ProductStockChangeApi {
    private Long id;
    private ProductApi product;
    private boolean increased;
    private int amount;
    private LocalDateTime date;
    private String createdBy;

    public static ProductStockChangeApi from(ProductStockChange productStockChange){
        if (productStockChange == null) {
            return null;
        }
        ProductStockChangeApi productStockChangeApi = new ProductStockChangeApi();
        productStockChangeApi.setId(productStockChange.getId());
        productStockChangeApi.setProduct(ProductApi.from(productStockChange.getProduct()));
        productStockChangeApi.setIncreased(productStockChange.isIncreased());
        productStockChangeApi.setAmount(productStockChange.getAmount());
        productStockChangeApi.setDate(productStockChange.getDate());
        productStockChangeApi.setCreatedBy(productStockChange.getCreatedBy());
        return productStockChangeApi;
    }

    public static List<ProductStockChangeApi> from(List<ProductStockChange> productStockChangeList){
        List<ProductStockChangeApi> productStockChangeApiList = new ArrayList<>();
        for (ProductStockChange productStockChange : productStockChangeList) {
            productStockChangeApiList.add(from(productStockChange));
        }
        return productStockChangeApiList;
    }

    public static ReturnList<ProductStockChangeApi> from(ReturnList<ProductStockChange> returnList){
        ReturnList<ProductStockChangeApi> result = new ReturnList<>();
        result.setPage(returnList.getPage());
        result.setPageSize(returnList.getPageSize());
        result.setTotalElements(returnList.getTotalElements());
        result.setTotalPages(returnList.getTotalPages());
        result.setData(from(returnList.getData()));
        return result;
    }
}
