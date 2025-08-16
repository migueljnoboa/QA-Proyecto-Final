package org.example.inventario.configuration;

import lombok.NonNull;
import org.example.inventario.model.entity.security.User;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

public class AuditorAwareImplementation implements AuditorAware<String> {


    @Override
    @NonNull
    public Optional<String> getCurrentAuditor() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if ((authentication == null) || !authentication.isAuthenticated()) {
            return Optional.empty();
        }

        return Optional.ofNullable(((User) authentication.getPrincipal()).getUsername());
    }
}
