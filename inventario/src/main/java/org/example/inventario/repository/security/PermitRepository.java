package org.example.inventario.repository.security;

import org.example.inventario.model.entity.security.Permit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PermitRepository extends JpaRepository<Permit, Long> {
    boolean existsByName(String name);

    Permit findByName(String name);

    List<Permit> findAllByEnabledIsTrue();
}
