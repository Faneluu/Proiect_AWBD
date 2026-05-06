package com.example.proiect_awbd.entity;

import jakarta.persistence.*;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Reprezinta un utilizator al sistemului de partajare a fisierelor.
 *
 * <p>Clasa implementeaza interfata {@link UserDetails}, fiind utilizata pentru gestionarea
 * autentificarii si autorizarii in cadrul sistemului. Fiecare utilizator poate avea
 * fisiere, foldere, grupuri si autoritati asociate.</p>
 */
@Entity
@Table(name = "users")
public class User implements UserDetails {

    /**
     * Numele unic de utilizator.
     */
    @Id
    @Column(name = "username", length = 50, nullable = false)
    private String username;

    /**
     * Parola utilizatorului, stocata in format hash.
     */
    @Column(name = "password", length = 70, nullable = false)
    private String password;

    /**
     * Starea contului utilizatorului: activ sau inactiv.
     */
    @Column(name = "enabled", nullable = false)
    private boolean enabled;

    /**
     * Adresa de email a utilizatorului.
     */
    @Column(name = "email", nullable = false)
    private String email;

    /**
     * Fisierele asociate acestui utilizator.
     */
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<File> files = new ArrayList<>();

    /**
     * Folderele asociate acestui utilizator.
     */
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<Folder> folders = new ArrayList<>();

    /**
     * Grupurile conduse de acest utilizator.
     */
    @OneToMany(mappedBy = "leader", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Group> ledGroups = new ArrayList<>();

    /**
     * Autoritatile asociate utilizatorului.
     */
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private Set<Authority> authorities = new HashSet<>();

    /**
     * Secretul utilizatorului pentru autentificari suplimentare (ex.: autentificare multi-factor).
     */
    @Column(name = "user_secret", nullable = false)
    private String userSecret;

    /**
     * Identificatorul unic al bucket-ului de stocare asociat utilizatorului.
     */
    @Column(name = "user_bucket_id", nullable = false)
    private String userBucketId;


    /**
     * Spatiul total disponibil pentru utilizator (in GB, MB, etc.).
     */
    @Column(name = "total_space", nullable = false)
    private float totalSpace;

    /**
     * Spatiul utilizat de utilizator (in GB, MB, etc.).
     */
    @Column(name = "used_space", nullable = false)
    private float usedSpace;

    // ---- Constructori ----

    /**
     * Constructor implicit pentru clasa User.
     */
    public User() {
        this.files = new ArrayList<>();
        this.folders = new ArrayList<>();
        this.ledGroups = new ArrayList<>();
    }

    /**
     * Constructor pentru crearea unui utilizator cu parametri specifici.
     *
     * @param username     Numele unic al utilizatorului
     * @param password     Parola utilizatorului
     * @param enabled      Starea contului
     * @param email        Adresa de email
     * @param userSecret   Secretul utilizatorului
     * @param totalSpace   Spatiul total disponibil
     * @param usedSpace    Spatiul utilizat
     */
    public User(String username, String password, boolean enabled, String email, String userSecret,
                float totalSpace, float usedSpace) {
        this();
        this.username = username;
        this.password = password;
        this.enabled = enabled;
        this.email = email;
        this.userSecret = userSecret;
        this.totalSpace = totalSpace;
        this.usedSpace = usedSpace;
    }

    // ---- Metode pentru gestionarea relatiilor ----

    /**
     * Adauga un fisier asociat utilizatorului.
     *
     * @param file Fisierul de adaugat
     */
    public void addFile(File file) {
        this.files.add(file);
        file.setUser(this);
    }

    /**
     * Elimina un fisier asociat utilizatorului.
     *
     * @param file Fisierul de eliminat
     */
    public void removeFile(File file) {
        this.files.remove(file);
        file.setUser(null);
    }

    /**
     * Adauga un folder asociat utilizatorului.
     *
     * @param folder Folderul de adaugat
     */
    public void addFolder(Folder folder) {
        this.folders.add(folder);
        folder.setUser(this);
    }

    /**
     * Elimina un folder asociat utilizatorului.
     *
     * @param folder Folderul de eliminat
     */
    public void removeFolder(Folder folder) {
        this.folders.remove(folder);
        folder.setUser(null);
    }

    /**
     * Adauga un grup condus de utilizator.
     *
     * @param group Grupul de adaugat
     */
    public void addLedGroup(Group group) {
        this.ledGroups.add(group);
        group.setLeader(this);
    }

    /**
     * Elimina un grup condus de utilizator.
     *
     * @param group Grupul de eliminat
     */
    public void removeLedGroup(Group group) {
        this.ledGroups.remove(group);
        group.setLeader(null);
    }

    // ---- Getteri si Setteri ----

    /**
     * @return Numele unic al utilizatorului
     */
    @Override
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public Set<Authority> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(Set<Authority> authorities) {
        this.authorities = authorities;
    }

    public String getUserSecret() {
        return userSecret;
    }

    public void setUserSecret(String userSecret) {
        this.userSecret = userSecret;
    }

    public float getTotalSpace() {
        return totalSpace;
    }

    public void setTotalSpace(float totalSpace) {
        this.totalSpace = totalSpace;
    }

    public float getUsedSpace() {
        return usedSpace;
    }

    public void setUsedSpace(float usedSpace) {
        this.usedSpace = usedSpace;
    }

    public List<File> getFiles() {
        return files;
    }

    public void setFiles(List<File> files) {
        this.files = files;
    }

    public List<Folder> getFolders() {
        return folders;
    }

    public void setFolders(List<Folder> folders) {
        this.folders = folders;
    }

    public List<Group> getLedGroups() {
        return ledGroups;
    }

    public void setLedGroups(List<Group> ledGroups) {
        this.ledGroups = ledGroups;
    }


    public String getUserBucketId() {
        return userBucketId;
    }

    public void setUserBucketId(String userBucketId) {
        this.userBucketId = userBucketId;
    }


    // ---- Metode implementate din UserDetails ----

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
}
