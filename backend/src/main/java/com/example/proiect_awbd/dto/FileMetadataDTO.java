package com.example.proiect_awbd.dto;

import java.time.LocalDateTime;
import java.util.Date;

public class FileMetadataDTO {

    private Long fileId;
    private String name;
    private String fileType;
    private double fileSize;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String username;



    public FileMetadataDTO(Long fileId, String name, String fileType, Double fileSize, LocalDateTime createdAt, LocalDateTime updatedAt, String username) {
        this.fileId = fileId;
        this.name = name;
        this.fileType = fileType;
        this.fileSize = fileSize;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.username = username;
    }



    public long getFileId() {
        return fileId;
    }

    public void setFileId(Long fileId) {
        this.fileId = fileId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public double getFileSize() {
        return fileSize;
    }

    public void setFileSize(double fileSize) {
        this.fileSize = fileSize;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    // Metodă de reprezentare a obiectului sub formă de String
    @Override
    public String toString() {
        return "FileFileMetadataDTO{" +
                "fileId=" + fileId +
                ", name='" + name + '\'' +
                ", fileType='" + fileType + '\'' +
                ", fileSize=" + fileSize +
                ", createdAt='" + createdAt + '\'' +
                ", updatedAt='" + updatedAt + '\'' +
                ", username='" + username + '\'' +
                '}';
    }
}

