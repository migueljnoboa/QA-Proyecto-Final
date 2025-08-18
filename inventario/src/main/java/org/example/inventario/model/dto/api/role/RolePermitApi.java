package org.example.inventario.model.dto.api.role;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.inventario.model.dto.api.permit.PermitApi;
import org.example.inventario.model.dto.api.permit.PermitRoleApi;
import org.example.inventario.model.dto.inventory.ReturnList;
import org.example.inventario.model.entity.security.Permit;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RolePermitApi {
    private Long id;
    private String name;

    public static RolePermitApi from(Permit permit){
        if (permit == null) {
            return null;
        }
        RolePermitApi rolePermitApi = new RolePermitApi();
        rolePermitApi.setId(permit.getId());
        rolePermitApi.setName(permit.getName());
        return rolePermitApi;
    }

    public static List<RolePermitApi> from(List<Permit> permitList){

        List<RolePermitApi> rolePermitApis = new ArrayList<>();
        for (Permit permit : permitList) {
            rolePermitApis.add(from(permit));
        }
        return rolePermitApis;
    }

    public static ReturnList<RolePermitApi> from(ReturnList<Permit> returnList){
        ReturnList<RolePermitApi> result = new ReturnList<>();
        result.setPage(returnList.getPage());
        result.setPageSize(returnList.getPageSize());
        result.setTotalElements(returnList.getTotalElements());
        result.setTotalPages(returnList.getTotalPages());
        result.setData(from(returnList.getData()));
        return result;
    }



}