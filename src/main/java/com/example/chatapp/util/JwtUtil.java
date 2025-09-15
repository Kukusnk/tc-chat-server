package com.example.chatapp.util;

import com.example.chatapp.config.JwtProperties;
import com.example.chatapp.model.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Component
@Slf4j
public class JwtUtil {
    private final Key key;
    private final JwtProperties jwtProperties;

    @Autowired
    public JwtUtil(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
        this.key = Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8));
    }

    public String generateResetToken(String email) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + 10 * 60 * 1000); // 10 минут

        return Jwts.builder()
                .subject(email)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(key)
                .claim("type", "reset")
                .compact();
    }

    public boolean validateResetToken(String token) {
        try {
            Claims claims = getClaims(token);
            return "reset".equals(claims.get("type", String.class)) && !isTokenExpired(token);
        } catch (JwtException e) {
            return false;
        }
    }

    public String extractEmailFromResetToken(String token) {
        return getClaims(token).getSubject();
    }

    public String generateToken(String username, Set<Role> roles) {
        return generateToken(username, jwtProperties.getExpiration(), roles);
    }

    public String generateTestToken(String username, Set<Role> roles) {
        return generateToken(username, jwtProperties.getTestExpiration(), roles);
    }

    public String generateToken(String username, Long expiration, Set<Role> roles) {
        List<String> rolesNames = roles.stream()
                .map(Role::getName)
                .toList();

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration * 1000);

        return Jwts.builder()
                .subject(username)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(key)
                .claim("roles", rolesNames)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith((SecretKey) key)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            log.error("JWT token validation failed: {}", e.getMessage());
            return false;
        }
    }

    private Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith((SecretKey) key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String extractUsername(String token) {
        return getClaims(token).getSubject();
    }

    public boolean isTokenExpired(String token) {
        return getClaims(token).getExpiration().before(new Date());
    }

    public Date getExpirationDate(String token) {
        return getClaims(token).getExpiration();
    }
}
