package org.example.inventario.model.dto.api.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.inventario.model.dto.inventory.ReturnList;
import org.example.inventario.model.entity.security.User;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserApi {

    private Long id;
    private String username;
    private String email;
    private Set<UserRoleApi> roles;

    public static UserApi from(User user){
        if (user == null) {
            return null;
        }
        UserApi userApi = new UserApi();
        userApi.setId(user.getId());
        userApi.setUsername(user.getUsername());
        userApi.setEmail(user.getEmail());
        userApi.setRoles(user.getRoles().stream().map(UserRoleApi::from).collect(Collectors.toSet()));
        return userApi;
    }

    public static List<UserApi> from(List<User> userList){
        List<UserApi> userApis = new ArrayList<>();
        for (User user : userList) {
            userApis.add(from(user));
        }
        return userApis;
    }

    public static ReturnList<UserApi> from(ReturnList<User> returnList){
        ReturnList<UserApi> result = new ReturnList<>();
        result.setPage(returnList.getPage());
        result.setPageSize(returnList.getPageSize());
        result.setTotalElements(returnList.getTotalElements());
        result.setTotalPages(returnList.getTotalPages());
        result.setData(from(returnList.getData()));
        return result;
    }

}
