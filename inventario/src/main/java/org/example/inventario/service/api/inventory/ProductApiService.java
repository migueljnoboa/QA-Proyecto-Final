package org.example.inventario.service.api.inventory;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.example.inventario.exception.MyException;
import org.example.inventario.model.dto.api.ProductApi;
import org.example.inventario.model.dto.inventory.ReturnList;
import org.example.inventario.model.entity.inventory.Product;
import org.example.inventario.repository.inventory.ProductRepository;
import org.example.inventario.service.inventory.SupplierService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductApiService {

    private final ProductRepository productRepository;
    private final SupplierService supplierService;


    public ReturnList<ProductApi> getAllProducts(int page, int size) {
        PageRequest pageable = PageRequest.of(page, size);
        Page<Product> list = productRepository.findAll(pageable);
        List<ProductApi> productApis = new ArrayList<>();

        for (Product product : list) {
            ProductApi productApi;
            productApi = ProductApi.from(product);
            productApis.add(productApi);
        }
        ReturnList<ProductApi> result = new ReturnList<>();
        result.setPage(page);
        result.setPageSize(size);
        result.setTotalElements((int) list.getTotalElements());
        result.setTotalPages(list.getTotalPages());
        result.setData(productApis);
        return result;
    }


    public ProductApi createProduct(ProductApi product) {

        if (product == null) {
            throw new MyException(400, "Product is null");
        }
        checkVariables(product);

        Product ProductNew  = productRepository.save(product);

        ProductApi productApi = ProductApi.from(ProductNew);

        return productApi;
    }

    private void checkVariables(ProductApi product) {

        if(product == null) {
            throw new MyException(400,"Supplier cannot be null");
        }
        if(StringUtils.isBlank(product.getName())) {
            throw  new MyException(400,"Product name cannot be null or empty");
        }
        if(product.getCategory() == null) {
            throw new MyException(400,"Category cannot be null");
        }
        if(product.getPrice() == null || product.getPrice().compareTo(BigDecimal.ZERO) < 0) {
            throw new MyException(400,"Product price cannot be null or less than zero");
        }
        if(product.getStock() == null || product.getStock() < 0) {
            throw new MyException(400,"Product stock cannot be null or less than zero");
        }
        if(product.getMinStock() == null || product.getMinStock() < 0) {
            throw new MyException(400,"Product min stock cannot be null or less than zero");
        }
        if(product.getSupplier() == null) {
            throw new MyException(400,"Product supplier cannot be null");
        }

        supplierService.getSupplierById(product.getSupplier().getId());
        if (supplierService.getSupplierById(product.getSupplier().getId()) == null){
            throw new MyException(400,"Supplier not found");
        }

    }

    public ProductApi getProductById(Long id) {
        if (id == null) {
            throw new MyException(MyException.ERROR_NOT_FOUND, "Product ID cannot be null");
        }
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new MyException(MyException.ERROR_NOT_FOUND, "Product not found with ID: " + id));
        ProductApi productApi;
        productApi = ProductApi.from(product);
        return productApi;
    }
}
