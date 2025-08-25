package org.example.inventario.model.specification.product;

import org.example.inventario.model.entity.inventory.Category;
import org.example.inventario.model.entity.inventory.Product;
import org.example.inventario.model.entity.inventory.Supplier;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;

public class ProductSpecification {

    public static Specification<Product> hasSupplier(Supplier supplier) {
        return (root, query, cb) -> {
            if (supplier == null || supplier.getId() == null) {
                return cb.conjunction();
            }
            return cb.equal(root.get("supplier").get("id"), supplier.getId());
        };
    }

    // (Optional: name-based search if you ever need it)
    public static Specification<Product> hasSupplierNameLike(String name) {
        return (root, query, cb) -> {
            if (name == null || name.trim().isEmpty()) return cb.conjunction();
            return cb.like(cb.lower(root.get("supplier").get("name")), "%" + name.toLowerCase() + "%");
        };
    }

    public static Specification<Product> hasName(String name) {
        return (root, query, criteriaBuilder) -> {
            if (name == null || name.trim().isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("name")),
                    "%" + name.toLowerCase() + "%"
            );
        };
    }
    public static Specification<Product> hasCategory(Category category) {
        return (root, query, criteriaBuilder) -> {
            if (category == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("category"), category);
        };
    }

    public static Specification<Product> hasPrice(BigDecimal price) {
        return (root, query, criteriaBuilder) -> {
            if (price == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("price"), price);
        };
    }

//    public static Specification<Product> hasStockBetween(Integer minStock, Integer maxStock) {
//        return (root, query, criteriaBuilder) -> {
//            if (minStock == null && maxStock == null) {
//                return criteriaBuilder.conjunction();
//            }
//
//            if (minStock != null && maxStock != null) {
//                return criteriaBuilder.between(root.get("stock"), minStock, maxStock);
//            } else if (minStock != null) {
//                return criteriaBuilder.greaterThanOrEqualTo(root.get("stock"), minStock);
//            } else {
//                return criteriaBuilder.lessThanOrEqualTo(root.get("stock"), maxStock);
//            }
//        };
//    }

    public static Specification<Product> hasMinStockThreshold(Integer minStockThreshold) {
        return (root, query, criteriaBuilder) -> {
            if (minStockThreshold == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.greaterThanOrEqualTo(root.get("minStock"), minStockThreshold);
        };
    }
    public static Specification<Product> hasStock(Integer stock) {
        return (root, query, criteriaBuilder) -> {
            if (stock == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("stock"), stock);
        };
    }


//    public static Specification<Product> hasSupplier(Long supplierId, String supplierName) {
//        return (root, query, criteriaBuilder) -> {
//            if (supplierId == null && (supplierName == null || supplierName.trim().isEmpty())) {
//                return criteriaBuilder.conjunction();
//            }
//
//            Join<Product, Supplier> supplierJoin = root.join("supplier", JoinType.INNER);
//
//            if (supplierId != null && supplierName != null && !supplierName.trim().isEmpty()) {
//                return criteriaBuilder.and(
//                        criteriaBuilder.equal(supplierJoin.get("id"), supplierId),
//                        criteriaBuilder.like(
//                                criteriaBuilder.lower(supplierJoin.get("name")),
//                                "%" + supplierName.toLowerCase() + "%"
//                        )
//                );
//            } else if (supplierId != null) {
//                return criteriaBuilder.equal(supplierJoin.get("id"), supplierId);
//            } else {
//                return criteriaBuilder.like(
//                        criteriaBuilder.lower(supplierJoin.get("name")),
//                        "%" + supplierName.toLowerCase() + "%"
//                );
//            }
//        };
//    }

    public static Specification<Product> hasLowStock() {
        return (root, query, criteriaBuilder) -> {
            return criteriaBuilder.lessThanOrEqualTo(root.get("stock"), root.get("minStock"));
        };
    }
    public static Specification<Product> isEnabled() {
        return (root, query, criteriaBuilder) -> criteriaBuilder.isTrue(root.get("enabled"));
    }
}
