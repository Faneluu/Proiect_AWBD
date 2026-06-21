package com.example.proiect_awbd.controller;

import com.example.proiect_awbd.dto.FileBackupDTO;
import com.example.proiect_awbd.dto.LoginRequestDTO;
import com.example.proiect_awbd.dto.RegisterRequestDTO;
import com.example.proiect_awbd.service.BackupService;
import com.example.proiect_awbd.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

/**
 * UserController gestionează operațiunile specifice utilizatorilor aplicației.
 *
 * <p>Această clasă definește endpoint-uri pentru înregistrarea și autentificarea utilizatorilor.</p>
 */
@RestController
@RequestMapping("/api/v1/user")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    /**
     * Serviciul utilizat pentru gestionarea utilizatorilor, inclusiv înregistrarea și autentificarea acestora.
     */
    private final UserService userService;

    private final BackupService backupService;

    /**
     * Rolul utilizatorului care utilizează endpoint-urile din acest controller.
     */
    private final String role = "USER";

    /**
     * Constructor care injectează serviciul de utilizator.
     *
     * @param userService obiectul {@link UserService} utilizat pentru gestionarea utilizatorilor
     */
    @Autowired
    public UserController(UserService userService, BackupService backupService) {

        this.userService = userService;
        this.backupService = backupService;
    }

    /**
     * Endpoint pentru înregistrarea unui utilizator nou.
     *
     * <p>Această metodă primește datele de înregistrare ale unui utilizator și apelează serviciul
     * pentru a adăuga utilizatorul în sistem. Returnează un mesaj de confirmare.</p>
     *
     * @param registerRequestDTO obiectul {@link RegisterRequestDTO} care conține datele utilizatorului (nume, email, parolă etc.)
     * @return un {@link ResponseEntity} care conține mesajul "User registered successfully"
     */
    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody RegisterRequestDTO registerRequestDTO) {
        logger.info("Inregistrare utilizator nou: '{}'", registerRequestDTO.getUsername());
        try {
            userService.registerUser(registerRequestDTO);
        } catch (NoSuchAlgorithmException e) {
            logger.error("Eroare la inregistrarea utilizatorului '{}': {}", registerRequestDTO.getUsername(), e.getMessage(), e);
            return ResponseEntity.internalServerError().body(e.getMessage());
        }

        logger.info("Utilizatorul '{}' a fost inregistrat cu succes", registerRequestDTO.getUsername());
        return ResponseEntity.ok("User registered successfully");
    }

    /**
     * Endpoint pentru autentificarea unui utilizator.
     *
     * <p>Această metodă primește credențialele utilizatorului și returnează un token JWT
     * dacă autentificarea este reușită. Tokenul este inclus în antetul răspunsului ca "Authorization".</p>
     *
     * @param loginRequestDTO obiectul {@link LoginRequestDTO} care conține datele de autentificare (username și password)
     * @return un {@link ResponseEntity} care conține tokenul JWT în antetul "Authorization"
     * @throws AccessDeniedException dacă autentificarea eșuează
     */
    @PostMapping("/login")
    public ResponseEntity<String> loginUser(@RequestBody LoginRequestDTO loginRequestDTO) throws AccessDeniedException {
        logger.info("Tentativa de autentificare pentru utilizatorul '{}'", loginRequestDTO.getUsername());
        String token = userService.authenticateUser(loginRequestDTO);
        logger.info("Autentificare reusita pentru utilizatorul '{}'", loginRequestDTO.getUsername());
        return ResponseEntity.ok()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token) // Use HttpHeaders.AUTHORIZATION for consistency
                .body("Login successful");
    }

    @GetMapping("/backups")
    public ResponseEntity<List<FileBackupDTO>> getAllBackups() {
        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            List<FileBackupDTO> backups = backupService.getBackupsForUser(username);
            return ResponseEntity.ok(backups);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    @PostMapping("/backups")
    public ResponseEntity<FileBackupDTO> createBackup(@RequestParam Long fileId) {
        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            FileBackupDTO backup = backupService.createBackup(fileId, username, false);
            return ResponseEntity.ok(backup);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping("/storage")
    public ResponseEntity<Object> getUserStorageDetails() {
        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            // Fetch storage details using the UserService
            var storageDetails = userService.getUserStorageDetails(username);
            return ResponseEntity.ok(storageDetails);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Unable to retrieve storage details: " + e.getMessage());
        }
    }

}


