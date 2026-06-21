package com.example.proiect_awbd.service;

import com.example.proiect_awbd.dto.LoginRequestDTO;
import com.example.proiect_awbd.dto.RegisterRequestDTO;
import com.example.proiect_awbd.dto.UserDTO;
import com.example.proiect_awbd.entity.Authority;
import com.example.proiect_awbd.entity.User;
import com.example.proiect_awbd.repository.UserRepository;
import com.example.proiect_awbd.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.nio.file.AccessDeniedException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Teste unitare pentru {@link UserService}, folosind JUnit 5 + Mockito.
 * Repository-ul, encoder-ul de parole si JwtUtil sunt mock-uite -
 * nu se atinge baza de date.
 */
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private UserService userService;

    private RegisterRequestDTO registerRequest;

    @BeforeEach
    void setUp() {
        registerRequest = new RegisterRequestDTO("john", "pass123", "john@test.com");
    }

    @Test
    @DisplayName("registerUser salveaza un utilizator nou cu parola criptata si rolul USER")
    void registerUser_savesNewUserWithEncodedPasswordAndUserRole() throws NoSuchAlgorithmException {
        when(userRepository.existsById("john")).thenReturn(false);
        when(passwordEncoder.encode("pass123")).thenReturn("ENCODED");

        userService.registerUser(registerRequest);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        User saved = captor.getValue();

        assertEquals("john", saved.getUsername());
        assertEquals("ENCODED", saved.getPassword());
        assertEquals("john@test.com", saved.getEmail());
        assertTrue(saved.isEnabled());
        assertTrue(saved.getAuthorities().stream()
                .map(Authority::getAuthority)
                .anyMatch("USER"::equals));
    }

    @Test
    @DisplayName("registerUser arunca exceptie daca username-ul exista deja")
    void registerUser_throwsWhenUsernameAlreadyExists() {
        when(userRepository.existsById("john")).thenReturn(true);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> userService.registerUser(registerRequest));
        assertEquals("Username already exists.", ex.getMessage());
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("authenticateUser returneaza un token JWT pentru credentiale valide")
    void authenticateUser_returnsTokenForValidCredentials() throws AccessDeniedException {
        User user = buildUser("john", "ENCODED");
        when(userRepository.findById("john")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("pass123", "ENCODED")).thenReturn(true);
        when(jwtUtil.generateToken(eq("john"), anyList())).thenReturn("jwt-token");

        String token = userService.authenticateUser(new LoginRequestDTO("john", "pass123"));

        assertEquals("jwt-token", token);
    }

    @Test
    @DisplayName("authenticateUser arunca BadCredentials cand utilizatorul nu exista")
    void authenticateUser_throwsWhenUserNotFound() {
        when(userRepository.findById("ghost")).thenReturn(Optional.empty());

        assertThrows(BadCredentialsException.class,
                () -> userService.authenticateUser(new LoginRequestDTO("ghost", "x")));
    }

    @Test
    @DisplayName("authenticateUser arunca BadCredentials cand parola este gresita")
    void authenticateUser_throwsWhenPasswordWrong() {
        User user = buildUser("john", "ENCODED");
        when(userRepository.findById("john")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong", "ENCODED")).thenReturn(false);

        assertThrows(BadCredentialsException.class,
                () -> userService.authenticateUser(new LoginRequestDTO("john", "wrong")));
        verifyNoInteractions(jwtUtil);
    }

    @Test
    @DisplayName("deleteUser permite adminului sa stearga orice utilizator existent")
    void deleteUser_allowsAdminToDeleteExistingUser() {
        when(userRepository.existsById("john")).thenReturn(true);

        userService.deleteUser("john", null, true);

        verify(userRepository).deleteById("john");
    }

    @Test
    @DisplayName("deleteUser interzice unui user sa stearga contul altcuiva")
    void deleteUser_forbidsDeletingAnotherUsersAccount() {
        assertThrows(SecurityException.class,
                () -> userService.deleteUser("john", "mary", false));
        verify(userRepository, never()).deleteById(anyString());
    }

    @Test
    @DisplayName("deleteUser arunca exceptie cand utilizatorul nu exista")
    void deleteUser_throwsWhenUserDoesNotExist() {
        when(userRepository.existsById("john")).thenReturn(false);

        assertThrows(IllegalArgumentException.class,
                () -> userService.deleteUser("john", "john", false));
        verify(userRepository, never()).deleteById(anyString());
    }

    @Test
    @DisplayName("getAllUsers mapeaza entitatile in DTO-uri")
    void getAllUsers_mapsEntitiesToDtos() {
        User u = buildUser("john", "ENCODED");
        u.setTotalSpace(100f);
        u.setUsedSpace(20f);
        when(userRepository.findAll()).thenReturn(List.of(u));

        List<UserDTO> result = userService.getAllUsers();

        assertEquals(1, result.size());
        assertEquals("john", result.get(0).getUsername());
        assertEquals("john@test.com", result.get(0).getEmail());
        assertEquals(100f, result.get(0).getTotalSpace());
        assertEquals(20f, result.get(0).getUsedSpace());
    }

    @Test
    @DisplayName("getUserStorageDetails returneaza spatiul total si folosit")
    void getUserStorageDetails_returnsSpaceInfo() {
        User u = buildUser("john", "ENCODED");
        u.setTotalSpace(100f);
        u.setUsedSpace(40f);
        when(userRepository.findByUsername("john")).thenReturn(Optional.of(u));

        Map<String, Float> details = userService.getUserStorageDetails("john");

        assertEquals(100f, details.get("totalSpace"));
        assertEquals(40f, details.get("usedSpace"));
    }

    @Test
    @DisplayName("getUserStorageDetails arunca exceptie cand utilizatorul nu exista")
    void getUserStorageDetails_throwsWhenUserNotFound() {
        when(userRepository.findByUsername("ghost")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> userService.getUserStorageDetails("ghost"));
    }

    private User buildUser(String username, String password) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setEmail("john@test.com");
        user.setEnabled(true);
        Authority authority = new Authority();
        authority.setAuthority("USER");
        authority.setUser(user);
        user.getAuthorities().add(authority);
        return user;
    }
}
