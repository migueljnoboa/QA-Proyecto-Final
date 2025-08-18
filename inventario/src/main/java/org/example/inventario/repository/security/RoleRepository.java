package org.example.inventario.repository.security;

import org.example.inventario.model.entity.security.Role;
import org.example.inventario.model.entity.security.User;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    List<Role> findAllByUsersAndEnabledIsTrue(List<User> users);

    @EntityGraph(attributePaths = {"permits", "users"})
    Page<Role> findAllByEnabledIsTrue(Pageable pageable);

    boolean existsByName(String name);

    @EntityGraph(attributePaths = {"permits", "users"})
    Role findByName(String name);

    @EntityGraph(attributePaths = {"permits", "users"})
    Page<Role> findAll(Specification<Role> spec, Pageable pageable);

    @NotNull
    @EntityGraph(attributePaths = {"permits", "users"})
    java.util.Optional<Role> findById(@NotNull Long id);
}
