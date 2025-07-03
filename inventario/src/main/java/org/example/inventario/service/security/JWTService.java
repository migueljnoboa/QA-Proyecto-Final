package org.example.inventario.service.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.apache.commons.lang3.StringUtils;
import org.example.inventario.model.entity.Base;
import org.example.inventario.model.entity.security.Permit;
import org.example.inventario.model.entity.security.Role;
import org.example.inventario.model.entity.security.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.function.Function;
@Service
public class JWTService {
    @Value("${jwt.secret}")
    private String key;

    public String generateJWT(User user) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + 3600000L);

        return Jwts.builder()
                .subject(user.getUsername())
                .issuedAt(now)
                .expiration(expiryDate)
                .claim("userID", user.getId())
                .claim("email", user.getEmail())
                .claim("name", user.getUsername())
                .claim("permit", StringUtils.join(user.getRoles().stream()
                        .filter(Base::isEnabled)
                        .flatMap(role -> role.getPermits().stream())
                        .map(Permit::getName)
                        .toList(), ","))
                .signWith(getJwtSecret())
                .compact();
    }

    public SecretKey getJwtSecret() {
        return Keys.hmacShaKeyFor(key.getBytes());
    }


    public Claims getClaimsPayloadFromJwtToken(String token) {
        return Jwts.parser()
                .verifyWith(getJwtSecret())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getJwtSecret())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }


}
