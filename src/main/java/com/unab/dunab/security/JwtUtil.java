package com.unab.dunab.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

/**
 * Utilidad JWT para generar, validar y extraer información de tokens.
 * El token contiene: userId, email, role, y expiración.
 */
@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private Long expiration;

    private SecretKey getSigningKey() {
        byte[] keyBytes = secret.getBytes();
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /** Genera un token JWT para el usuario autenticado */
    public String generateToken(Long userId, String email, String role) {
        return Jwts.builder()
                .subject(email)
                .claim("userId", userId)
                .claim("role", role)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey())
                .compact();
    }

    /** Extrae el email (subject) del token */
    public String extractEmail(String token) {
        return getClaims(token).getSubject();
    }

    /** Extrae el userId del token */
    public Long extractUserId(String token) {
        return getClaims(token).get("userId", Long.class);
    }

    /** Extrae el rol del token */
    public String extractRole(String token) {
        return getClaims(token).get("role", String.class);
    }

    /** Verifica si el token es válido y no expiró */
    public boolean isTokenValid(String token) {
        try {
            Claims claims = getClaims(token);
            return !claims.getExpiration().before(new Date());
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    private Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
