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

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil; // adăugat

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) { // adăugat
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil; // adăugat
    }

    public void registerUser(RegisterRequestDTO registerRequestDTO) throws NoSuchAlgorithmException {
        if (userRepository.existsById(registerRequestDTO.getUsername())) {
            throw new IllegalArgumentException("Username already exists.");
        }

        User user = new User();
        user.setUsername(registerRequestDTO.getUsername());
        user.setPassword(passwordEncoder.encode(registerRequestDTO.getPassword()));
        user.setEmail(registerRequestDTO.getEmail());
        user.setEnabled(true);
        user.setTotalSpace(100.0f);
        user.setUsedSpace(0.0f);

        user.setUserBucketId(UUID.randomUUID().toString());
        user.setUserSecret(CryptoUtil.generateUserSecretKey());

        Authority authority = new Authority();
        authority.setAuthority("USER");
        authority.setUser(user);

        user.getAuthorities().add(authority);

        userRepository.save(user);
    }

    public String authenticateUser(LoginRequestDTO loginRequestDTO) throws AccessDeniedException {
        User user = userRepository.findById(loginRequestDTO.getUsername())
                .orElseThrow(() -> new BadCredentialsException("Invalid credentials"));

        if (!passwordEncoder.matches(loginRequestDTO.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("Invalid credentials");
        }

        List<String> roles = user.getAuthorities()
                .stream()
                .map(Authority::getAuthority)
                .collect(Collectors.toList());

        return jwtUtil.generateToken(user.getUsername(), roles); // JwtUtil. → jwtUtil.
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

        return Map.of(
                "totalSpace", user.getTotalSpace(),
                "usedSpace", user.getUsedSpace()
        );
    }
}