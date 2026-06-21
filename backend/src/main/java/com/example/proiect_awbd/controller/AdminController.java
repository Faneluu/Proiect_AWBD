package com.example.proiect_awbd.controller;

import com.example.proiect_awbd.dto.*;
import com.example.proiect_awbd.service.AdminService;
import com.example.proiect_awbd.service.BackupService;
import com.example.proiect_awbd.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;

/**
 * AdminController gestionează operațiunile specifice administratorilor aplicației.
 *
 * <p>Această clasă definește endpoint-uri pentru autentificarea administratorilor și accesarea
 * statisticilor, cu restricții bazate pe rolul de "ADMIN".</p>
 */
@RestController
@RequestMapping("/api/v1/admin")
public class AdminController {

    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);

    /**
     * Serviciul utilizat pentru gestionarea utilizatorilor, inclusiv autentificarea acestora.
     */
    private final UserService userService;
    private final AdminService adminService;
    private final BackupService backupService;

    /**
     * Rolul necesar pentru a accesa operațiunile definite în acest controller.
     */
    private final String role = "ADMIN";

    /**
     * Constructor care injectează serviciul de utilizator.
     *
     * @param userService obiectul {@link UserService} utilizat pentru gestionarea utilizatorilor
     */
    @Autowired
    public AdminController(UserService userService, AdminService adminService, BackupService backupService) {
        this.userService = userService;
        this.adminService = adminService;
        this.backupService = backupService;
    }

    /**
     * Endpoint pentru autentificarea unui administrator.
     *
     * <p>Acest endpoint primește credențialele unui utilizator și returnează un token JWT
     * dacă autentificarea este reușită. Tokenul este inclus în antetul răspunsului ca "Authorization".</p>
     *
     * @param loginRequestDTO obiectul {@link LoginRequestDTO} care conține datele de autentificare (username și password)
     * @return un {@link ResponseEntity} care conține tokenul JWT în antetul "Authorization"
     * @throws AccessDeniedException dacă autentificarea eșuează
     */
    @PostMapping("/login")
    public ResponseEntity<String> loginAdmin(@RequestBody LoginRequestDTO loginRequestDTO) throws AccessDeniedException {
        String token = userService.authenticateUser(loginRequestDTO);
        return ResponseEntity.ok()
                .header("Authorization", "Bearer " + token)
                .build();
    }

    /**
     * Endpoint pentru accesarea statisticilor de către administrator.
     *
     * <p>Acest endpoint este protejat, fiind accesibil doar utilizatorilor cu rolul de "ADMIN".
     * Returnează un mesaj care confirmă accesul acordat.</p>
     *
     * @param authHeader antetul "Authorization" care conține tokenul JWT pentru autentificare
     * @return un {@link ResponseEntity} cu mesajul "Access granted to statistics"
     * @throws AccessDeniedException dacă utilizatorul nu are permisiunile necesare
     */
    @GetMapping("/statistics")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, List<StatisticsDTO<?>>>> getStatistics(@RequestHeader("Authorization") String authHeader) throws AccessDeniedException {
        Map<String, List<StatisticsDTO<?>>> statistics = adminService.getStatistics();
        return ResponseEntity.ok(statistics);
    }

    @PostMapping("/allocate-space")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> allocateUserSpace(
            @RequestParam("username") String username,
            @RequestParam("space") Float space) {
        logger.info("Alocare spatiu {} pentru utilizatorul '{}'", space, username);
        try {
            adminService.allocateSpace(username, space);
            return ResponseEntity.ok("Space allocated successfully for user: " + username);
        } catch (Exception e) {
            logger.error("Eroare la alocarea spatiului pentru '{}': {}", username, e.getMessage(), e);
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        try {
            List<UserDTO> users = userService.getAllUsers();
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PostMapping("/")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> createUser(@RequestBody RegisterRequestDTO registerRequestDTO) {
        logger.info("Admin creeaza utilizatorul '{}'", registerRequestDTO.getUsername());
        try {
            userService.registerUser(registerRequestDTO);
        } catch (NoSuchAlgorithmException e) {
            logger.error("Eroare la crearea utilizatorului '{}' de catre admin: {}", registerRequestDTO.getUsername(), e.getMessage(), e);
            return ResponseEntity.internalServerError().body(e.getMessage());
        }

        return ResponseEntity.ok("User registered successfully");
    }

    @DeleteMapping("/users/{username}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteUser(@PathVariable String username) {
        logger.warn("Admin sterge utilizatorul '{}'", username);
        try {
            userService.deleteUser(username, null, true);
            return ResponseEntity.ok("User deleted successfully: " + username);
        } catch (Exception e) {
            logger.error("Eroare la stergerea utilizatorului '{}': {}", username, e.getMessage(), e);
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/backups")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<FileBackupDTO>> getAllBackups() {
        try {
            List<FileBackupDTO> backups = backupService.getAllBackups();
            return ResponseEntity.ok(backups);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    @PostMapping("/backups")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<FileBackupDTO> createBackup(@RequestParam("username") String username, @RequestParam Long fileId) {
        try {
            FileBackupDTO backup = backupService.createBackup(fileId, username, true);
            return ResponseEntity.ok(backup);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }
}
