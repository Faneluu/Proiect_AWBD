package com.example.proiect_awbd.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * File gestioneaza informatiile asociate unui fisier din sistemul de partajare.
 *
 * <p>Clasa defineste atributele si relatiile unui fisier, inclusiv legaturile cu utilizatorii si folderele.
 * De asemenea, asigura functionalitati pentru gestionarea metadatelor fisierului, cum ar fi dimensiunea,
 * tipul si timpii de creare/actualizare.</p>
 */
@Entity
@Table(name = "files")
public class File {
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Identificator unic al fisierului in baza de date.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Numele fisierului.
     *
     * <p>Exemple: "document.pdf", "imagine.png".</p>
     */
    @Column(name = "name", nullable = false)
    private String name;

    /**
     * Cheie unica pentru identificarea fisierului.
     *
     * <p>Generata automat pentru fiecare fisier, utilizata pentru acces sigur.</p>
     */
    @Column(name = "file_key", nullable = false)
    private String fileKey;

    /**
     * Dimensiunea fisierului, exprimata in bytes.
     */
    @Column(name = "file_size", nullable = false)
    private Double fileSize;

    /**
     * Tipul MIME al fisierului.
     *
     * <p>Exemple: "application/pdf", "image/png".</p>
     */
    @Column(name = "file_type", nullable = false)
    private String fileType;

    /**
     * Data si ora la care a fost creat fisierul.
     */
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    /**
     * Data si ora ultimei actualizari a fisierului.
     */
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * Utilizatorul care detine fisierul.
     *
     * <p>Relatie de tip @ManyToOne cu entitatea {@link User}.</p>
     */
    @ManyToOne
    @JoinColumn(name = "username", referencedColumnName = "username", nullable = false)
    @JsonIgnore
    private User user;

    /**
     * Lista de foldere in care este inclus fisierul.
     *
     * <p>Relatie de tip @ManyToMany cu entitatea {@link Folder}.</p>
     */
    @ManyToMany
    @JoinTable(
            name = "folder_files",
            joinColumns = @JoinColumn(name = "file_id"),
            inverseJoinColumns = @JoinColumn(name = "folder_id")
    )
    private List<Folder> folders = new ArrayList<>();;

    @OneToMany(mappedBy = "file", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<FileSharingLink> fileSharingLinks;

    /**
     * Constructor implicit pentru clasa File.
     */
    public File() {}

    /**
     * Constructor pentru crearea unui fisier cu datele de baza.
     *
     * @param name     Numele fisierului
     * @param fileSize Dimensiunea fisierului
     * @param fileType Tipul MIME al fisierului
     * @param user     Utilizatorul care detine fisierul
     */
    public File(String name, double fileSize, String fileType, User user) {
        this.name = name;
        this.fileSize = fileSize;
        this.fileType = fileType;
        this.createdAt = LocalDateTime.now();
        this.user = user;
        this.fileKey = UUID.randomUUID().toString();
    }

    /**
     * Constructor pentru crearea unui fisier cu foldere asociate.
     *
     * @param id       Identificator unic
     * @param name     Numele fisierului
     * @param fileSize Dimensiunea fisierului
     * @param fileType Tipul MIME al fisierului
     * @param user     Utilizatorul care detine fisierul
     * @param folders  Lista de foldere asociate
     */
    public File(Long id, String name, double fileSize, String fileType, User user, List<Folder> folders) {
        this.id = id;
        this.name = name;
        this.fileSize = fileSize;
        this.fileType = fileType;
        this.createdAt = LocalDateTime.now();
        this.user = user;
        this.folders = folders;
        this.fileKey = UUID.randomUUID().toString();
    }

    /**
     * Seteaza timestamp-ul pentru crearea fisierului inainte de persistare.
     */
    @PrePersist
    public void prePersist() {
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Actualizeaza timestamp-ul atunci cand fisierul este modificat.
     */
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // Getteri si setteri pentru atributele clasei

    /**
     * @return Identificatorul unic al fisierului.
     */
    public Long getId() {
        return id;
    }

    /**
     * @return Numele fisierului.
     */
    public String getName() {
        return name;
    }

    /**
     * Seteaza numele fisierului.
     *
     * @param name Numele de setat
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return Cheia unica a fisierului.
     */
    public String getFileKey() {
        return fileKey;
    }

    /**
     * Seteaza cheia unica a fisierului.
     *
     * @param fileKey Cheia unica de setat
     */
    public void setFileKey(String fileKey) {
        this.fileKey = fileKey;
    }

    /**
     * @return Dimensiunea fisierului in bytes.
     */
    public double getFileSize() {
        return fileSize;
    }

    /**
     * Seteaza dimensiunea fisierului.
     *
     * @param fileSize Dimensiunea de setat
     */
    public void setFileSize(double fileSize) {
        this.fileSize = fileSize;
    }

    /**
     * @return Tipul MIME al fisierului.
     */
    public String getFileType() {
        return fileType;
    }

    /**
     * Seteaza tipul MIME al fisierului.
     *
     * @param fileType Tipul de setat
     */
    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    /**
     * @return Data si ora la care a fost creat fisierul.
     */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    /**
     * Seteaza data si ora la care a fost creat fisierul.
     *
     * @param createdAt Data si ora de setat
     */
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * @return Data si ora ultimei actualizari.
     */
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    /**
     * Seteaza data si ora ultimei actualizari.
     *
     * @param updatedAt Data si ora de setat
     */
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    /**
     * @return Utilizatorul care detine fisierul.
     */
    public User getUser() {
        return user;
    }

    /**
     * Seteaza utilizatorul care detine fisierul.
     *
     * @param user Utilizatorul de setat
     */
    public void setUser(User user) {
        this.user = user;
    }

    /**
     * @return Lista de foldere asociate fisierului.
     */
    public List<Folder> getFolders() {
        return folders;
    }

    /**
     * Seteaza lista de foldere asociate fisierului.
     *
     * @param folders Lista de foldere de setat
     */
    public void setFolders(List<Folder> folders) {
        this.folders = folders;
    }
}
