package org.example.inventario.model.dto.api.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.inventario.model.dto.api.role.RolePermitApi;
import org.example.inventario.model.dto.inventory.ReturnList;
import org.example.inventario.model.entity.security.Role;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserRoleApi {
    private Long id;
    private String name;
    private String description;
    private List<RolePermitApi> permits;

    public static UserRoleApi from(Role role){
        if (role == null) {
            return null;
        }
        UserRoleApi roleApi = new UserRoleApi();
        roleApi.setId(role.getId());
        roleApi.setName(role.getName());
        roleApi.setDescription(role.getDescription());
        roleApi.setPermits(RolePermitApi.from(role.getPermits().stream().toList()));
        return roleApi;
    }

    public static List<UserRoleApi> from(List<Role> roleList){
        List<UserRoleApi> roleApiList = new ArrayList<>();
        for (Role role : roleList) {
            roleApiList.add(from(role));
        }
        return roleApiList;
    }

    public static ReturnList<UserRoleApi> from(ReturnList<Role> returnList){
        ReturnList<UserRoleApi> result = new ReturnList<>();
        result.setPage(returnList.getPage());
        result.setPageSize(returnList.getPageSize());
        result.setTotalElements(returnList.getTotalElements());
        result.setTotalPages(returnList.getTotalPages());
        result.setData(from(returnList.getData()));
        return result;
    }
}

