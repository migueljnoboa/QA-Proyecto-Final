package org.example.inventario.service.security;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.example.inventario.exception.MyException;
import org.example.inventario.model.dto.scurity.ReturnAuthentication;
import org.example.inventario.model.dto.scurity.UserAuthentication;
import org.example.inventario.model.entity.security.Permit;
import org.example.inventario.model.entity.security.Role;
import org.example.inventario.model.entity.security.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JWTService jwtService;
    private final RoleService roleService;

    public ReturnAuthentication authenticate(UserAuthentication userAuthentication) {
        String username = StringUtils.trim(userAuthentication.username());
        String password = userAuthentication.password();

        if (StringUtils.isBlank(username) || StringUtils.isBlank(password)) {
            throw new MyException(MyException.ERROR_AUTHENTICATION, "Username and password cannot be empty.");
        }
        User user = userService.findByUsername(username);
        if (user == null || !passwordEncoder.matches( password, user.getPassword())) {
            throw new MyException(MyException.ERROR_AUTHENTICATION, "Invalid username or password.");
        }
        String token = jwtService.generateJWT(user);
        return  new ReturnAuthentication(token);
    }

    public User getUserByToken(String token) {
        Claims claims;
        try {
            claims = jwtService.getClaimsPayloadFromJwtToken(token);
        } catch (Exception e) {
            throw new MyException(MyException.ERROR_JWT_VERIFICATION, "JWT invalido: " + e.getMessage());
        }

        if (claims.getExpiration().before(new Date())) {
            throw new MyException(MyException.ERROR_JWT_EXPIRED, "EL token JWT esta expirado.");
        }

        User user = userService.findByUsername(claims.getSubject());
        if (user == null) {
            throw new MyException(MyException.ERROR_USER_NOT_FOUND, "El usuario del token suministrado no existe.");
        }

        if (!user.isEnabled() ) {
            throw new MyException(MyException.ERROR_USER_TOKEN_DISABLED, String.format("El usuario %s esta desactivado.", user.getUsername()));
        }

        return user;
    }

    public Set<Permit> listPermits(User user) {
        Set<Permit> permits = new HashSet<>();
        if (user == null) {
            throw new MyException(MyException.ERROR_USER_NOT_FOUND, "El usuario no puede ser nulo.");
        }
        List<Role> roles = roleService.listAllRoleByUser(user);
        for (Role role : roles) {
            permits.addAll(role.getPermits());
        }
        return permits;
    }



}
