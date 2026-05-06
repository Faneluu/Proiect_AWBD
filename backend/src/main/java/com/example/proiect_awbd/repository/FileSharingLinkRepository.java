package com.example.proiect_awbd.repository;

import com.example.proiect_awbd.entity.FileSharingLink;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface FileSharingLinkRepository extends JpaRepository<FileSharingLink, Long> {
    void deleteByExpiresAtBefore(LocalDateTime date);
    void deleteByLinkId(String linkId);
    boolean existsByLinkId(String linkId);
    Optional<FileSharingLink> findByLinkId(String linkId);
}
