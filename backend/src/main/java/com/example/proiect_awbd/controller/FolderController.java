package com.example.proiect_awbd.controller;

import com.example.proiect_awbd.dto.FileMetadataDTO;
import com.example.proiect_awbd.dto.FolderDTO;
import com.example.proiect_awbd.service.FolderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/folders")
public class FolderController {

    private final FolderService folderService;

    public FolderController(FolderService folderService) {
        this.folderService = folderService;
    }

    @GetMapping("/")
    public ResponseEntity<List<FolderDTO>> getRootFoldersByUsername() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        List<FolderDTO> folders = folderService.getRootFoldersByUsername(username);
        return ResponseEntity.ok(folders);
    }

    @GetMapping("/all")
    public ResponseEntity<List<FolderDTO>> getAllFoldersByUsername() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        List<FolderDTO> folders = folderService.getAllFolders(username);
        return ResponseEntity.ok(folders);
    }

    @PostMapping("/")
    public ResponseEntity<FolderDTO> createFolder(@RequestBody FolderDTO folderDTO) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        folderDTO.setOwnerUsername(username);

        FolderDTO createdFolder = folderService.createFolder(folderDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdFolder);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteFolder(@PathVariable Long id) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        folderService.deleteFolderAndFiles(id, username);
        return ResponseEntity.ok("Folder deleted successfully: ID " + id);
    }


    @PutMapping("/")
    public ResponseEntity<FolderDTO> updateFolder(@RequestBody FolderDTO folderDTO) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        FolderDTO updatedFolder = folderService.updateFolder(username, folderDTO);
        return ResponseEntity.ok(updatedFolder);
    }

    @GetMapping("/files")
    public ResponseEntity<List<FileMetadataDTO>> getFilesInFolder(@RequestBody FolderDTO folderDTO) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        List<FileMetadataDTO> files = folderService.getFilesInFolder(folderDTO, username);

        return ResponseEntity.ok(files);
    }

    @GetMapping("/contents")
    public ResponseEntity<List<FolderDTO>> getFoldersInFolder(@RequestParam("id") Long folderId) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        List<FolderDTO> folders = folderService.getFoldersInFolder(folderId, username);
        return ResponseEntity.ok(folders);
    }

    @PutMapping("/{folderId}/move")
    public ResponseEntity<String> moveFolder(
            @PathVariable Long folderId,
            @RequestParam Long targetFolderId) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        folderService.moveFolder(folderId, targetFolderId, username);
        return ResponseEntity.ok("Folder moved successfully");
    }


    @PostMapping("/{parentId}/child")
    public ResponseEntity<FolderDTO> createChildFolder(@PathVariable Long parentId, @RequestBody FolderDTO childFolderDTO) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        childFolderDTO.setOwnerUsername(username);

        FolderDTO createdChildFolder = folderService.createChildFolder(parentId, childFolderDTO, username);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdChildFolder);
    }
}

