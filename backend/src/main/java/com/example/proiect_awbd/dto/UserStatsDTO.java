package com.example.proiect_awbd.dto;

/**
 * DTO care contine statisticile unui utilizator:
 * spatiu total, spatiu folosit, numar fisiere, foldere si grupuri.
 */
public class UserStatsDTO {

    private String username;
    private float totalSpace;
    private float usedSpace;
    private float freeSpace;
    private int fileCount;
    private int folderCount;
    private int groupCount;

    public UserStatsDTO(String username, float totalSpace, float usedSpace,
                        int fileCount, int folderCount, int groupCount) {
        this.username = username;
        this.totalSpace = totalSpace;
        this.usedSpace = usedSpace;
        this.freeSpace = totalSpace - usedSpace;
        this.fileCount = fileCount;
        this.folderCount = folderCount;
        this.groupCount = groupCount;
    }

    public String getUsername() { return username; }
    public float getTotalSpace() { return totalSpace; }
    public float getUsedSpace() { return usedSpace; }
    public float getFreeSpace() { return freeSpace; }
    public int getFileCount() { return fileCount; }
    public int getFolderCount() { return folderCount; }
    public int getGroupCount() { return groupCount; }
}