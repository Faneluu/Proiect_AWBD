package com.example.proiect_awbd.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * CustomAccessDeniedHandler este o implementare personalizată a interfeței {@link AccessDeniedHandler},
 * care gestionează excepțiile de tip AccessDeniedException prin setarea unui răspuns HTTP 403 Forbidden.
 */
@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    /**
     * Gestionează excepțiile de tip AccessDeniedException prin personalizarea răspunsului HTTP.
     *
     * <p>Această metodă setează codul de status HTTP la 403 (Forbidden), specifică tipul de conținut
     * ca text simplu și scrie mesajul excepției în corpul răspunsului.</p>
     *
     * @param request obiectul HttpServletRequest care a declanșat excepția {@link AccessDeniedException}
     * @param response obiectul HttpServletResponse utilizat pentru a trimite răspunsul HTTP 403
     * @param accessDeniedException excepția care a declanșat acest handler
     * @throws IOException dacă apare o eroare de intrare/ieșire în timpul scrierii răspunsului
     */
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException)
            throws IOException {
        // Setează răspunsul HTTP la 403 (Forbidden)
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        // Specifică tipul de conținut ca text simplu
        response.setContentType("text/plain");

        // Scrie mesajul excepției direct în corpul răspunsului
        response.getWriter().write(accessDeniedException.getMessage());
    }
}
