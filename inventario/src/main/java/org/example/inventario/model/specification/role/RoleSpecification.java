package org.example.inventario.model.specification.role;

import org.example.inventario.model.entity.inventory.Category;
import org.example.inventario.model.entity.inventory.Product;
import org.example.inventario.model.entity.security.Permit;
import org.example.inventario.model.entity.security.Role;
import org.springframework.data.jpa.domain.Specification;

public class RoleSpecification {
    public static Specification<Role> hasName(String name) {
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
    public static Specification<Role> hasPermit(Permit permit) {
        return (root, query, criteriaBuilder) -> {
            if (permit == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.isMember(permit, root.get("permits"));
        };
    }

    public static Specification<Role> isEnabled() {
        return (root, query, criteriaBuilder) -> criteriaBuilder.isTrue(root.get("enabled"));
    }
}
