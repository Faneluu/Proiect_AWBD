package com.example.proiect_awbd.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * FileBackup gestioneaza backup-urile fisierelor in cadrul sistemului de partajare.
 *
 * <p>Clasa defineste atributele si relatiile unui backup, inclusiv referinta la fisierul original
 * si informatii despre locatie si momentul crearii backup-ului.</p>
 */
@Entity
@Table(name = "file_backups")
public class FileBackup {

    /**
     * Identificator unic pentru backup-ul fisierului in baza de date.
     *
     * <p>Acest identificator este generat automat si utilizat pentru identificarea unica
     * a unui backup in cadrul sistemului.</p>
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    /**
     * Fisierul asociat acestui backup.
     *
     * <p>Relatie de tip @ManyToOne cu entitatea {@link File}, reprezentand fisierul original
     * pentru care a fost creat backup-ul.</p>
     */
    @ManyToOne
    @JoinColumn(name = "file_id", referencedColumnName = "id", nullable = false)
    private File file;

    /**
     * Numele bucket-ului in care este stocat backup-ul.
     *
     * <p>Acest camp stocheaza informatii despre locatia de stocare, de exemplu un bucket
     * dintr-un serviciu cloud.</p>
     */
    @Column(name = "backup_bucket", nullable = false)
    private String backupBucket;

    /**
     * Data si ora la care a fost realizat backup-ul.
     *
     * <p>Acest camp este utilizat pentru a urmari cand a fost creat backup-ul.</p>
     */
    @Column(name = "backup_date", nullable = false)
    private LocalDateTime backupDate;

    /**
     * Constructor pentru crearea unei noi instante de FileBackup.
     *
     * <p>Permite initializarea unui backup cu toate atributele necesare.</p>
     *
     * @param id           identificatorul unic al backup-ului
     * @param file         fisierul asociat backup-ului
     * @param backupBucket numele bucket-ului pentru stocare
     * @param backupDate   data si ora realizarii backup-ului
     */
    public FileBackup(int id, File file, String backupBucket, LocalDateTime backupDate) {
        this.id = id;
        this.file = file;
        this.backupBucket = backupBucket;
        this.backupDate = backupDate;
    }

    /**
     * Constructor implicit pentru clasa FileBackup.
     */
    public FileBackup() {}

    /**
     * @return identificatorul unic al backup-ului
     */
    public int getId() {
        return id;
    }

    /**
     * Seteaza identificatorul unic al backup-ului.
     *
     * @param id identificatorul de setat
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * @return fisierul asociat acestui backup
     */
    public File getFile() {
        return file;
    }

    /**
     * Seteaza fisierul asociat acestui backup.
     *
     * @param file fisierul de setat
     */
    public void setFile(File file) {
        this.file = file;
    }

    /**
     * @return numele bucket-ului in care este stocat backup-ul
     */
    public String getBackupBucket() {
        return backupBucket;
    }

    /**
     * Seteaza numele bucket-ului pentru stocarea backup-ului.
     *
     * @param backupBucket numele bucket-ului de setat
     */
    public void setBackupBucket(String backupBucket) {
        this.backupBucket = backupBucket;
    }

    /**
     * @return data si ora la care a fost realizat backup-ul
     */
    public LocalDateTime getBackupDate() {
        return backupDate;
    }

    /**
     * Seteaza data si ora realizarii backup-ului.
     *
     * @param backupDate data si ora de setat
     */
    public void setBackupDate(LocalDateTime backupDate) {
        this.backupDate = backupDate;
    }
}
