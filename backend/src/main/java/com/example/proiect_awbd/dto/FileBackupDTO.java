package com.example.proiect_awbd.dto;

public class FileBackupDTO {
    private int id;
    private int fileId;
    private String backupBucket;
    private String backupDate;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getFileId() {
        return fileId;
    }

    public void setFileId(int fileId) {
        this.fileId = fileId;
    }

    public String getBackupBucket() {
        return backupBucket;
    }

    public void setBackupBucket(String backupBucket) {
        this.backupBucket = backupBucket;
    }

    public String getBackupDate() {
        return backupDate;
    }

    public void setBackupDate(String backupDate) {
        this.backupDate = backupDate;
    }
}

