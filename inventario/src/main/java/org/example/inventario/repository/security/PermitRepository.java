package org.example.inventario.repository.security;

import org.example.inventario.model.entity.security.Permit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PermitRepository extends JpaRepository<Permit, Long> {
}
