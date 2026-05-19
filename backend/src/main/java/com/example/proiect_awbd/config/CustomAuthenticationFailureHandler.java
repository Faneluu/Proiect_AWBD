package com.example.proiect_awbd.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * CustomAuthenticationFailureHandler este o implementare personalizată a interfeței {@link AuthenticationFailureHandler},
 * care gestionează încercările eșuate de autentificare prin setarea unui răspuns HTTP 401 Unauthorized.
 */
@Component
public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {

    /**
     * Gestionează eșecul autentificării prin personalizarea răspunsului HTTP.
     *
     * <p>Această metodă setează codul de status HTTP la 401 (Unauthorized), specifică tipul de conținut
     * ca text simplu și scrie mesajul excepției în corpul răspunsului.</p>
     *
     * @param request obiectul HttpServletRequest care a declanșat excepția {@link AuthenticationException}
     * @param response obiectul HttpServletResponse utilizat pentru a trimite răspunsul HTTP 401
     * @param exception excepția care a declanșat acest handler
     * @throws IOException dacă apare o eroare de intrare/ieșire în timpul scrierii răspunsului
     */
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception)
            throws IOException {
        // Setează răspunsul HTTP la 401 (Unauthorized)
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        // Specifică tipul de conținut ca text simplu
        response.setContentType("text/plain");

        // Scrie mesajul excepției direct în corpul răspunsului
        response.getWriter().write("Autentificare eșuată: " + exception.getMessage());
    }
}
