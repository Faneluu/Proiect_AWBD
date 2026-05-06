package com.example.proiect_awbd.dto;

/**
 * RegisterRequestDTO este un obiect de transfer de date (DTO) utilizat pentru a captura
 * informațiile necesare pentru înregistrarea unui utilizator nou.
 *
 * <p>Clasa conține trei câmpuri principale: numele de utilizator, parola și adresa de email.</p>
 */
public class RegisterRequestDTO {

    /**
     * Numele de utilizator al utilizatorului care dorește să se înregistreze.
     */
    private String username;

    /**
     * Parola utilizatorului care dorește să se înregistreze.
     */
    private String password;

    /**
     * Adresa de email a utilizatorului.
     */
    private String email;

    /**
     * Constructor implicit. Este necesar pentru serializarea și deserializarea obiectelor.
     */
    public RegisterRequestDTO() {
    }

    /**
     * Constructor care inițializează câmpurile cu valori furnizate.
     *
     * @param username numele de utilizator
     * @param password parola utilizatorului
     * @param email adresa de email a utilizatorului
     */
    public RegisterRequestDTO(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
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
     * Obține adresa de email a utilizatorului.
     *
     * @return adresa de email
     */
    public String getEmail() {
        return email;
    }

    /**
     * Setează adresa de email a utilizatorului.
     *
     * @param email adresa de email
     */
    public void setEmail(String email) {
        this.email = email;
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
        return "RegisterRequestDTO{" +
                "username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", password='[PROTECTED]'" +
                '}';
    }
}

