package org.example.inventario.service.security;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.example.inventario.exception.MyException;
import org.example.inventario.model.dto.inventory.ReturnList;
import org.example.inventario.model.entity.security.Permit;
import org.example.inventario.model.entity.security.Role;
import org.example.inventario.model.entity.security.User;
import org.example.inventario.model.specification.role.RoleSpecification;
import org.example.inventario.repository.security.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class RoleService {

    @Autowired private final PermitService permitService;
    @Autowired private final RoleRepository roleRepository;

    @Transactional(readOnly = true)
    public ReturnList<Role> listAllRole(Pageable pageable) {
        Page<Role> page = roleRepository.findAllByEnabledIsTrue(pageable);

        page.getContent().forEach(r -> {
            r.getPermits().size();
            r.getUsers().size();
        });

        ReturnList<Role> result = new ReturnList<>();
        result.setPage(pageable.getPageNumber());
        result.setPageSize(pageable.getPageSize());
        result.setTotalElements((int) page.getTotalElements());
        result.setTotalPages(page.getTotalPages());
        result.setData(page.getContent());
        return result;
    }

    @Transactional(readOnly = true)
    public Role findById(Long id) {
        if(id == null) {
            throw new MyException(MyException.ERROR_VALIDATION, "Role ID cannot be null");
        }
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new MyException(MyException.ERROR_NOT_FOUND, "Role not found with ID: " + id));
        // initialize collections needed by RoleApi
        role.getPermits().size();
        role.getUsers().size();
        return role;

    }

    @Transactional(readOnly = true)
    public List<Role> listAllRoleByUser(User user){
        if(user == null){
            throw new MyException(MyException.ERROR_USER_NOT_FOUND, "The user cannot be null.");
        }
        return roleRepository.findAllByUsersAndEnabledIsTrue(Set.of(user));
    }

    @Transactional
    public void createDefaultRolesIfNotExists(){

        Role adminRole = roleRepository.findByName(Role.ADMIN_ROLE);
        if (adminRole.getPermits().isEmpty()){
            adminRole.setPermits(new LinkedHashSet<>(permitService.findAll()));
            roleRepository.save(adminRole);
        }

        Role employeeRole = roleRepository.findByName(Role.EMPLOYEE_ROLE);
        if (employeeRole.getPermits().isEmpty()) {
            employeeRole.setPermits(new LinkedHashSet<>(Arrays.asList(
                    permitService.findByName(Permit.DASHBOARD_MENU),

                    permitService.findByName(Permit.SUPPLIERS_MENU),
                    permitService.findByName(Permit.SUPPLIER_VIEW),
                    permitService.findByName(Permit.SUPPLIER_CREATE),
                    permitService.findByName(Permit.SUPPLIER_EDIT),

                    permitService.findByName(Permit.PRODUCTS_MENU),
                    permitService.findByName(Permit.PRODUCT_CREATE),
                    permitService.findByName(Permit.PRODUCT_EDIT),
                    permitService.findByName(Permit.PRODUCT_VIEW),
                    permitService.findByName(Permit.PRODUCT_STOCK_CHANGE))));
            roleRepository.save(employeeRole);
        }

        Role userRole = roleRepository.findByName(Role.USER_ROLE);
        if (userRole.getPermits().isEmpty()){
            userRole.setPermits(new LinkedHashSet<>(Arrays.asList(
                    permitService.findByName(Permit.DASHBOARD_MENU),
                    permitService.findByName(Permit.PRODUCTS_MENU),
                    permitService.findByName(Permit.PRODUCT_VIEW),
                    permitService.findByName(Permit.SUPPLIER_VIEW))));
            roleRepository.save(userRole);
        }
    }

    @Transactional(readOnly = true)
    public Role findByName(String name) {
        if(StringUtils.isBlank(name)){
            throw new MyException(MyException.ERROR_VALIDATION, "Role name cannot be null or empty.");
        }
        return roleRepository.findByNameAndEnabledIsTrue(name);
    }

    @Transactional
    public Role createRole(Role role) {
        if (role == null || StringUtils.isBlank(role.getName())) {
            throw new MyException(MyException.ERROR_VALIDATION, "Role and its name cannot be null or empty.");
        }
        String normalized = role.getName().trim();
        if (roleRepository.existsByNameAndEnabledIsTrue(normalized)) {
            throw new MyException(MyException.ERROR_VALIDATION, "Role with this name already exists.");
        }
        if (role.getPermits() == null || role.getPermits().isEmpty()) {
            throw new MyException(MyException.ERROR_VALIDATION, "Role must have at least one permit.");
        }
        role.setName(normalized);
        List<Permit> permits = extractPermits(role);
        role.setPermits(new LinkedHashSet<>(permits));

        Role saved = roleRepository.save(role);
        saved.getPermits().size();
        saved.getUsers().size();
        return saved;
    }

    @Transactional
    public Role updateRole(Long id, Role role) {
        if (role == null || StringUtils.isBlank(role.getName())) {
            throw new MyException(MyException.ERROR_VALIDATION, "Role and its name cannot be null or empty.");
        }
        Role existingRole = findById(id);
        if (existingRole == null) {
            throw new MyException(MyException.ERROR_NOT_FOUND, "Role not found with ID: " + id);
        }
        if (!existingRole.getName().equals(role.getName())
                && roleRepository.existsByNameAndEnabledIsTrue(role.getName())) {
            throw new MyException(MyException.ERROR_VALIDATION, "Role with this name already exists.");
        }
        List<Permit> permits = extractPermits(role);
        existingRole.setName(role.getName());
        existingRole.setDescription(role.getDescription());
        existingRole.setPermits(new LinkedHashSet<>(permits));
        existingRole.setEnabled(role.isEnabled());

        Role updated = roleRepository.save(existingRole);
        updated.getPermits().size();
        updated.getUsers().size();
        return updated;
    }

    @Transactional(readOnly = true)
    public List<Permit> extractPermits(Role role){
        List<Permit> permits = new ArrayList<>();
        for (Permit permit : role.getPermits()) {
            Permit existingPermit = permitService.findByName(permit.getName());
            if (existingPermit == null) {
                throw new MyException(MyException.ERROR_NOT_FOUND, "Permit not found: " + permit.getName());
            }
            permits.add(existingPermit);
        }
        return permits;
    }

    @Transactional
    public Role deleteRole(Long id) {
        Role role = findById(id);
        if (role == null) {
            throw new MyException(MyException.ERROR_NOT_FOUND, "Role not found with ID: " + id);
        }
        role.setEnabled(false);
        Role saved = roleRepository.save(role);
        saved.getPermits().size();
        saved.getUsers().size();
        return saved;
    }

    @Transactional(readOnly = true)
    public Page<Role> searchRole(String name, Permit permit, Pageable pageable) {
        Specification<Role> spec = Specification.not(null);
        spec = spec.and(RoleSpecification.hasName(name));
        spec = spec.and(RoleSpecification.hasPermit(permit));
        spec = spec.and(RoleSpecification.isEnabled());
        return roleRepository.findAll(spec, pageable);
    }
}
