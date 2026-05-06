package com.example.proiect_awbd.dto;

public class FolderDTO {
    private Long id;
    private String name;
    private String ownerUsername;
    private String parentPath;
    private Long parentId;

    public FolderDTO() {

    }

    public FolderDTO(Long id, String name, String username, String parentPath) {
        this.id = id;
        this.name = name;
        this.ownerUsername = username;
        this.parentPath = parentPath;
    }

    public FolderDTO(Long id, String name, String ownerUsername, String parentPath, Long parentId) {
        this.id = id;
        this.name = name;
        this.ownerUsername = ownerUsername;
        this.parentPath = parentPath;
        this.parentId = parentId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOwnerUsername() {
        return ownerUsername;
    }

    public void setOwnerUsername(String ownerUsername) {
        this.ownerUsername = ownerUsername;
    }

    public String getParentPath() { return parentPath; }

    public void setPathFolder(String s) { this.parentPath = s; }

    public Long getParentId() { return parentId; }

    public void setCurrentPath(Long parentId) { this.parentId = parentId; }
}
