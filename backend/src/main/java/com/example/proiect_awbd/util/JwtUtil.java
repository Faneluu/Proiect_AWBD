package com.example.proiect_awbd.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

/**
 * JwtUtil este o clasă utilitară pentru generarea și interpretarea tokenurilor JWT.
 */
@Component
public class JwtUtil {

    /**
     * Cheia secretă utilizată pentru semnarea tokenurilor JWT, încărcată din application.properties.
     */
    private final String secretKey;

    private static final long EXPIRATION_TIME = 86400000; // 24 hours

    public JwtUtil(@Value("${app.jwt.secret}") String secretKey) {
        this.secretKey = secretKey;
    }

    /**
     * Generează un token JWT pentru un utilizator specific.
     */
    public String generateToken(String username, List<String> roles) {
        return Jwts.builder()
                .setSubject(username)
                .claim("roles", roles)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    /**
     * Extrage numele utilizatorului dintr-un token JWT.
     */
    public String extractUsername(String token) {
        return Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    /**
     * Extrage lista de roluri dintr-un token JWT.
     */
    public List<String> extractRoles(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody();
        return claims.get("roles", List.class);
    }
}