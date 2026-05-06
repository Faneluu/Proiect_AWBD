package com.example.proiect_awbd.entity;

import com.example.proiect_awbd.enums.AccessType;
import com.example.proiect_awbd.enums.Permissions;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * FileSharingLink gestioneaza link-urile de partajare a fisierelor in sistem.
 *
 * <p>Aceasta clasa defineste structura entitatii "FileSharingLink", care include informatii
 * despre fisierul partajat, permisiunile asociate, tipul de acces si data expirarii.</p>
 */
@Entity
@Table(name = "file_sharing_links")
public class FileSharingLink {

    /**
     * Identificator unic pentru link-ul de partajare.
     *
     * <p>Generat automat pentru fiecare instanta a link-ului, utilizat pentru identificare unica.</p>
     */
    @Id
    private String linkId;

    /**
     * Fisierul asociat acestui link de partajare.
     *
     * <p>Relatie de tip @ManyToOne cu entitatea {@link File}, reprezentand fisierul
     * partajat prin acest link.</p>
     */
    @ManyToOne
    @JoinColumn(name = "file_id", referencedColumnName = "id", nullable = false)
    private File file;

    /**
     * Tipul de acces oferit prin link (ex.: public, privat).
     *
     * <p>Acest camp specifica natura accesului oferit de link-ul de partajare.</p>
     */
    @Column(name = "acces_type", nullable = false)
    private AccessType accessType;

    /**
     * Data si ora la care expira link-ul de partajare.
     *
     * <p>Dupa expirarea acestui timp, link-ul devine inactiv.</p>
     */
    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    /**
     * Permisiunile asociate link-ului (ex.: doar citire, citire si scriere).
     *
     * <p>Defineste ce actiuni pot fi efectuate asupra fisierului partajat prin acest link.</p>
     */
    @Column(name = "permissions", nullable = false)
    private Permissions permissions;

    /**
     * Utilizatorul care a creat link-ul de partajare.
     *
     * <p>Relatie de tip @ManyToOne cu entitatea {@link User}, indicand utilizatorul
     * care a generat acest link.</p>
     */
    @ManyToOne
    @JoinColumn(name = "username", referencedColumnName = "username", nullable = false)
    private User user;

    /**
     * Constructor pentru crearea unei noi instante de FileSharingLink.
     *
     * <p>Initializarea completa a link-ului cu toate atributele necesare.</p>
     *
     * @param linkId      identificatorul unic al link-ului
     * @param file        fisierul partajat prin link
     * @param accessType  tipul de acces oferit
     * @param expiresAt   data si ora expirarii link-ului
     * @param permissions permisiunile asociate
     * @param user        utilizatorul care a creat link-ul
     */
    public FileSharingLink(String linkId, File file, AccessType accessType,
                           LocalDateTime expiresAt, Permissions permissions, User user) {
        this.linkId = linkId;
        this.file = file;
        this.accessType = accessType;
        this.expiresAt = expiresAt;
        this.permissions = permissions;
        this.user = user;
    }

    /**
     * Constructor implicit pentru clasa FileSharingLink.
     */
    public FileSharingLink() {}

    /**
     * @return identificatorul unic al link-ului
     */
    public String getLinkId() {
        return linkId;
    }

    /**
     * Seteaza identificatorul unic al link-ului.
     *
     * @param linkId identificatorul de setat
     */
    public void setLinkId(String linkId) {
        this.linkId = linkId;
    }

    /**
     * @return fisierul asociat acestui link
     */
    public File getFile() {
        return file;
    }

    /**
     * Seteaza fisierul asociat acestui link.
     *
     * @param file fisierul de setat
     */
    public void setFile(File file) {
        this.file = file;
    }

    /**
     * @return tipul de acces oferit de link
     */
    public AccessType getAccessType() {
        return accessType;
    }

    /**
     * Seteaza tipul de acces oferit de link.
     *
     * @param accessType tipul de acces de setat
     */
    public void setAccessType(AccessType accessType) {
        this.accessType = accessType;
    }

    /**
     * @return data si ora expirarii link-ului
     */
    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    /**
     * Seteaza data si ora expirarii link-ului.
     *
     * @param expiresAt data si ora de setat
     */
    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    /**
     * @return permisiunile asociate link-ului
     */
    public Permissions getPermissions() {
        return permissions;
    }

    /**
     * Seteaza permisiunile asociate link-ului.
     *
     * @param permissions permisiunile de setat
     */
    public void setPermissions(Permissions permissions) {
        this.permissions = permissions;
    }

    /**
     * @return utilizatorul care a creat link-ul
     */
    public User getUser() {
        return user;
    }

    /**
     * Seteaza utilizatorul care a creat link-ul.
     *
     * @param user utilizatorul de setat
     */
    public void setUser(User user) {
        this.user = user;
    }
}
