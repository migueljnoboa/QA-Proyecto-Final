package org.example.inventario.model.dto.api.permit;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.inventario.model.dto.inventory.ReturnList;
import org.example.inventario.model.entity.security.Permit;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PermitApi{
    private Long id;
    private String name;
    private List<PermitRoleApi> roles = new ArrayList<>();

    public static PermitApi from(Permit permit){
        if (permit == null) {
            return null;
        }
        PermitApi permitApi = new PermitApi();
        permitApi.setId(permit.getId());
        permitApi.setName(permit.getName());
        permitApi.setRoles(PermitRoleApi.from(permit.getRoles()));
        return permitApi;
    }

    public static List<PermitApi> from(List<Permit> permitList){

        List<PermitApi> permitApiList = new ArrayList<>();
        for (Permit permit : permitList) {
            permitApiList.add(from(permit));
        }
        return permitApiList;
    }

    public static ReturnList<PermitApi> from(ReturnList<Permit> returnList){
        ReturnList<PermitApi> result = new ReturnList<>();
        result.setPage(returnList.getPage());
        result.setPageSize(returnList.getPageSize());
        result.setTotalElements(returnList.getTotalElements());
        result.setTotalPages(returnList.getTotalPages());
        result.setData(from(returnList.getData()));
        return result;
    }
}
