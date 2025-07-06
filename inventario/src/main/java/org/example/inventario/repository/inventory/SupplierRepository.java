package org.example.inventario.repository.inventory;

import org.example.inventario.model.entity.inventory.Supplier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SupplierRepository extends JpaRepository<Supplier, Long> {

    Page<Supplier> findAllByEnabledIsTrue(Pageable pageable);
}
