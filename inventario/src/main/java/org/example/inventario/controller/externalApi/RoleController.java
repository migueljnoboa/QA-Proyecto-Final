package org.example.inventario.controller.externalApi;

import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.example.inventario.model.dto.api.role.RoleApi;
import org.example.inventario.model.dto.inventory.ReturnList;
import org.example.inventario.model.entity.security.Permit;
import org.example.inventario.model.entity.security.Role;
import org.example.inventario.service.security.RoleService;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/role")
@RequiredArgsConstructor
public class RoleController {
    private final RoleService roleService;

    @Secured({Permit.ROLE_VIEW})
    @GetMapping("")
    public ReturnList<RoleApi> getAllRoles(Pageable pageable){
        return RoleApi.from(roleService.listAllRole(pageable));
    }

    @Secured({Permit.ROLE_VIEW})
    @GetMapping("/{id}")
    public RoleApi getRoleById(@PathVariable(name = "id") @Parameter(description = "Role ID.", example = "1") Long id) {
        return RoleApi.from(roleService.findById(id));
    }

    @Secured({Permit.ROLE_CREATE})
    @PostMapping("/")
    public RoleApi createRole(@RequestBody Role role) {
        if (role == null || role.getName() == null || role.getName().isBlank()) {
            throw new IllegalArgumentException("Role and its name cannot be null or empty");
        }
        return RoleApi.from(roleService.createRole(role));
    }

    @Secured({Permit.ROLE_EDIT})
    @PutMapping("/{id}")
    public RoleApi updateRole(@PathVariable(name = "id") @Parameter(description = "Role ID.", example = "1") Long id,
                           @RequestBody Role role) {
        if (role == null || role.getName() == null || role.getName().isBlank()) {
            throw new IllegalArgumentException("Role and its name cannot be null or empty");
        }
        return RoleApi.from(roleService.updateRole(id, role));
    }

    @Secured({Permit.ROLE_DELETE})
    @DeleteMapping("/{id}")
    public RoleApi deleteRole(@PathVariable(name = "id") @Parameter(description = "Role ID.", example = "1") Long id) {
       return RoleApi.from(roleService.deleteRole(id));
    }



}
