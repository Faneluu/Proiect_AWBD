package com.example.proiect_awbd.dto;

/**
 * UserDTO este un obiect de transfer de date (DTO) utilizat pentru a transfera
 * informații despre utilizator între diferite părți ale aplicației.
 *
 * <p>Clasa conține informații precum numele de utilizator, adresa de email,
 * spațiul total disponibil și spațiul utilizat.</p>
 */
public class UserDTO {

    /**
     * Numele de utilizator al utilizatorului.
     */
    private String username;

    /**
     * Adresa de email a utilizatorului.
     */
    private String email;

    /**
     * Spațiul total disponibil pentru utilizator (în unități de stocare, cum ar fi MB sau GB).
     */
    private Float totalSpace;

    /**
     * Spațiul utilizat de utilizator (în unități de stocare, cum ar fi MB sau GB).
     */
    private Float usedSpace;

    /**
     * Constructor implicit. Este necesar pentru serializarea și deserializarea obiectelor.
     */
    public UserDTO() {
    }

    /**
     * Constructor care inițializează toate câmpurile cu valori furnizate.
     *
     * @param username numele de utilizator
     * @param email adresa de email a utilizatorului
     * @param totalSpace spațiul total disponibil pentru utilizator
     * @param usedSpace spațiul deja utilizat de utilizator
     */
    public UserDTO(String username, String email, Float totalSpace, Float usedSpace) {
        this.username = username;
        this.email = email;
        this.totalSpace = totalSpace;
        this.usedSpace = usedSpace;
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
     * Obține spațiul total disponibil pentru utilizator.
     *
     * @return spațiul total disponibil
     */
    public Float getTotalSpace() {
        return totalSpace;
    }

    /**
     * Setează spațiul total disponibil pentru utilizator.
     *
     * @param totalSpace spațiul total disponibil
     */
    public void setTotalSpace(Float totalSpace) {
        this.totalSpace = totalSpace;
    }

    /**
     * Obține spațiul deja utilizat de utilizator.
     *
     * @return spațiul utilizat
     */
    public Float getUsedSpace() {
        return usedSpace;
    }

    /**
     * Setează spațiul utilizat de utilizator.
     *
     * @param usedSpace spațiul utilizat
     */
    public void setUsedSpace(Float usedSpace) {
        this.usedSpace = usedSpace;
    }

    /**
     * Returnează o reprezentare sub formă de text a obiectului.
     *
     * @return o reprezentare sub formă de string a obiectului
     */
    @Override
    public String toString() {
        return "UserDTO{" +
                "username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", totalSpace=" + totalSpace +
                ", usedSpace=" + usedSpace +
                '}';
    }
}

