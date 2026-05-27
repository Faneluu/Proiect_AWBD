package com.example.proiect_awbd.controller;

import com.example.proiect_awbd.dto.FileSharingLinkDTO;
import com.example.proiect_awbd.service.FileService;
import com.example.proiect_awbd.service.FileSharingLinkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/links")
public class FileSharingLinkController {

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
        System.out.println("Cerere facuta de " + username + " pentru descarcare prin link");

        try {
            fileId = fileSharingLinkService.getFileIdBySharingLinkId(linkId);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }

        return fileService.downloadFileById(fileId, username);
    }

    @PostMapping
    public ResponseEntity<String> createFileSharingLink(@RequestBody FileSharingLinkDTO fileSharingLink) {
        String linkId = null;
        try {
            linkId = fileSharingLinkService.createFileSharingLink(
                    fileSharingLink.getFileId(), fileSharingLink.getAccessType(),
                    fileSharingLink.getPermissions(), fileSharingLink.getExpiresAt());

        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(linkId);
    }
}


