package com.example.proiect_awbd.service;

import com.example.proiect_awbd.dto.FileBackupDTO;
import com.example.proiect_awbd.entity.File;
import com.example.proiect_awbd.entity.FileBackup;
import com.example.proiect_awbd.repository.FileBackupRepository;
import com.example.proiect_awbd.repository.FileRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BackupService {

    private final FileBackupRepository fileBackupRepository;
    private final FileRepository fileRepository;

    public BackupService(FileBackupRepository fileBackupRepository, FileRepository fileRepository) {
        this.fileBackupRepository = fileBackupRepository;
        this.fileRepository = fileRepository;
    }

    public List<FileBackupDTO> getAllBackups() {
        return fileBackupRepository.findAll().stream().map(backup -> {
            FileBackupDTO dto = new FileBackupDTO();
            dto.setId(backup.getId());
            dto.setFileId(backup.getFile().getId().intValue());
            dto.setBackupBucket(backup.getBackupBucket());
            dto.setBackupDate(backup.getBackupDate().toString());
            return dto;
        }).collect(Collectors.toList());
    }

    public List<FileBackupDTO> getBackupsForUser(String username) {
        return fileBackupRepository.findAllByFileUserUsername(username).stream().map(backup -> {
            FileBackupDTO dto = new FileBackupDTO();
            dto.setId(backup.getId());
            dto.setFileId(backup.getFile().getId().intValue());
            dto.setBackupBucket(backup.getBackupBucket());
            dto.setBackupDate(backup.getBackupDate().toString());
            return dto;
        }).collect(Collectors.toList());
    }

    public FileBackupDTO createBackup(Long fileId, String requesterUsername, boolean isAdmin) {
        File file = fileRepository.findById(fileId)
                .orElseThrow(() -> new IllegalArgumentException("File not found with ID: " + fileId));

        if (!isAdmin && !file.getUser().getUsername().equals(requesterUsername)) {
            throw new SecurityException("You can only create backups for your own files.");
        }

        FileBackup backup = new FileBackup();
        backup.setFile(file);
        backup.setBackupBucket("default-backup-bucket");
        backup.setBackupDate(LocalDateTime.now());

        FileBackup savedBackup = fileBackupRepository.save(backup);

        FileBackupDTO dto = new FileBackupDTO();
        dto.setId(savedBackup.getId());
        dto.setFileId(savedBackup.getFile().getId().intValue());
        dto.setBackupBucket(savedBackup.getBackupBucket());
        dto.setBackupDate(savedBackup.getBackupDate().toString());

        return dto;
    }
}
