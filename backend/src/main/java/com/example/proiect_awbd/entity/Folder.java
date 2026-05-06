package com.example.proiect_awbd.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Folder gestioneaza directoarele utilizatorilor in sistemul de partajare a fisierelor.
 *
 * <p>Aceasta clasa defineste structura entitatii "Folder", care include informatii
 * despre utilizatorul proprietar, fisierele continute si eventualul folder parinte.</p>
 */
@Entity
@Table(name = "folders", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"name", "username", "parent_id"})
})
public class Folder {

    /**
     * Identificator unic pentru folder.
     *
     * <p>Acest identificator este generat automat si utilizat pentru identificarea
     * unica a folderelor in baza de date.</p>
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /**
     * Numele folderului.
     *
     * <p>Exemple: "Documente", "Imagini".</p>
     */
    @Column(name = "name", nullable = false)
    private String name;

    /**
     * Utilizatorul care detine acest folder.
     *
     * <p>Relatie de tip @ManyToOne cu entitatea {@link User}, indicand proprietarul folderului.</p>
     */
    @ManyToOne
    @JoinColumn(name = "username", referencedColumnName = "username", nullable = false)
    @JsonIgnore
    private User user;

    /**
     * Folderul parinte asociat acestui folder (daca exista).
     *
     * <p>Relatie de tip @ManyToOne pentru structurarea ierarhica a folderelor.</p>
     */
    @ManyToOne
    @JoinColumn(name = "parent_id", referencedColumnName = "id")
    private Folder parentId;

    /**
     * Lista de fisiere asociate acestui folder.
     *
     * <p>Relatie de tip @ManyToMany cu entitatea {@link File}, reprezentand fisierele
     * continute de acest folder.</p>
     */
    @ManyToMany(mappedBy = "folders", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JsonIgnore
    private List<File> files = new ArrayList<>();

    /**
     * Constructor pentru crearea unui folder cu datele de baza.
     *
     * @param id   identificatorul unic al folderului
     * @param name numele folderului
     * @param user utilizatorul asociat
     */
    public Folder(Long id, String name, User user) {
        this.id = id;
        this.name = name;
        this.user = user;
    }

    /**
     * Constructor pentru crearea unui folder cu folder parinte.
     *
     * @param id     identificatorul unic al folderului
     * @param name   numele folderului
     * @param user   utilizatorul asociat
     * @param folder folderul parinte
     */
    public Folder(Long id, String name, User user, Folder folder) {
        this.id = id;
        this.name = name;
        this.user = user;
        this.parentId = folder;
    }

    /**
     * Constructor implicit pentru clasa Folder.
     */
    public Folder() {}

    /**
     * @return identificatorul unic al folderului
     */
    public Long getId() {
        return id;
    }

    /**
     * Seteaza identificatorul unic al folderului.
     *
     * @param id identificatorul de setat
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * @return numele folderului
     */
    public String getName() {
        return name;
    }

    /**
     * Seteaza numele folderului.
     *
     * @param name numele de setat
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return utilizatorul care detine acest folder
     */
    public User getUser() {
        return user;
    }

    /**
     * Seteaza utilizatorul care detine acest folder.
     *
     * @param user utilizatorul de setat
     */
    public void setUser(User user) {
        this.user = user;
    }

    /**
     * @return lista de fisiere asociate acestui folder
     */
    public List<File> getFiles() {
        return files;
    }

    /**
     * Seteaza lista de fisiere asociate acestui folder.
     *
     * @param files lista de fisiere de setat
     */
    public void setFiles(List<File> files) {
        this.files = files;
    }

    /**
     * @return folderul parinte asociat acestui folder, sau null daca nu exista
     */
    public Folder getParentId() {
        return parentId;
    }

    /**
     * Seteaza folderul parinte asociat acestui folder.
     *
     * @param parentId folderul parinte de setat
     */
    public void setParentId(Folder parentId) {
        this.parentId = parentId;
    }

}
