package org.example.inventario.model.specification.user;

import org.apache.commons.lang3.StringUtils;
import org.example.inventario.model.entity.security.Role;
import org.example.inventario.model.entity.security.User;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.JoinType;
import java.util.Collection;

public class UserSpecification {
    private UserSpecification() {}

    public static Specification<User> hasUsernameLike(String username) {
        if (StringUtils.isBlank(username)) return null;
        String like = "%" + username.trim().toLowerCase() + "%";
        return (root, query, cb) -> cb.like(cb.lower(root.get("username")), like);
    }

    public static Specification<User> hasEmailLike(String email) {
        if (StringUtils.isBlank(email)) return null;
        String like = "%" + email.trim().toLowerCase() + "%";
        return (root, query, cb) -> cb.like(cb.lower(root.get("email")), like);
    }

    /** Matches users that have ANY of the given roles. */
    public static Specification<User> hasAnyRole(Collection<Role> roles) {
        if (roles == null || roles.isEmpty()) return null;
        return (root, query, cb) -> root.join("roles", JoinType.LEFT).in(roles);
    }

    public static Specification<User> isEnabled() {
        return (root, query, cb) -> cb.isTrue(root.get("enabled"));
    }
}
