package com.example.proiect_awbd.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Group gestioneaza grupurile de utilizatori in cadrul sistemului de partajare a fisierelor.
 *
 * <p>Aceasta clasa defineste structura entitatii "Group", care include informatii
 * despre numele grupului, liderul acestuia si membrii asociati.</p>
 */
@Entity
@Table(name = "`groups`")
public class Group {

    /**
     * Identificator unic pentru grup.
     *
     * <p>Acest identificator este generat automat si utilizat pentru identificarea unica
     * a grupurilor in baza de date.</p>
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    /**
     * Numele grupului.
     *
     * <p>Exemplu: "Dezvoltatori", "Marketing".</p>
     */
    @Column(name = "name", nullable = false)
    private String name;

    /**
     * Liderul grupului.
     *
     * <p>Relatie de tip @ManyToOne cu entitatea {@link User}, indicand utilizatorul
     * care administreaza acest grup.</p>
     */
    @ManyToOne
    @JoinColumn(name = "leader", referencedColumnName = "username")
    private User leader;

    /**
     * Lista de membri ai grupului.
     *
     * <p>Relatie de tip @ManyToMany cu entitatea {@link User}, reprezentand utilizatorii
     * asociati acestui grup.</p>
     */
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    @JoinTable(
            name = "group_members",
            joinColumns = @JoinColumn(name = "group_id"),
            inverseJoinColumns = @JoinColumn(name = "username")
    )
    private List<User> users = new ArrayList<>();

    /**
     * Constructor implicit pentru clasa Group.
     *
     * <p>Initializare a listei de membri pentru evitarea problemelor legate de null.</p>
     */
    public Group() {
        this.users = new ArrayList<>();
    }

    /**
     * Constructor pentru crearea unui nou grup cu un lider specificat.
     *
     * @param id     identificatorul unic al grupului
     * @param name   numele grupului
     * @param leader liderul grupului
     */
    public Group(int id, String name, User leader) {
        this();
        this.id = id;
        this.name = name;
        this.leader = leader;
    }

    /**
     * @return identificatorul unic al grupului
     */
    public int getId() {
        return id;
    }

    /**
     * Seteaza identificatorul unic al grupului.
     *
     * @param id identificatorul de setat
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * @return numele grupului
     */
    public String getName() {
        return name;
    }

    /**
     * Seteaza numele grupului.
     *
     * @param name numele de setat
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return liderul grupului
     */
    public User getLeader() {
        return leader;
    }

    /**
     * Seteaza liderul grupului.
     *
     * @param leader liderul de setat
     */
    public void setLeader(User leader) {
        this.leader = leader;
    }

    /**
     * @return lista de membri ai grupului
     */
    public List<User> getUsers() {
        return users;
    }

    /**
     * Seteaza lista de membri ai grupului.
     *
     * @param users lista de membri de setat
     */
    public void setUsers(List<User> users) {
        this.users = users;
    }
}
