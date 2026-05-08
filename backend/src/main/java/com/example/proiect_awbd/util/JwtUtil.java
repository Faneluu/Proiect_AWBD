package com.example.proiect_awbd.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

/**
 * JwtUtil este o clasă utilitară pentru generarea și interpretarea tokenurilor JWT.
 *
 * <p>Această clasă folosește biblioteca jjwt pentru a crea și valida tokenurile JWT,
 * gestionând date precum numele utilizatorului și rolurile asociate.</p>
 */
@Component
public class JwtUtil {


    /**
     * Cheia secretă utilizată pentru semnarea tokenurilor JWT, încărcată din variabilele de mediu.
     */
    private static final String SECRET_KEY = System.getenv("JWT_SECRET");

    /**
     * Timpul de expirare al tokenurilor JWT în milisecunde (24 de ore).
     */
    private static final long EXPIRATION_TIME = 86400000; // 24 hours

    /**
     * Generează un token JWT pentru un utilizator specific.
     *
     * <p>Tokenul include numele utilizatorului, rolurile acestuia și un timp de expirare prestabilit.
     * Este semnat utilizând algoritmul HS256 și cheia secretă.</p>
     *
     * @param username numele utilizatorului pentru care se generează tokenul
     * @param roles lista de roluri asociate utilizatorului
     * @return un token JWT sub formă de string
     */
    public static String generateToken(String username, List<String> roles) {
        return Jwts.builder()
                .setSubject(username)
                .claim("roles", roles)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }

    /**
     * Extrage numele utilizatorului dintr-un token JWT.
     *
     * <p>Tokenul este analizat folosind cheia secretă pentru a extrage subiectul (numele utilizatorului).</p>
     *
     * @param token tokenul JWT din care se extrage numele utilizatorului
     * @return numele utilizatorului conținut în token
     */
    public static String extractUsername(String token) {
        return Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    /**
     * Extrage lista de roluri dintr-un token JWT.
     *
     * <p>Rolurile sunt stocate ca o revendicare ("claim") în token și sunt extrase
     * folosind cheia secretă.</p>
     *
     * @param token tokenul JWT din care se extrag rolurile
     * @return o listă de stringuri care reprezintă rolurile utilizatorului
     */
    public static List<String> extractRoles(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(SECRET_KEY) // Utilizarea aceleiași chei secrete pentru validare
                .parseClaimsJws(token)
                .getBody();
        return claims.get("roles", List.class); // Extrage revendicarea "roles"
    }
}

