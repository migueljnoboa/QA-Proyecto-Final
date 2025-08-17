package org.example.inventario.repository.inventory;

import org.example.inventario.model.entity.inventory.Category;
import org.example.inventario.model.entity.inventory.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {

    Page<Product> findAllByEnabledIsTrue(Specification<Product> spec, Pageable pageable);

    @Query("SELECT SUM(p.stock * p.price) FROM Product p WHERE p.enabled = true")
    BigDecimal getTotalStockValue();

    @Query("SELECT SUM(p.price) FROM Product p WHERE p.category = ?1")
    BigDecimal getTotalStockValueByCategory(Category category);

    @Query("SELECT (SUM(p.stock * p.price) * 100.0) / (SELECT SUM(p2.stock * p2.price) FROM Product p2 WHERE p2.enabled = true) FROM Product p WHERE p.category = :category AND p.enabled = true")
    Double getTotalStockValuePercentByCategory(Category category);

    int countAllByEnabledIsTrue();
}
