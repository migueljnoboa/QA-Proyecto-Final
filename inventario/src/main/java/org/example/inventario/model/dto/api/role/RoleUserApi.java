package org.example.inventario.model.dto.api.role;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.inventario.model.dto.inventory.ReturnList;
import org.example.inventario.model.entity.security.User;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RoleUserApi {
    private Long id;
    private String username;
    private String email;

    public static RoleUserApi from(User user){
        if (user == null) {
            return null;
        }
        RoleUserApi roleUserApi = new RoleUserApi();
        roleUserApi.setId(user.getId());
        roleUserApi.setUsername(user.getUsername());
        roleUserApi.setEmail(user.getEmail());
        return roleUserApi;
    }

    public static List<RoleUserApi> from(List<User> userList){
        List<RoleUserApi> roleUserApis = new ArrayList<>();
        for (User user : userList) {
            roleUserApis.add(from(user));
        }
        return roleUserApis;
    }

    public static ReturnList<RoleUserApi> from(ReturnList<User> returnList){
        ReturnList<RoleUserApi> result = new ReturnList<>();
        result.setPage(returnList.getPage());
        result.setPageSize(returnList.getPageSize());
        result.setTotalElements(returnList.getTotalElements());
        result.setTotalPages(returnList.getTotalPages());
        result.setData(from(returnList.getData()));
        return result;
    }



}