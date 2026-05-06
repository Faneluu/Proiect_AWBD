package com.example.proiect_awbd.entity;

import com.example.proiect_awbd.entity.User;
import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;

/**
 * Authority gestioneaza autoritatile (rolurile) asociate utilizatorilor in sistem.
 *
 * <p>Aceasta clasa defineste structura entitatii "Authority", care este folosita
 * pentru a lega utilizatorii de rolurile lor in cadrul sistemului. Este integrata
 * cu Spring Security prin implementarea interfetei {@link GrantedAuthority}.</p>
 */
@Entity
@Table(name = "authorities", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"username", "authority"})
})
public class Authority implements GrantedAuthority {

    /**
     * Identificator unic pentru entitatea "Authority".
     *
     * <p>Acest camp este generat automat si utilizat pentru identificarea unica
     * a autoritatii in baza de date.</p>
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Numele autoritatii (rolului) atribuite unui utilizator.
     *
     * <p>Exemple de valori pentru acest camp includ "ADMIN" sau "USER".</p>
     */
    @Column(name = "authority", length = 50, nullable = false)
    private String authority;

    /**
     * Referinta catre utilizatorul asociat acestei autoritati.
     *
     * <p>Acest camp leaga entitatea "Authority" de entitatea {@link User}, permitand
     * gestionarea autoritatilor la nivel de utilizator.</p>
     */
    @ManyToOne
    @JoinColumn(name = "username", nullable = true)
    private User user;

    /**
     * Constructor implicit pentru Authority.
     *
     * <p>Acest constructor este necesar pentru crearea obiectelor prin mecanisme ORM.</p>
     */
    public Authority() {
    }

    /**
     * Constructor pentru crearea unei noi instante de Authority.
     *
     * @param id        identificator unic al autoritatii
     * @param authority numele autoritatii (rolului)
     * @param user      utilizatorul asociat acestei autoritati
     */
    public Authority(Long id, String authority, User user) {
        this.id = id;
        this.authority = authority;
        this.user = user;
    }

    /**
     * Obține identificatorul unic al autoritatii.
     *
     * @return identificatorul unic
     */
    public Long getId() {
        return id;
    }

    /**
     * Seteaza identificatorul unic al autoritatii.
     *
     * @param id identificatorul de setat
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Obține numele autoritatii (rolului).
     *
     * <p>Acesta este utilizat de Spring Security pentru gestionarea autorizarii.</p>
     *
     * @return numele autoritatii
     */
    @Override
    public String getAuthority() {
        return authority;
    }

    /**
     * Seteaza numele autoritatii (rolului).
     *
     * @param authority numele autoritatii de setat
     */
    public void setAuthority(String authority) {
        this.authority = authority;
    }

    /**
     * Obține utilizatorul asociat acestei autoritati.
     *
     * @return utilizatorul asociat
     */
    public User getUser() {
        return user;
    }

    /**
     * Seteaza utilizatorul asociat acestei autoritati.
     *
     * @param user utilizatorul de setat
     */
    public void setUser(User user) {
        this.user = user;
    }
}
