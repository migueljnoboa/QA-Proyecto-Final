package org.example.inventario.model.dto.scurity;

import lombok.Getter;
import org.example.inventario.model.entity.security.User;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

@Getter
public class JWTAuthentication extends AbstractAuthenticationToken {
    private final String token;

    private final User user;

    public JWTAuthentication(String token,  User user, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.token = token;
        this.user = user;

        setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return user;
    }

    @Override
    public String toString() {
        return user != null ? user.getUsername() : "";
    }
}
