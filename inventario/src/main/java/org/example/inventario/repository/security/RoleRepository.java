package org.example.inventario.repository.security;

import org.example.inventario.model.entity.security.Role;
import org.example.inventario.model.entity.security.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    List<Role> findAllByUsersAndEnabledIsTrue(User user);

    boolean existsByName(String name);

    Role findByName(String name);
}
