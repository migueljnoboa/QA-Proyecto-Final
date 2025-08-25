package org.example.inventario.model.dto.api.permit;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.inventario.model.dto.inventory.ReturnList;
import org.example.inventario.model.entity.security.Role;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PermitRoleApi {
    private Long id;
    private String name;
    private String description;

    public static PermitRoleApi from(Role role){
        if (role == null) {
            return null;
        }
        PermitRoleApi permitRoleApi = new PermitRoleApi();

        permitRoleApi.setId(role.getId());
        permitRoleApi.setName(role.getName());
        permitRoleApi.setDescription(role.getDescription());

        return permitRoleApi;
    }

    public static List<PermitRoleApi> from(List<Role> roleList){
        List<PermitRoleApi> permitRoleApiList = new ArrayList<>();
        for (Role role : roleList) {
            PermitRoleApi permitRoleApi = PermitRoleApi.from(role);
            permitRoleApiList.add(permitRoleApi);
        }
        return permitRoleApiList;
    }

    public static ReturnList<PermitRoleApi> from(ReturnList<Role> roleReturnList){
        ReturnList<PermitRoleApi> result = new ReturnList<>();
        result.setPage(roleReturnList.getPage());
        result.setPageSize(roleReturnList.getPageSize());
        result.setTotalElements(roleReturnList.getTotalElements());
        result.setTotalPages(roleReturnList.getTotalPages());
        result.setData(from(roleReturnList.getData()));
        return result;
    }



}
