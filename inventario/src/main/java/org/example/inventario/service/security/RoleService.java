package org.example.inventario.service.security;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.example.inventario.exception.MyException;
import org.example.inventario.model.dto.inventory.ReturnList;
import org.example.inventario.model.entity.inventory.Product;
import org.example.inventario.model.entity.security.Permit;
import org.example.inventario.model.entity.security.Role;
import org.example.inventario.model.entity.security.User;
import org.example.inventario.model.specification.product.ProductSpecification;
import org.example.inventario.model.specification.role.RoleSpecification;
import org.example.inventario.repository.security.RoleRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RoleService {
    private final PermitService permitService;
    private final RoleRepository roleRepository;

    public ReturnList<Role> listAllRole( int page , int size) {
        PageRequest pageable = PageRequest.of(page, size);
        Page<Role> list = roleRepository.findAllByEnabledIsTrue(pageable);
        ReturnList<Role> result = new ReturnList<>();
        result.setPage(page);
        result.setPageSize(size);
        result.setTotalElements((int) list.getTotalElements());
        result.setTotalPages(list.getTotalPages());
        result.setData(list.getContent());
        return result;
    }
    public Role findById(Long id) {
        if(id == null) {
            throw new MyException(MyException.ERROR_VALIDATION, "Role ID cannot be null");
        }
        return roleRepository.findById(id)
                .orElseThrow(() -> new MyException(MyException.ERROR_NOT_FOUND, "Role not found with ID: " + id));

    }

    public List<Role> listAllRoleByUser(User user){
        if(user == null){
            throw new MyException(MyException.ERROR_USER_NOT_FOUND, "The user cannot be null.");
        }
        return roleRepository.findAllByUsersAndEnabledIsTrue(user);
    }
    public void createDefaultRolesIfNotExists(){
        if (!roleRepository.existsByName(Role.ADMIN_ROLE)) {
            Role adminRole = new Role();
            adminRole.setName(Role.ADMIN_ROLE);
            adminRole.setDescription("Administrator role with all permissions");
            adminRole.setPermits(permitService.findAll());
            roleRepository.save(adminRole);
        }

        if (!roleRepository.existsByName(Role.USER_ROLE)) {
            Role userRole = new Role();
            userRole.setName(Role.USER_ROLE);
            userRole.setPermits(List.of(permitService.findByName(Permit.DASHBOARD_MENU),
                    permitService.findByName(Permit.PRODUCTS_MENU),
                    permitService.findByName(Permit.PRODUCT_VIEW)));
            userRole.setDescription("Regular user role with limited permissions");
            roleRepository.save(userRole);
        }
    }

    public Role findByName(String name) {
        if(StringUtils.isBlank(name)){
            throw new MyException(MyException.ERROR_VALIDATION, "Role name cannot be null or empty.");
        }
        return roleRepository.findByName(name);
    }

    public Role createRole(Role role) {
        if (role == null || StringUtils.isBlank(role.getName())) {
            throw new MyException(MyException.ERROR_VALIDATION, "Role and its name cannot be null or empty.");
        }
        if (roleRepository.existsByName(role.getName())) {
            throw new MyException(MyException.ERROR_VALIDATION, "Role with this name already exists.");
        }
        if (role.getPermits() == null || role.getPermits().isEmpty()) {
            throw new MyException(MyException.ERROR_VALIDATION, "Role must have at least one permit.");
        }
        List<Permit> permits = extractPermits(role);
        role.setPermits(permits);
        return roleRepository.save(role);
    }

    public Role updateRole(Long id, Role role) {
        if (role == null || StringUtils.isBlank(role.getName())) {
            throw new MyException(MyException.ERROR_VALIDATION, "Role and its name cannot be null or empty.");
        }
        Role existingRole = findById(id);
        if (existingRole == null) {
            throw new MyException(MyException.ERROR_NOT_FOUND, "Role not found with ID: " + id);
        }
        if (!existingRole.getName().equals(role.getName()) && roleRepository.existsByName(role.getName())) {
            throw new MyException(MyException.ERROR_VALIDATION, "Role with this name already exists.");
        }
        List<Permit> permits = extractPermits(role);
        existingRole.setName(role.getName());
        existingRole.setDescription(role.getDescription());
        existingRole.setPermits(permits);
        existingRole.setEnabled(role.isEnabled());
        return roleRepository.save(existingRole);
    }

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

    public Role deleteRole(Long id) {
        Role role = findById(id);
        if (role == null) {
            throw new MyException(MyException.ERROR_NOT_FOUND, "Role not found with ID: " + id);
        }
        role.setEnabled(false);
        return roleRepository.save(role);
    }

    public Page<Role> searchRole(String name, Permit permit, Pageable pageable) {
        Specification<Role> spec = Specification.not(null);
        spec = spec.and(RoleSpecification.hasName(name));


        spec = spec.and(RoleSpecification.hasPermit(permit));


        spec = spec.and(RoleSpecification.isEnabled());

        return roleRepository.findAll(spec, pageable);
    }
}
