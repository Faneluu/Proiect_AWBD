package com.example.proiect_awbd.integration;

import com.example.proiect_awbd.dto.LoginRequestDTO;
import com.example.proiect_awbd.dto.RegisterRequestDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
// ObjectMapper este construit local: in Spring Boot 4 bean-ul Jackson nu este
// mereu prezent in contextul de test non-web.
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Teste de integrare end-to-end care parcurg intregul flux
 * controller -> service -> repository -> baza de date H2 (profil "test"),
 * inclusiv lantul de filtre Spring Security si filtrul JWT.
 *
 * <p>MockMvc este construit manual din {@link WebApplicationContext} cu
 * {@code springSecurity()}, ca sa nu depindem de modulele de test web
 * reorganizate in Spring Boot 4.</p>
 */
@SpringBootTest
@ActiveProfiles("test")
class AuthFlowIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();
    }

    @Test
    @DisplayName("Scenariu 1: inregistrare reusita, apoi autentificare care returneaza un token JWT")
    void registerThenLogin_returnsJwtToken() throws Exception {
        mockMvc.perform(post("/api/v1/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new RegisterRequestDTO("alice", "secret123", "alice@test.com"))))
                .andExpect(status().isOk())
                .andExpect(content().string("User registered successfully"));

        MvcResult loginResult = mockMvc.perform(post("/api/v1/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new LoginRequestDTO("alice", "secret123"))))
                .andExpect(status().isOk())
                .andReturn();

        String authHeader = loginResult.getResponse().getHeader(HttpHeaders.AUTHORIZATION);
        assertNotNull(authHeader, "Raspunsul de login ar trebui sa contina antetul Authorization");
        assertTrue(authHeader.startsWith("Bearer "), "Tokenul ar trebui sa fie de tip Bearer");
    }

    @Test
    @DisplayName("Scenariu 2: autentificare cu parola gresita este respinsa")
    void loginWithWrongPassword_isRejected() throws Exception {
        mockMvc.perform(post("/api/v1/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new RegisterRequestDTO("bob", "correct-pass", "bob@test.com"))))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/v1/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new LoginRequestDTO("bob", "wrong-pass"))))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("Scenariu 3: accesarea unui endpoint protejat fara token este refuzata")
    void accessProtectedEndpointWithoutToken_isUnauthorized() throws Exception {
        mockMvc.perform(get("/api/v1/folders/"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("Scenariu 4: flux complet - inregistrare, login si acces protejat cu token valid")
    void fullFlow_registerLoginAndAccessProtectedResource() throws Exception {
        String token = registerAndLogin("carol", "pass12345", "carol@test.com");

        mockMvc.perform(get("/api/v1/folders/")
                        .header(HttpHeaders.AUTHORIZATION, token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    @DisplayName("Scenariu 5: flux complet - creare grup si regasirea lui in lista utilizatorului")
    void fullFlow_createGroupAndListIt() throws Exception {
        String token = registerAndLogin("dave", "pass12345", "dave@test.com");

        mockMvc.perform(post("/api/v1/groups/team-rocket")
                        .header(HttpHeaders.AUTHORIZATION, token))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/v1/groups/")
                        .header(HttpHeaders.AUTHORIZATION, token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("team-rocket"))
                .andExpect(jsonPath("$[0].leader").value("dave"));
    }

    /**
     * Helper: inregistreaza un utilizator, il autentifica si returneaza
     * valoarea antetului Authorization ("Bearer <token>").
     */
    private String registerAndLogin(String username, String password, String email) throws Exception {
        mockMvc.perform(post("/api/v1/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new RegisterRequestDTO(username, password, email))))
                .andExpect(status().isOk());

        MvcResult loginResult = mockMvc.perform(post("/api/v1/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new LoginRequestDTO(username, password))))
                .andExpect(status().isOk())
                .andReturn();

        return loginResult.getResponse().getHeader(HttpHeaders.AUTHORIZATION);
    }
}
