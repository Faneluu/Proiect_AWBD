package com.example.proiect_awbd.repository;

import com.example.proiect_awbd.entity.Folder;
import com.example.proiect_awbd.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FolderRepository extends JpaRepository<Folder, Long> {
    List<Folder> findByUserUsernameAndParentIdIsNull(String username);

    @Query("SELECT f FROM Folder f WHERE f.id = :id AND f.user.username = :username")
    Folder findByIdAndUsername(@Param("id") long id, @Param("username") String username);


    List<Folder> findByUserUsername(String username);

    List<Folder> findByParentId(Folder parentFolder);

    Optional<Folder> findByNameAndUserAndParentId(String name, User user, Folder parentFolder);

    boolean existsByNameAndUserAndParentId(String name, User user, Folder parentFolder);

    @Query("SELECT f FROM Folder f WHERE f.parentId.id = :parentId AND f.user.username = :username")
    List<Folder> findByUsernameAndParentId(@Param("username") String username, @Param("parentId") Long parentId);
}
