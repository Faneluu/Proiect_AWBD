package com.example.proiect_awbd.dto;

import com.example.proiect_awbd.enums.AccessType;
import com.example.proiect_awbd.enums.Permissions;

import java.time.LocalDateTime;

public class FileSharingLinkDTO {
    private Long fileId;
    private String filename;
    private Permissions permissions;
    private AccessType accessType;
    private LocalDateTime expiresAt;

    public FileSharingLinkDTO() {
    }

    public FileSharingLinkDTO(Long fileId, String filename, Permissions permissions, AccessType accessType, LocalDateTime expiresAt) {
        this.fileId = fileId;
        this.filename = filename;
        this.permissions = permissions;
        this.accessType = accessType;
        this.expiresAt = expiresAt;
    }

    public Long getFileId() {
        return fileId;
    }

    public void setFileId(Long fileId) {
        this.fileId = fileId;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public Permissions getPermissions() {
        return permissions;
    }

    public void setPermissions(Permissions permissions) {
        this.permissions = permissions;
    }

    public AccessType getAccessType() {
        return accessType;
    }

    public void setAccessType(AccessType accessType) {
        this.accessType = accessType;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }
}
