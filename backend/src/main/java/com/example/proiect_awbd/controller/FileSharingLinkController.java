package com.example.proiect_awbd.controller;

import com.example.proiect_awbd.dto.FileSharingLinkDTO;
import com.example.proiect_awbd.service.FileService;
import com.example.proiect_awbd.service.FileSharingLinkService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/links")
public class FileSharingLinkController {

    private static final Logger logger = LoggerFactory.getLogger(FileSharingLinkController.class);

    FileSharingLinkService fileSharingLinkService;
    FileService fileService;

    @Autowired
    FileSharingLinkController(FileSharingLinkService fileSharingLinkService, FileService fileService) {
        this.fileSharingLinkService = fileSharingLinkService;
        this.fileService = fileService;
    }


    @GetMapping("/{linkId}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String linkId) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Long fileId;
        logger.info("Cerere de descarcare prin link '{}' facuta de utilizatorul '{}'", linkId, username);

        try {
            fileId = fileSharingLinkService.getFileIdBySharingLinkId(linkId);
        } catch (Exception e) {
            logger.warn("Link de partajare invalid sau expirat: '{}'", linkId);
            return ResponseEntity.notFound().build();
        }

        return fileService.downloadFileById(fileId, username);
    }

    @PostMapping
    public ResponseEntity<String> createFileSharingLink(@RequestBody FileSharingLinkDTO fileSharingLink) {
        String linkId = null;
        logger.info("Creare link de partajare pentru fisierul {}", fileSharingLink.getFileId());
        try {
            linkId = fileSharingLinkService.createFileSharingLink(
                    fileSharingLink.getFileId(), fileSharingLink.getAccessType(),
                    fileSharingLink.getPermissions(), fileSharingLink.getExpiresAt());

        } catch (Exception e) {
            logger.error("Eroare la crearea link-ului de partajare pentru fisierul {}: {}", fileSharingLink.getFileId(), e.getMessage(), e);
            return ResponseEntity.notFound().build();
        }
        logger.debug("Link de partajare creat: '{}'", linkId);
        return ResponseEntity.ok(linkId);
    }
}


