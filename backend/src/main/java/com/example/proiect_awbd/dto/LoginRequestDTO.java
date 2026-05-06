package com.example.proiect_awbd.dto;

/**
 * LoginRequestDTO este un obiect de transfer de date (DTO) utilizat pentru a captura
 * credențialele de autentificare furnizate de utilizator.
 *
 * <p>Clasa conține două câmpuri principale: numele de utilizator și parola.</p>
 */
public class LoginRequestDTO {

    /**
     * Numele de utilizator al utilizatorului care dorește să se autentifice.
     */
    private String username;

    /**
     * Parola utilizatorului care dorește să se autentifice.
     */
    private String password;

    /**
     * Constructor implicit. Este necesar pentru serializarea și deserializarea obiectelor.
     */
    public LoginRequestDTO() {
    }

    /**
     * Constructor care inițializează câmpurile cu valori furnizate.
     *
     * @param username numele de utilizator
     * @param password parola utilizatorului
     */
    public LoginRequestDTO(String username, String password) {
        this.username = username;
        this.password = password;
    }

    /**
     * Obține numele de utilizator.
     *
     * @return numele de utilizator
     */
    public String getUsername() {
        return username;
    }

    /**
     * Setează numele de utilizator.
     *
     * @param username numele de utilizator
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Obține parola utilizatorului.
     *
     * @return parola utilizatorului
     */
    public String getPassword() {
        return password;
    }

    /**
     * Setează parola utilizatorului.
     *
     * @param password parola utilizatorului
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Returnează o reprezentare sub formă de text a obiectului, fără a expune parola.
     *
     * <p>Parola este protejată pentru a preveni expunerea în jurnalele aplicației.</p>
     *
     * @return o reprezentare sub formă de string a obiectului
     */
    @Override
    public String toString() {
        return "LoginRequestDTO{" +
                "username='" + username + '\'' +
                ", password='[PROTECTED]'" +
                '}';
    }
}


