package com.example.proiect_awbd.service;

import com.example.proiect_awbd.entity.User;
import com.example.proiect_awbd.util.CryptoUtil;
import io.minio.*;
import io.minio.errors.MinioException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.UUID;


@Service
public class ObjectStorageService {

    private final MinioClient minioClient;

    /**
     * Constructor care initializează clientul MinIO utilizat pentru interacțiunea cu serviciul de stocare.
     *
     * @param minioClient instanța de MinioClient configurată
     */
    @Autowired
    public ObjectStorageService(MinioClient minioClient) {
        this.minioClient = minioClient;
    }

    /**
     * Încărca un fișier în bucket-ul unui utilizator specific.
     * Fișierul este criptat înainte de a fi încărcat.
     *
     * @param user utilizatorul pentru care fișierul trebuie încărcat
     * @param file metadatele fișierului
     * @param fileStream fluxul de date al fișierului de încărcat
     * @throws RuntimeException dacă apar erori la criptare sau încărcare
     */
    public Long uploadFileUsers(User user, com.example.proiect_awbd.entity.File file, InputStream fileStream) {
        Long fileSize = 0L;
        try {
            File encryptedFile = File.createTempFile(UUID.randomUUID().toString(), ".enc");
            CryptoUtil.encryptFile(fileStream, new FileOutputStream(encryptedFile), user);

            fileSize = encryptedFile.length();
            if (fileSize == 0) {
                throw new RuntimeException("Fișierul criptat este gol.");
            }

            try (InputStream inputStreamEnc = new FileInputStream(encryptedFile)) {
                boolean found = minioClient.bucketExists(BucketExistsArgs.builder().bucket(user.getUserBucketId()).build());
                if (!found) {
                    minioClient.makeBucket(MakeBucketArgs.builder().bucket(user.getUserBucketId()).build());
                }

                minioClient.putObject(
                        PutObjectArgs.builder()
                                .bucket(user.getUserBucketId())
                                .object(file.getFileKey())
                                .stream(inputStreamEnc, fileSize, -1)
                                .contentType(file.getFileType())
                                .build()
                );
            } finally {
                encryptedFile.delete();
            }

        } catch (Exception e) {
            throw new RuntimeException("Eroare la încărcarea fișierului în MinIO: " + e.getMessage());
        }

        return fileSize;
    }

    /**
     * Descarcă un fișier din bucket-ul utilizatorului specificat.
     * Fișierul este decriptat înainte de a fi scris în fluxul de ieșire.
     *
     * @param user utilizatorul căruia îi aparține fișierul
     * @param file metadatele fișierului
     * @param outputFile fluxul de ieșire unde datele fișierului sunt scrise
     * @throws RuntimeException dacă apar erori la descărcare sau decriptare
     */
    public void downloadFileUsers(User user, com.example.proiect_awbd.entity.File file, FileOutputStream outputFile) {
        try {
            InputStream inputStream = minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(user.getUserBucketId())
                            .object(file.getFileKey())
                            .build()
            );
            File decryptedFile = File.createTempFile(UUID.randomUUID().toString(), ".dec");

            CryptoUtil.decryptFile(inputStream, new FileOutputStream(decryptedFile), user);
            Files.copy(decryptedFile.toPath(), outputFile);

            decryptedFile.delete();
            inputStream.close();
        } catch (Exception e) {
            throw new RuntimeException("Eroare la descărcarea sau decriptarea fișierului din MinIO: " + e.getMessage());
        }
    }

    /**
     * Șterge un fișier din bucket-ul utilizatorului specificat.
     *
     * @param user utilizatorul căruia îi aparține fișierul
     * @param file metadatele fișierului (cheia etc.)
     * @throws RuntimeException dacă apar erori la ștergerea fișierului
     */
    public void deleteFileUsers(User user, com.example.proiect_awbd.entity.File file) {
        try {
            boolean bucketExists = minioClient.bucketExists(
                    BucketExistsArgs.builder()
                            .bucket(user.getUserBucketId())
                            .build()
            );

            if (!bucketExists) {
                throw new RuntimeException("Bucket-ul nu există pentru utilizator: " + user.getUsername());
            }

            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(user.getUserBucketId())
                            .object(file.getFileKey())
                            .build()
            );

            System.out.println("Fișier șters cu succes: " + file.getFileKey());
        } catch (MinioException e) {
            throw new RuntimeException("Eroare la ștergerea fișierului (eroare MinIO): " + e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException("Eroare la ștergerea fișierului: " + e.getMessage());
        }
    }

    /**
     * Functie pentru a actualiza un fisier existent in MinIO.
     * Fisierul actual va fi sters, iar noul fisier va fi incarcat cu aceeasi cheie.
     *
     * @param user utilizatorul al carui fisier trebuie actualizat
     * @param file metadatele fisierului care trebuie actualizat
     * @param fileStream continutul noului fisier
     * @throws RuntimeException daca apare o eroare in procesul de actualizare
     */
    public void updateFileUsers(User user, com.example.proiect_awbd.entity.File file, InputStream fileStream) {
        try {
            boolean bucketExists = minioClient.bucketExists(
                    BucketExistsArgs.builder()
                            .bucket(user.getUserBucketId())
                            .build()
            );

            if (!bucketExists) {
                throw new RuntimeException("Bucket-ul utilizatorului nu există: " + user.getUsername());
            }

            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(user.getUserBucketId())
                            .object(file.getFileKey())
                            .build()
            );

            System.out.println("Fișier șters cu succes înainte de modificare: " + file.getFileKey());

            uploadFileUsers(user, file, fileStream);

            System.out.println("Fișier modificat cu succes: " + file.getFileKey());
        } catch (Exception e) {
            throw new RuntimeException("Eroare la modificarea (upload) fișierului: " + e.getMessage());
        }
    }
}
