package org.example.inventario.configuration;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.example.inventario.exception.MyException;
import org.example.inventario.exception.ResponseMessage;
import org.example.inventario.model.dto.scurity.JWTAuthentication;
import org.example.inventario.model.entity.security.User;
import org.example.inventario.service.security.AuthenticationService;
import org.example.inventario.utils.Utlity;
import org.springframework.http.MediaType;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;

@Component
@RequiredArgsConstructor
@Slf4j
public class JWTAuthFilter extends OncePerRequestFilter {

    private final AuthenticationService authenticacionService;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        return !request.getRequestURI().startsWith("/api/") ||
                SecurityConfig.EXCLUDE_ROUTES.stream().anyMatch(matcher -> matcher.matches(request));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            final String authHeader = request.getHeader("Authorization");
            final String empresaHeader = request.getHeader("Empresa-UUID");
            final String jwt;

            if (StringUtils.isBlank(authHeader) || !authHeader.startsWith("Bearer ")) {
                throw new MyException(MyException.ERROR_JWT_NOT_FOUND, "Access Denied: Bearer Token not found in Authorization header.");
            }

            jwt = authHeader.substring(7);

            User user = authenticacionService.getUserByToken(jwt);

            Collection<GrantedAuthority> grantedAuthorities = AuthorityUtils.NO_AUTHORITIES;
            if (user != null) {
                grantedAuthorities = SecurityUtils.getGrantedAuthorities(authenticacionService.listPermits(user));
            }

            JWTAuthentication authToken = new JWTAuthentication(jwt, user, grantedAuthorities);
            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            SecurityContextHolder.getContext().setAuthentication(authToken);

            filterChain.doFilter(request, response);
        } catch (MyException e) {
            log.warn("Error checking th jwt{}", e.getMessage());

            response.setStatus(e.getHttpStatus().value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);

            PrintWriter writer = response.getWriter();
            writer.print(Utlity.convertToJson(ResponseMessage.errorMyExcepcion(e)));
            writer.flush();
            writer.close();
        }
    }
}
