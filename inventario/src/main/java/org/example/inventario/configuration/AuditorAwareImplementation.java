package org.example.inventario.configuration;

import lombok.NonNull;
import org.example.inventario.model.dto.scurity.JWTAuthentication;
import org.example.inventario.model.entity.security.User;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

public class AuditorAwareImplementation implements AuditorAware<String> {


    @Override
    @NonNull
    public Optional<String> getCurrentAuditor() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated() || auth instanceof AnonymousAuthenticationToken) {
            return Optional.of("system");
        }
        if (auth instanceof JWTAuthentication jwtAuth) {
            User u = jwtAuth.getUser();
            if (u != null && u.getUsername() != null && !u.getUsername().isBlank()) {
                return Optional.of(u.getUsername());
            }
        }

        Object principal = auth.getPrincipal();
        if (principal instanceof UserDetails ud && ud.getUsername() != null) {
            return Optional.of(ud.getUsername());
        }
        if (principal instanceof User u && u.getUsername() != null) {
            return Optional.of(u.getUsername());
        }
        if (principal instanceof String s && !s.isBlank()) {
            return Optional.of(s);
        }
        String name = auth.getName();
        return (name == null || name.isBlank()) ? Optional.of("system") : Optional.of(name);
    }
}
