package com.example.proiect_awbd.service;

import com.example.proiect_awbd.dto.LoginRequestDTO;
import com.example.proiect_awbd.dto.RegisterRequestDTO;
import com.example.proiect_awbd.dto.UserDTO;
import com.example.proiect_awbd.entity.Authority;
import com.example.proiect_awbd.entity.User;
import com.example.proiect_awbd.repository.UserRepository;
import com.example.proiect_awbd.util.CryptoUtil;
import com.example.proiect_awbd.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * UserService gestionează operațiunile legate de utilizatori, cum ar fi înregistrarea,
 * autentificarea și alte acțiuni conexe.
 */
@Service
public class UserService {

    /**
     * Repository-ul utilizat pentru interacțiunea cu baza de date pentru entitatea {@link User}.
     */
    private final UserRepository userRepository;

    /**
     * Encoder-ul utilizat pentru codificarea parolelor utilizatorilor.
     */
    private final PasswordEncoder passwordEncoder;

    /**
     * Constructor care injectează repository-ul de utilizatori și encoder-ul de parole.
     *
     * @param userRepository obiectul {@link UserRepository} utilizat pentru accesarea datelor din baza de date
     * @param passwordEncoder obiectul {@link PasswordEncoder} utilizat pentru codificarea parolelor
     */
    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Înregistrează un utilizator nou în sistem.
     *
     * <p>Această metodă verifică dacă utilizatorul există deja, creează un nou obiect {@link User},
     * codifică parola și setează atributele implicite, cum ar fi spațiul total și spațiul utilizat.
     * De asemenea, atribuie utilizatorului rolul implicit "USER" și salvează utilizatorul în baza de date.</p>
     *
     * @param registerRequestDTO obiectul {@link RegisterRequestDTO} care conține datele utilizatorului (username, parolă, email)
     * @throws IllegalArgumentException dacă numele de utilizator există deja în sistem
     */
    public void registerUser(RegisterRequestDTO registerRequestDTO) throws NoSuchAlgorithmException {
        if (userRepository.existsById(registerRequestDTO.getUsername())) {
            throw new IllegalArgumentException("Username already exists.");
        }

        User user = new User();
        user.setUsername(registerRequestDTO.getUsername());
        user.setPassword(passwordEncoder.encode(registerRequestDTO.getPassword()));
        user.setEmail(registerRequestDTO.getEmail());
        user.setEnabled(true);
        user.setTotalSpace(100.0f); // Spațiul total disponibil implicit
        user.setUsedSpace(0.0f);    // Spațiul utilizat inițial

        user.setUserBucketId(UUID.randomUUID().toString());
        user.setUserSecret(CryptoUtil.generateUserSecretKey()); // Generare UUID pentru secretele utilizatorului

        // Setarea rolului implicit ca "USER"
        Authority authority = new Authority();
        authority.setAuthority("USER");
        authority.setUser(user);

        user.getAuthorities().add(authority);

        userRepository.save(user);
    }

    /**
     * Autentifică un utilizator pe baza credențialelor furnizate.
     *
     * <p>Metoda validează numele de utilizator și parola, apoi generează un token JWT
     * care conține numele utilizatorului și rolurile acestuia.</p>
     *
     * @param loginRequestDTO obiectul {@link LoginRequestDTO} care conține username-ul și parola utilizatorului
     * @return un token JWT care poate fi utilizat pentru autentificare
     * @throws AccessDeniedException dacă autentificarea eșuează din cauza credențialelor invalide
     * @throws BadCredentialsException dacă parola nu se potrivește sau utilizatorul nu există
     */
    public String authenticateUser(LoginRequestDTO loginRequestDTO) throws AccessDeniedException {
        User user = userRepository.findById(loginRequestDTO.getUsername())
                .orElseThrow(() -> new BadCredentialsException("Invalid credentials"));

        if (!passwordEncoder.matches(loginRequestDTO.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("Invalid credentials");
        }

        // Obține lista de roluri ale utilizatorului
        List<String> roles = user.getAuthorities()
                .stream()
                .map(Authority::getAuthority)
                .collect(Collectors.toList());

        // Generează și returnează token-ul JWT
        return JwtUtil.generateToken(user.getUsername(), roles);
    }

    public void deleteUser(String username, String requesterUsername, boolean isAdmin) {
        if (!isAdmin && !username.equals(requesterUsername)) {
            throw new SecurityException("You are not allowed to delete another user's account.");
        }

        if (!userRepository.existsById(username)) {
            throw new IllegalArgumentException("User not found.");
        }

        userRepository.deleteById(username);
    }

    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(user -> {
                    UserDTO userDTO = new UserDTO();
                    userDTO.setUsername(user.getUsername());
                    userDTO.setEmail(user.getEmail());
                    userDTO.setTotalSpace(user.getTotalSpace());
                    userDTO.setUsedSpace(user.getUsedSpace());
                    return userDTO;
                })
                .collect(Collectors.toList());
    }

    public Map<String, Float> getUserStorageDetails(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

        // Return a map with total and used space
        return Map.of(
                "totalSpace", user.getTotalSpace(),
                "usedSpace", user.getUsedSpace()
        );
    }
}
