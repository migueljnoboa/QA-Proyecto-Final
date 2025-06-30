package org.example.inventario.configuration;

import org.example.inventario.model.entity.security.Permit;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
public class SecurityUtils {

    public static Set<GrantedAuthority> getGrantedAuthorities(Set<Permit> permits) {
        Set<GrantedAuthority> updatedAuthorities = new HashSet<>();
        for (Permit per : permits) {
            updatedAuthorities.add(new SimpleGrantedAuthority("ROLE_" + per.getName()));
        }

        return updatedAuthorities;
    }
}
