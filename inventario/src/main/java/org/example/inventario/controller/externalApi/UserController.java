package org.example.inventario.controller.externalApi;

import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.example.inventario.model.dto.api.user.UserApi;
import org.example.inventario.model.dto.inventory.ReturnList;
import org.example.inventario.model.entity.security.Permit;
import org.example.inventario.model.entity.security.User;
import org.example.inventario.service.security.UserService;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Secured({Permit.USER_VIEW})
    @GetMapping("")
    public ReturnList<UserApi> getUsers(Pageable pageable) {
        return UserApi.from(userService.findAllUsers(pageable));
    }

    @Secured({Permit.USER_VIEW})
    @GetMapping("/{id}")
    public UserApi getUserById(
            @PathVariable(name = "id")
            @Parameter(description = "User ID.", example = "1") Long id) {
        return UserApi.from(userService.findById(id));
    }

    @Secured({Permit.USER_VIEW})
    @GetMapping("/username/{username}")
    public UserApi getUserByUsername(
            @PathVariable(name = "username")
            @Parameter(description = "Username.", example = "admin") String username) {
        return UserApi.from(userService.findByUsername(username));
    }

    @Secured({Permit.USER_CREATE})
    @PostMapping("")
    public UserApi createUser(@RequestBody User user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        return UserApi.from(userService.createUser(user));
    }

    @Secured({Permit.USER_EDIT})
    @PutMapping("/{id}")
    public UserApi updateUser(
            @PathVariable(name = "id")
            @Parameter(description = "User ID.", example = "1") Long id,
            @RequestBody User user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        return UserApi.from(userService.updateUser(id, user));
    }

    @Secured({Permit.USER_DELETE})
    @DeleteMapping("/{id}")
    public UserApi deleteUser(
            @PathVariable(name = "id")
            @Parameter(description = "User ID.", example = "1") Long id) {
        userService.deleteUser(id);
        return UserApi.from(userService.findById(id));
    }

    @Secured({Permit.USER_EDIT})
    @PutMapping("/{id}/enable")
    public UserApi enableUser(
            @PathVariable(name = "id")
            @Parameter(description = "User ID.", example = "1") Long id) {
        userService.enableUser(id);
        return UserApi.from(userService.findById(id));
    }
}
