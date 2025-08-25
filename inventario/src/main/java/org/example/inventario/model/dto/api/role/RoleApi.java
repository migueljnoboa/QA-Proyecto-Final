package org.example.inventario.model.dto.api.role;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.inventario.model.dto.api.ProductApi;
import org.example.inventario.model.dto.api.SupplierApi;
import org.example.inventario.model.dto.api.permit.PermitApi;
import org.example.inventario.model.dto.inventory.ReturnList;
import org.example.inventario.model.entity.inventory.Product;
import org.example.inventario.model.entity.security.Role;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RoleApi {
    private Long id;
    private String name;
    private String description;
    private List<RolePermitApi> permits;
    private List<RoleUserApi> users;

    public static RoleApi from(Role role){
        if (role == null) {
            return null;
        }
        RoleApi roleApi = new RoleApi();
        roleApi.setId(role.getId());
        roleApi.setName(role.getName());
        roleApi.setDescription(role.getDescription());
        roleApi.setPermits(RolePermitApi.from(role.getPermits().stream().toList()));
        roleApi.setUsers(RoleUserApi.from(role.getUsers().stream().toList()));
        return roleApi;
    }

    public static List<RoleApi> from(List<Role> roleList){
        List<RoleApi> roleApiList = new ArrayList<>();
        for (Role role : roleList) {
            roleApiList.add(from(role));
        }
        return roleApiList;
    }

    public static ReturnList<RoleApi> from(ReturnList<Role> returnList){
        ReturnList<RoleApi> result = new ReturnList<>();
        result.setPage(returnList.getPage());
        result.setPageSize(returnList.getPageSize());
        result.setTotalElements(returnList.getTotalElements());
        result.setTotalPages(returnList.getTotalPages());
        result.setData(from(returnList.getData()));
        return result;
    }
}

