package org.example.inventario.model.specification.product;

import org.example.inventario.model.entity.inventory.Product;
import org.example.inventario.model.entity.inventory.ProductStockChange;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

public final class ProductStockChangeSpecification {

    private ProductStockChangeSpecification() {}

    public static Specification<ProductStockChange> byProduct(Long productId) {
        if (productId == null) return null;
        return (root, query, cb) -> cb.equal(root.get("product").get("id"), productId);
    }

    public static Specification<ProductStockChange> increased(Boolean increased) {
        if (increased == null) return null;
        return (root, query, cb) -> cb.equal(root.get("increased"), increased);
    }

    public static Specification<ProductStockChange> amountBetween(Integer min, Integer max) {
        if (min == null && max == null) return null;

        Integer a = min, b = max;
        if (a != null && b != null && a > b) { int t = a; a = b; b = t; }

        final Integer minFinal = a, maxFinal = b;
        return (root, query, cb) -> {
            if (minFinal != null && maxFinal != null) {
                return cb.between(root.get("amount"), minFinal, maxFinal);
            } else if (minFinal != null) {
                return cb.greaterThanOrEqualTo(root.get("amount"), minFinal);
            } else {
                return cb.lessThanOrEqualTo(root.get("amount"), maxFinal);
            }
        };
    }

    // (optional) also swap from/to if from > to
    public static Specification<ProductStockChange> dateBetween(LocalDateTime from, LocalDateTime to) {
        if (from == null && to == null) return null;

        LocalDateTime a = from, b = to;
        if (a != null && b != null && a.isAfter(b)) { LocalDateTime t = a; a = b; b = t; }

        final LocalDateTime fromFinal = a, toFinal = b;
        return (root, query, cb) -> {
            if (fromFinal != null && toFinal != null) {
                return cb.between(root.get("date"), fromFinal, toFinal);
            } else if (fromFinal != null) {
                return cb.greaterThanOrEqualTo(root.get("date"), fromFinal);
            } else {
                return cb.lessThanOrEqualTo(root.get("date"), toFinal);
            }
        };
    }

    public static Specification<ProductStockChange> createdByEquals(String createdBy) {
        if (createdBy == null || createdBy.isBlank()) return null;
        return (root, query, cb) -> cb.equal(root.get("createdBy"), createdBy.trim());
    }

    public static Specification<ProductStockChange> build(
            Long productId,
            Boolean increased,
            Integer minAmount,
            Integer maxAmount,
            LocalDateTime fromDate,
            LocalDateTime toDate,
            String createdBy
    ) {
        return Specification.allOf(
                byProduct(productId),
                increased(increased),
                amountBetween(minAmount, maxAmount),
                dateBetween(fromDate, toDate),
                createdByEquals(createdBy)
        );
    }
}