package org.example.inventario.service.inventory;

import lombok.RequiredArgsConstructor;
import org.example.inventario.exception.MyException;
import org.example.inventario.model.dto.inventory.ReturnList;
import org.example.inventario.model.entity.inventory.Product;
import org.example.inventario.model.entity.inventory.Supplier;
import org.example.inventario.repository.inventory.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;


    public Product createProduct(Product product) {

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
        if (id == null || !productRepository.existsById(id)) {
            throw new MyException(MyException.ERROR_NOT_FOUND, "Product not found with ID: " + id);
        }
        product.setId(id);
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

}
