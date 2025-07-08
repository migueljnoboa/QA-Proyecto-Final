package org.example.inventario.model.specification.supplier;

import org.example.inventario.model.entity.inventory.Product;
import org.example.inventario.model.entity.inventory.Supplier;
import org.springframework.data.jpa.domain.Specification;

public class SupplierSpecification {
        public static Specification<Supplier> hasName(String name) {
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

    public static Specification<Supplier> hasContactInfo(String contactInfo) {
        return (root, query, criteriaBuilder) -> {
            if (contactInfo == null || contactInfo.trim().isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("contactInfo")),
                    "%" + contactInfo.toLowerCase() + "%"
            );
        };
    }

    public static Specification<Supplier> hasAddress(String address) {
        return (root, query, criteriaBuilder) -> {
            if (address == null || address.trim().isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("address")),
                    "%" + address.toLowerCase() + "%"
            );
        };
    }

    public static Specification<Supplier> hasEmail(String email) {
        return (root, query, criteriaBuilder) -> {
            if (email == null || email.trim().isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("email")),
                    "%" + email.toLowerCase() + "%"
            );
        };
    }
    public static Specification<Supplier> hasPhoneNumber(String phoneNumber) {
        return (root, query, criteriaBuilder) -> {
            if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("phoneNumber")),
                    "%" + phoneNumber.toLowerCase() + "%"
            );
        };
    }
    public static Specification<Supplier> isEnabled() {
        return (root, query, criteriaBuilder) -> criteriaBuilder.isTrue(root.get("enabled"));
    }
}
