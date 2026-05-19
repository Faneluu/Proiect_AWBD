package com.example.proiect_awbd.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * CustomAuthenticationSuccessHandler este o implementare personalizată a interfeței {@link AuthenticationSuccessHandler},
 * care gestionează autentificarea reușită a utilizatorilor prin setarea unui răspuns HTTP 200 OK.
 */
@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    /**
     * Gestionează autentificarea reușită prin personalizarea răspunsului HTTP.
     *
     * <p>Această metodă setează codul de status HTTP la 200 (OK), specifică tipul de conținut
     * ca text simplu și scrie numele utilizatorului autentificat în corpul răspunsului.</p>
     *
     * @param request obiectul HttpServletRequest care a declanșat autentificarea
     * @param response obiectul HttpServletResponse utilizat pentru a trimite răspunsul HTTP 200
     * @param authentication obiectul {@link Authentication} care reprezintă utilizatorul autentificat
     * @throws IOException dacă apare o eroare de intrare/ieșire în timpul scrierii răspunsului
     */
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException {
        // Setează răspunsul HTTP la 200 (OK)
        response.setStatus(HttpServletResponse.SC_OK);
        // Specifică tipul de conținut ca text simplu
        response.setContentType("text/plain");

        // Scrie un mesaj de confirmare a autentificării în corpul răspunsului
        response.getWriter().write("Autentificare reușită pentru utilizatorul: " + authentication.getName());
    }
}
