package com.example.proiect_awbd.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * CustomLogoutSuccessHandler este o implementare personalizată a interfeței {@link LogoutSuccessHandler},
 * care gestionează delogarea cu succes a utilizatorilor prin setarea unui răspuns HTTP 200 OK.
 */
@Component
public class CustomLogoutSuccessHandler implements LogoutSuccessHandler {

    /**
     * Gestionează delogarea cu succes prin personalizarea răspunsului HTTP.
     *
     * <p>Această metodă setează codul de status HTTP la 200 (OK), specifică tipul de conținut
     * ca text simplu și scrie un mesaj de confirmare în corpul răspunsului.</p>
     *
     * @param request obiectul HttpServletRequest care a declanșat delogarea
     * @param response obiectul HttpServletResponse utilizat pentru a trimite răspunsul HTTP 200
     * @param authentication obiectul {@link Authentication} care reprezintă utilizatorul delogat
     * @throws IOException dacă apare o eroare de intrare/ieșire în timpul scrierii răspunsului
     */
    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException {
        // Setează răspunsul HTTP la 200 (OK)
        response.setStatus(HttpServletResponse.SC_OK);
        // Specifică tipul de conținut ca text simplu
        response.setContentType("text/plain");

        // Scrie un mesaj de confirmare a delogării în corpul răspunsului
        response.getWriter().write("Delogare efectuată cu succes.");
    }
}
