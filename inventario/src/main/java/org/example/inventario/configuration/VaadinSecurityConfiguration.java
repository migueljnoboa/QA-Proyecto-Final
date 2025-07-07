package org.example.inventario.configuration;

import com.vaadin.flow.spring.security.VaadinWebSecurity;
import lombok.RequiredArgsConstructor;
import org.example.inventario.service.security.ValidateUserService;
import org.example.inventario.ui.view.login.Login;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.web.util.pattern.PathPatternParser;

import java.util.List;


@RequiredArgsConstructor
@Configuration
@Order(2)
public class VaadinSecurityConfiguration extends VaadinWebSecurity {

    private final ValidateUserService userDetailsService;
    private final PasswordEncodingConfig passwordEncodingConfig;

    public static final List<PathPatternRequestMatcher> EXCLUDE_ROUTES = List.of(
            PathPatternRequestMatcher.withDefaults().matcher("/images/*"),
            PathPatternRequestMatcher.withDefaults().matcher("/application/health/**"),
            PathPatternRequestMatcher.withDefaults().matcher("/swagger-ui/**"),
            PathPatternRequestMatcher.withDefaults().matcher("/v3/**"),
            PathPatternRequestMatcher.withDefaults().matcher("/css/**"),
            PathPatternRequestMatcher.withDefaults().matcher("/js/**"),
            PathPatternRequestMatcher.withDefaults().matcher("/font-awesome/**"),
            PathPatternRequestMatcher.withDefaults().matcher("/img/**"),
            PathPatternRequestMatcher.withDefaults().matcher("/fonts/**")
    );

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authenticationProvider(authenticationProvider());

        http.authorizeHttpRequests(auth -> auth
                .requestMatchers(EXCLUDE_ROUTES.toArray(new PathPatternRequestMatcher[0])).permitAll()
                .requestMatchers("/login").permitAll()
        );

        super.configure(http);

        setLoginView(http, Login.class);
    }

    @Bean("authProvider")
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncodingConfig.passwordEncoder());
        return authProvider;
    }
}
