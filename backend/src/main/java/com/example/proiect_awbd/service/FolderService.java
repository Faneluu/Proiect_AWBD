package com.example.proiect_awbd.service;

import com.example.proiect_awbd.dto.FileMetadataDTO;
import com.example.proiect_awbd.dto.FolderDTO;
import com.example.proiect_awbd.entity.File;
import com.example.proiect_awbd.entity.Folder;
import com.example.proiect_awbd.entity.User;
import com.example.proiect_awbd.repository.FileRepository;
import com.example.proiect_awbd.repository.FolderRepository;
import com.example.proiect_awbd.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FolderService {

    private final FolderRepository folderRepository;
    private final UserRepository userRepository;
    private final FileRepository fileRepository;
    private final ObjectStorageService objectStorageService;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

    @Autowired
    public FolderService(FolderRepository folderRepository, UserRepository userRepository,
                         FileRepository fileRepository, ObjectStorageService objectStorageService) {
        this.folderRepository = folderRepository;
        this.userRepository = userRepository;
        this.fileRepository = fileRepository;
        this.objectStorageService = objectStorageService;
    }

    public List<FolderDTO> getRootFoldersByUsername(String username) {
        List<Folder> folders = folderRepository.findByUserUsernameAndParentIdIsNull(username);

        return folders.stream()
                .map(folder -> {
                    FolderDTO folderDTO = convertToDTO(folder);
                    folderDTO.setPathFolder(buildFolderPath(folder));
                    return folderDTO;
                })
                .collect(Collectors.toList());
    }

    public List<FolderDTO> getAllFolders(String username) {
        List<Folder> folders = folderRepository.findByUserUsername(username);

        return folders.stream()
                .map(folder -> {
                    FolderDTO folderDTO = convertToDTO(folder);
                    folderDTO.setPathFolder(buildFolderPath(folder));
                    return folderDTO;
                })
                .collect(Collectors.toList());
    }

    public FolderDTO createFolder(FolderDTO folderDTO) {
        User user = userRepository.findByUsername(folderDTO.getOwnerUsername())
                .orElseThrow(() -> new RuntimeException("User not found: " + folderDTO.getOwnerUsername()));

        Folder parentFolder = getFolderParentName(folderDTO.getParentPath(), user);

        boolean folderExists = folderRepository.existsByNameAndUserAndParentId(
                folderDTO.getName(), user, parentFolder);

        if (folderExists) {
            throw new RuntimeException("A folder with the name '" + folderDTO.getName() +
                    "' already exists under the specified parent.");
        }

        Folder folder = new Folder();
        folder.setName(folderDTO.getName());
        folder.setUser(user);
        folder.setParentId(parentFolder);

        Folder savedFolder = folderRepository.save(folder);

        return convertToDTO(savedFolder);
    }


    private void deleteChildFolders(Folder parentFolder) {
        List<Folder> childFolders = folderRepository.findByParentId(parentFolder);
        for (Folder child : childFolders) {
            deleteChildFolders(child);
            folderRepository.delete(child);
        }
    }

    public FolderDTO updateFolder(String username, FolderDTO folderDTO) {
        Folder folder = folderRepository.findByIdAndUsername(folderDTO.getId(), username);

        if (!folder.getUser().getUsername().equals(username)) {
            throw new RuntimeException("Access denied to update this folder");
        }

        if (folderDTO.getName() != null) {
            folder.setName(folderDTO.getName());
        }

        Folder newParentFolder = folderDTO.getParentPath() != null
                ? getFolderParentName(folderDTO.getParentPath(), folder.getUser())
                : null;

        if (folder.equals(newParentFolder)) {
            throw new RuntimeException("A folder cannot be its own parent.");
        }

        folder.setParentId(newParentFolder);

        Folder updatedFolder = folderRepository.save(folder);
        return convertToDTO(updatedFolder);
    }

    public List<FolderDTO> getFoldersInFolder(Long folderId, String username) {
        Folder parentFolder = folderRepository.findById(folderId)
                .orElseThrow(() -> new RuntimeException("Parent folder not found for ID: " + folderId));

        if (!parentFolder.getUser().getUsername().equals(username)) {
            throw new RuntimeException("Access denied to folder: " + parentFolder.getName());
        }

        return folderRepository.findByParentId(parentFolder)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<FileMetadataDTO> getFilesInFolder(FolderDTO folderDTO, String username) {
        Folder folder = getFolderName(folderDTO.getParentPath(), username);

        if (!folder.getUser().getUsername().equals(username)) {
            throw new RuntimeException("Access denied to folder: " + folderDTO.getParentPath());
        }

        return folder.getFiles().stream()
                .map(this::convertToFileDTO)
                .collect(Collectors.toList());
    }

    public Long getFolderId(FolderDTO folderDTO, String username) {
        Folder folder = getFolderName(folderDTO.getParentPath(), username);
        return folder.getId();
    }


    private FolderDTO convertToDTO(Folder folder) {
        String parentPath = buildFolderPath(folder);

        if (folder.getParentId() != null)
            return new FolderDTO(
                    folder.getId(),
                    folder.getName(),
                    folder.getUser().getUsername(),
                    parentPath,
                    folder.getParentId().getId()
            );
        else
            return new FolderDTO(
                    folder.getId(),
                    folder.getName(),
                    folder.getUser().getUsername(),
                    parentPath
            );
    }

    private FileMetadataDTO convertToFileDTO(File file) {
        return new FileMetadataDTO(
                file.getId(),
                file.getName(),
                file.getFileType(),
                file.getFileSize(),
                file.getCreatedAt() != null ? LocalDateTime.parse(String.format(String.valueOf(FORMATTER))) : null,
                file.getUpdatedAt() != null ? LocalDateTime.parse(String.format(String.valueOf(FORMATTER))) : null,
                file.getUser().getUsername()
        );
    }

    private String buildFolderPath(Folder folder) {
        StringBuilder pathBuilder = new StringBuilder();
        folder = folder.getParentId();

        while (folder != null) {
            pathBuilder.insert(0, "/" + folder.getName());
            folder = folder.getParentId();
        }
        return pathBuilder.toString();
    }

    private Folder getFolderName(String path, String username) {

        String[] pathParts = path.split("/");
        Folder parentFolder = null;
        Folder currentFolder = null;
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

        for (String part : pathParts) {
            if (part.isEmpty()) continue;
            currentFolder = folderRepository.findByNameAndUserAndParentId(part, user, parentFolder)
                    .orElseThrow(() -> new RuntimeException("Folder not found for path segment: " + part));
            parentFolder = currentFolder;
        }
        return currentFolder;
    }

    private Folder getFolderParentName(String parentPath, User user) {
        if (parentPath == null || parentPath.isEmpty()) {
            return null;
        }

        String[] pathParts = parentPath.split("/");
        Folder parentFolder = null;

        for (String part : pathParts) {
            if (part.isEmpty()) continue;
            parentFolder = folderRepository.findByNameAndUserAndParentId(part, user, parentFolder)
                    .orElseThrow(() -> new RuntimeException("Parent folder not found for path: " + parentPath));
        }
        return parentFolder;
    }

    /**
     * Create a child folder under a specified parent folder.
     */
    public FolderDTO createChildFolder(Long parentId, FolderDTO childFolderDTO, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

        Folder parentFolder = folderRepository.findById(parentId)
                .orElseThrow(() -> new RuntimeException("Parent folder not found for ID: " + parentId));

        if (!parentFolder.getUser().getUsername().equals(username)) {
            throw new RuntimeException("Access denied to parent folder: " + parentFolder.getName());
        }

        boolean folderExists = folderRepository.existsByNameAndUserAndParentId(
                childFolderDTO.getName(), user, parentFolder);

        if (folderExists) {
            throw new RuntimeException("A folder with the name '" + childFolderDTO.getName() +
                    "' already exists under the specified parent.");
        }

        Folder childFolder = new Folder();
        childFolder.setName(childFolderDTO.getName());
        childFolder.setParentId(parentFolder);
        childFolder.setUser(user);

        Folder savedChildFolder = folderRepository.save(childFolder);

        return convertToDTO(savedChildFolder);
    }

    /**
     * Delete a child folder under a specified parent folder.
     */
    public void deleteChildFolder(Long parentId, Long childId, String username) {
        Folder parentFolder = folderRepository.findById(parentId)
                .orElseThrow(() -> new RuntimeException("Parent folder not found for ID: " + parentId));

        Folder childFolder = folderRepository.findById(childId)
                .orElseThrow(() -> new RuntimeException("Child folder not found for ID: " + childId));

        if (!parentFolder.getUser().getUsername().equals(username) ||
                !childFolder.getUser().getUsername().equals(username)) {
            throw new RuntimeException("Access denied to folder.");
        }

        if (!childFolder.getParentId().equals(parentFolder)) {
            throw new RuntimeException("The specified child folder is not under the given parent folder.");
        }

        deleteChildFolders(childFolder);
        folderRepository.delete(childFolder);
    }

    public void moveFolder(Long folderId, Long targetParentId, String username) {
        // Fetch the folder to be moved
        Folder folderToMove = folderRepository.findById(folderId)
                .orElseThrow(() -> new RuntimeException("Folder not found for ID: " + folderId));

        // Fetch the target parent folder
        Folder targetParentFolder = folderRepository.findById(targetParentId)
                .orElseThrow(() -> new RuntimeException("Target parent folder not found for ID: " + targetParentId));

        // Check if the user has access to both folders
        if (!folderToMove.getUser().getUsername().equals(username) ||
                !targetParentFolder.getUser().getUsername().equals(username)) {
            throw new RuntimeException("Access denied to folder.");
        }

        // Check if the folder is being moved to itself or its own subfolder
        if (folderToMove.equals(targetParentFolder) || isSubfolder(folderToMove, targetParentFolder)) {
            throw new RuntimeException("Invalid move operation: A folder cannot be its own parent or subfolder.");
        }

        // Move the folder
        folderToMove.setParentId(targetParentFolder);
        folderRepository.save(folderToMove);
    }

    // Helper method to check if a folder is a subfolder of another
    private boolean isSubfolder(Folder folder, Folder potentialParent) {
        Folder current = potentialParent;
        while (current != null) {
            if (current.equals(folder)) {
                return true;
            }
            current = current.getParentId();
        }
        return false;
    }

    public void deleteFolderAndFiles(Long folderId, String username) {
        // Fetch the folder to be deleted
        Folder folder = folderRepository.findById(folderId)
                .orElseThrow(() -> new RuntimeException("Folder not found for ID: " + folderId));

        // Check if the user is authorized to delete the folder
        if (!folder.getUser().getUsername().equals(username)) {
            throw new RuntimeException("Unauthorized to delete this folder");
        }

        // Recursively delete child folders
        List<Folder> childFolders = folderRepository.findByParentId(folder); // Adjusted to fetch child folders
        for (Folder childFolder : childFolders) {
            deleteFolderAndFiles(childFolder.getId(), username);
        }

        // Delete files in the current folder
        List<File> files = folder.getFiles();
        for (File file : files) {
            objectStorageService.deleteFileUsers(folder.getUser(), file); // Delete from storage
            file.getFolders().remove(folder); // Detach folder from file
            fileRepository.save(file); // Update the file entity
        }
        folder.getFiles().clear(); // Clear files from folder to avoid issues
        folderRepository.save(folder); // Save updated folder state

        // Delete the current folder
        folderRepository.delete(folder);
    }


    private List<Folder> getChildFolders(Long parentId, String username) {
        return folderRepository.findByUsernameAndParentId(username, parentId);
    }
}
