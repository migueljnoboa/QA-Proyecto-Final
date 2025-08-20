package org.example.inventario.service.inventory;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.example.inventario.exception.MyException;
import org.example.inventario.model.dto.inventory.ReturnList;
import org.example.inventario.model.entity.inventory.Category;
import org.example.inventario.model.entity.inventory.Product;
import org.example.inventario.model.entity.inventory.ProductStockChange;
import org.example.inventario.model.specification.product.ProductSpecification;
import org.example.inventario.repository.inventory.ProductRepository;
import org.example.inventario.repository.inventory.ProductStockChangeRepository;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductStockChangeService {

    @Autowired
    private final ProductStockChangeRepository productStockChangeRepository;

    @Transactional
    public ProductStockChange create(Product product, boolean increased, int stockChange) {
        ProductStockChange productStockChange = new ProductStockChange();
        productStockChange.setProduct(product);
        productStockChange.setAmount(stockChange);
        productStockChange.setDate(LocalDateTime.now());
        productStockChange.setIncreased(increased);
        productStockChange.setCreatedBy(product.getLastModifiedBy());
        return productStockChangeRepository.saveAndFlush(productStockChange);
    }

    @Transactional(readOnly = true)
    public ProductStockChange getById(Long id) {
        return productStockChangeRepository.findById(id).orElse(null);
    }
    @Transactional(readOnly = true)
    public ReturnList<ProductStockChange> getAll(@NotNull Pageable pageable) {
        Page<ProductStockChange> list = productStockChangeRepository.findAll(pageable);
        ReturnList<ProductStockChange> result = new ReturnList<>();
        result.setPage(pageable.getPageNumber());
        result.setPageSize(pageable.getPageSize());
        result.setTotalElements((int) list.getTotalElements());
        result.setTotalPages(list.getTotalPages());
        result.setData(list.getContent());
        return result;
    }

}
