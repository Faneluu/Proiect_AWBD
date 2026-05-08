package com.example.proiect_awbd.util;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * JwtAuthorizationFilter este un filtru care se aplică o dată pe fiecare cerere și gestionează
 * autentificarea pe baza unui token JWT.
 *
 * <p>Filtrul verifică antetul "Authorization" al cererii, extrage și validează tokenul JWT,
 * iar apoi populează contextul de securitate cu informațiile utilizatorului (username și roluri).</p>
 */
@Component
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    /**
     * Utilitarul pentru gestionarea operațiunilor legate de tokenurile JWT.
     */

    private JwtUtil jwtUtil;

    @Autowired
    public JwtAuthorizationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    /**
     * Aplica filtrul pentru fiecare cerere HTTP pentru a autentifica utilizatorii pe baza unui token JWT.
     *
     * <p>Filtrul verifică dacă antetul "Authorization" conține un token valid. Dacă da, extrage
     * numele utilizatorului și rolurile din token, le transformă în autorități și creează un
     * obiect de tip {@link UsernamePasswordAuthenticationToken} pentru a popula contextul de securitate.</p>
     *
     * @param request obiectul {@link HttpServletRequest} care reprezintă cererea HTTP curentă
     * @param response obiectul {@link HttpServletResponse} care reprezintă răspunsul HTTP curent
     * @param filterChain lanțul de filtre care trebuie continuat după aplicarea acestui filtru
     * @throws ServletException dacă apare o eroare la procesarea cererii
     * @throws IOException dacă apare o eroare de intrare/ieșire
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7); // Elimină prefixul "Bearer "
            try {
                // Extrage numele utilizatorului și rolurile din tokenul JWT
                String username = jwtUtil.extractUsername(token);
                List<String> roles = jwtUtil.extractRoles(token);

                // Transformă rolurile în obiecte GrantedAuthority
                List<GrantedAuthority> authorities = roles.stream()
                        .map(role -> new SimpleGrantedAuthority("ROLE_" + role)) // Prefixează cu "ROLE_"
                        .collect(Collectors.toList());

                // Creează obiectul de autentificare și populează SecurityContext
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(username, null, authorities);
                SecurityContextHolder.getContext().setAuthentication(authentication);

            } catch (Exception e) {
                // Resetează contextul de securitate dacă tokenul este invalid
                SecurityContextHolder.clearContext();
            }
        }

        // Continuă procesarea lanțului de filtre
        filterChain.doFilter(request, response);
    }
}


