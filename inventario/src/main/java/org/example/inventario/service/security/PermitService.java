package org.example.inventario.service.security;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.example.inventario.model.entity.inventory.Product;
import org.example.inventario.model.entity.security.Permit;
import org.example.inventario.model.specification.permit.PermitSpecification;
import org.example.inventario.model.specification.product.ProductSpecification;
import org.example.inventario.repository.security.PermitRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PermitService {
    private final PermitRepository permitRepository;

    public void createDefaultPermitsIfNotExists() {
        String[] defaultPermits = {
                Permit.DASHBOARD_MENU,

                Permit.SUPPLIERS_MENU,
                Permit.SUPPLIER_CREATE,
                Permit.SUPPLIER_EDIT,
                Permit.SUPPLIER_DELETE,
                Permit.SUPPLIER_VIEW,

                Permit.PRODUCTS_MENU,
                Permit.PRODUCT_CREATE,
                Permit.PRODUCT_EDIT,
                Permit.PRODUCT_DELETE,
                Permit.PRODUCT_VIEW,

                Permit.USERS_MENU,
                Permit.USER_CREATE,
                Permit.USER_EDIT,
                Permit.USER_DELETE,

                Permit.ROLES_MENU,
                Permit.ROLE_CREATE,
                Permit.ROLE_EDIT,
                Permit.ROLE_DELETE
        };

        for (String permitName : defaultPermits) {
            if (!permitRepository.existsByName(permitName)) {
                Permit permit = new Permit();
                permit.setName(permitName);
                permitRepository.save(permit);
            }
        }
    }

    public List<Permit> findAll() {
        return permitRepository.findAllByEnabledIsTrue();
    }

    public Permit findByName(String name) {
        if(StringUtils.isBlank(name)) {
            throw new IllegalArgumentException("Permit name cannot be null or empty.");
        }
        return permitRepository.findByName(name);
    }

    public Page<Permit> searchPermit(String name, PageRequest pageable) {
        Specification<Permit> spec = Specification.not(null);
        spec = spec.and(PermitSpecification.hasName(name));
        spec = spec.and(PermitSpecification.isEnabled());
        return permitRepository.findAll(spec, pageable);

    }
}
