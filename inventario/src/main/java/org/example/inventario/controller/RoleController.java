package org.example.inventario.controller;

import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.example.inventario.model.dto.inventory.ReturnList;
import org.example.inventario.model.entity.security.Role;
import org.example.inventario.service.security.RoleService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/role")
@RequiredArgsConstructor
public class RoleController {
    private final RoleService roleService;

    @GetMapping("")
    public ReturnList<Role> getAllRoles(@RequestParam(name = "page", defaultValue = "0") @Parameter(description = "Page Number.", example = "0") int page,
                                        @RequestParam(name = "size", defaultValue = "50") @Parameter(description = "Page Size.", example = "50") int size ){
        return roleService.listAllRole(page, size);

    }

    @GetMapping("/{id}")
    public Role getRoleById(@PathVariable(name = "id") @Parameter(description = "Role ID.", example = "1") Long id) {
        return roleService.findById(id);
    }

    @PostMapping("/")
    public Role createRole(@RequestBody Role role) {
        if (role == null || role.getName() == null || role.getName().isBlank()) {
            throw new IllegalArgumentException("Role and its name cannot be null or empty");
        }
        return roleService.createRole(role);
    }

    @PutMapping("/{id}")
    public Role updateRole(@PathVariable(name = "id") @Parameter(description = "Role ID.", example = "1") Long id,
                           @RequestBody Role role) {
        if (role == null || role.getName() == null || role.getName().isBlank()) {
            throw new IllegalArgumentException("Role and its name cannot be null or empty");
        }
        return roleService.updateRole(id, role);
    }

    @DeleteMapping("/{id}")
    public Role deleteRole(@PathVariable(name = "id") @Parameter(description = "Role ID.", example = "1") Long id) {
       return roleService.deleteRole(id);
    }



}
