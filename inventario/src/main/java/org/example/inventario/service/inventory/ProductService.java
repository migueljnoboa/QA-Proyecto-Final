package org.example.inventario.service.inventory;

import jakarta.persistence.*;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.example.inventario.exception.MyException;
import org.example.inventario.model.dto.inventory.ReturnList;
import org.example.inventario.model.entity.inventory.Category;
import org.example.inventario.model.entity.inventory.Product;
import org.example.inventario.model.entity.inventory.Supplier;
import org.example.inventario.model.specification.ProductSpecification;
import org.example.inventario.repository.inventory.ProductRepository;
import org.example.inventario.repository.inventory.SupplierRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    @Autowired
    private SupplierService supplierService;


    public Product createProduct(Product product) {

        if (product == null) {
            throw new MyException(400, "Product is null");
        }

        checkVariables(product);

        return productRepository.save(product);
    }

    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new MyException(MyException.ERROR_NOT_FOUND, "Product not found with ID: " + id));
    }

    public ReturnList<Product> getAllProducts(int page, int size) {
        PageRequest pageable = PageRequest.of(page, size);
        Page<Product> list = productRepository.findAll(pageable);
        ReturnList<Product> result = new ReturnList<>();
        result.setPage(page);
        result.setPageSize(size);
        result.setTotalElements((int) list.getTotalElements());
        result.setTotalPages(list.getTotalPages());
        result.setData(list.getContent());
        return result;
    }

    public Product updateProduct(Long id, Product product) {

        if(id == null) {
            throw new MyException(MyException.ERROR_NOT_FOUND, "Product not found with ID: " + id);
        }

        Product oldProduct = getProductById(id);

        if(product == null || oldProduct == null) {
            throw  new MyException(400, "Supplier not found or null");
        }

        checkVariables(product);

        oldProduct.setName(product.getName());
        oldProduct.setDescription(product.getDescription());
        oldProduct.setCategory(product.getCategory());
        oldProduct.setPrice(product.getPrice());
        oldProduct.setStock(product.getStock());
        oldProduct.setMinStock(product.getMinStock());
        oldProduct.setImage(product.getImage());
        oldProduct.setSupplier(product.getSupplier());

        return productRepository.save(product);
    }

    public Product deleteProduct(Long id) {
        if (id == null || !productRepository.existsById(id)) {
            throw new MyException(MyException.ERROR_NOT_FOUND, "Product not found with ID: " + id);
        }
        Product supplier = getProductById(id);
        supplier.setEnabled(false);
        return productRepository.save(supplier);
    }

    private void checkVariables(Product product) {

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

    public Page<Product> searchProducts(String name, Category category, BigDecimal price, Integer minStock,Integer stock , Pageable pageable) {
        Specification<Product> spec = Specification.not((Specification<Product>) null);
        spec = spec.and(ProductSpecification.hasName(name));
        spec = spec.and(ProductSpecification.hasCategory(category));
        spec = spec.and(ProductSpecification.hasPrice(price));
        spec = spec.and(ProductSpecification.hasMinStockThreshold(minStock));
        spec = spec.and(ProductSpecification.hasStock(stock));

        return productRepository.findAll(spec, pageable);
    }

}
