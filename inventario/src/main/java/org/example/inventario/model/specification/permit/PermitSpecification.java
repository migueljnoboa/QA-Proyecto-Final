package org.example.inventario.model.specification.permit;

import org.example.inventario.model.entity.inventory.Product;
import org.example.inventario.model.entity.security.Permit;
import org.springframework.data.jpa.domain.Specification;

public class PermitSpecification {

    public static Specification<Permit> hasName(String name) {
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


    public static Specification<Permit> isEnabled() {
        return (root, query, criteriaBuilder) -> criteriaBuilder.isTrue(root.get("enabled"));
    }
}
