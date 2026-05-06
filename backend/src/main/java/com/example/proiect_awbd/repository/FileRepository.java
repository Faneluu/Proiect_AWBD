package com.example.proiect_awbd.repository;

import com.example.proiect_awbd.dto.FileMetadataDTO;
import com.example.proiect_awbd.entity.File;
import com.example.proiect_awbd.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FileRepository extends JpaRepository<File, Long> {
    @Query("SELECT f FROM File f WHERE f.user.username = :username")
    List<File> findAllByUsername(@Param("username") String username);

    @Query("SELECT f FROM File f WHERE f.user.username = :username AND (f.folders IS EMPTY)")
    List<File> findRootFilesByUsername(@Param("username") String username);

    @Query("SELECT f FROM File f WHERE f.name = :filename AND f.user.username = :username")
    Optional<File> findByFilenameAndUsername(@Param("filename") String filename, @Param("username") String username);

    @Query("SELECT f FROM File f WHERE f.name LIKE %:filename% AND f.user = :user")
    Optional<com.example.proiect_awbd.entity.File> findByFileNameAndUser(String filename, User user);

    Optional<com.example.proiect_awbd.entity.File> findByName(String name);

    boolean existsByName(String fileName);

    Optional<File> findFileByName(String filename);

    @Query("SELECT AVG(f.fileSize) FROM File f")
    Double findAverageFileSize();

    @Query("SELECT new com.example.proiect_awbd.dto.FileMetadataDTO(f.id, f.name, f.fileType, f.fileSize, f.createdAt, f.updatedAt, f.user.username) " +
            "FROM File f " +
            "WHERE f.user.username = :username AND LOWER(f.name) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<FileMetadataDTO> searchFiles(@Param("username") String username, @Param("query") String query);

    void removeFileById(Long id);
}
