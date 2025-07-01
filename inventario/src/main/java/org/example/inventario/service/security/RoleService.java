package org.example.inventario.service.security;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.example.inventario.exception.MyException;
import org.example.inventario.model.entity.security.Permit;
import org.example.inventario.model.entity.security.Role;
import org.example.inventario.model.entity.security.User;
import org.example.inventario.repository.security.RoleRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoleService {
    private final PermitService permitService;
    private final RoleRepository roleRepository;

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
}
