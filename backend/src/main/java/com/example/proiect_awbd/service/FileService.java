package com.example.proiect_awbd.service;

import com.example.proiect_awbd.dto.FileMetadataDTO;
import com.example.proiect_awbd.entity.File;
import com.example.proiect_awbd.entity.Folder;
import com.example.proiect_awbd.entity.User;
import com.example.proiect_awbd.repository.FileRepository;
import com.example.proiect_awbd.repository.FolderRepository;
import com.example.proiect_awbd.repository.UserRepository;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class FileService {

    private final FileRepository fileRepository;
    private final ObjectStorageService objectStorageService;
    private final UserRepository userRepository;
    private final FolderRepository folderRepository;

    public FileService(FileRepository fileRepository, ObjectStorageService objectStorageService, UserRepository userRepository, FolderRepository folderRepository) {
        this.fileRepository = fileRepository;
        this.objectStorageService = objectStorageService;
        this.userRepository = userRepository;
        this.folderRepository = folderRepository;
    }

    public List<FileMetadataDTO> getRootFilesMetadataByUsername(String username) {
        return fileRepository.findRootFilesByUsername(username)
                .stream()
                .map(file -> new FileMetadataDTO(
                        file.getId(),
                        file.getName(),
                        file.getFileType(),
                        file.getFileSize(),
                        file.getCreatedAt(),
                        file.getUpdatedAt() != null ? file.getUpdatedAt() : null,
                        file.getUser().getUsername()
                ))
                .toList();
    }



    private ResponseEntity<Resource> downloadFile(String username, String filename, Folder folder) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

        System.out.println("User key: " + user.getUserBucketId());

        File file;
        if (folder == null) {
            file = fileRepository.findFileByName(filename)
                    .orElseThrow(() -> new RuntimeException("File not found: " + filename));
        } else {
            if (!folder.getUser().equals(user)) {
                throw new RuntimeException("Access denied: Folder does not belong to the user.");
            }

            file = folder.getFiles().stream()
                    .filter(f -> f.getName().equals(filename))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("File not found in the specified folder: " + filename));
        }

        try {
            java.io.File tempFile = java.io.File.createTempFile("downloaded_", ".tmp");
            FileOutputStream fileOutputStream = new FileOutputStream(tempFile);

            objectStorageService.downloadFileUsers(user, file, fileOutputStream);

            InputStreamResource resource = new InputStreamResource(new FileInputStream(tempFile));

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getName() + "\"")
                    .body(resource);

        } catch (IOException e) {
            throw new RuntimeException("Error occurred during file download: " + e.getMessage(), e);
        }
    }

    public ResponseEntity<Resource> downloadFileByUsername(String filename, String username) {
        return downloadFile(username, filename, null);
    }

    public ResponseEntity<Resource> downloadFileById(Long fileId, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

        File file = fileRepository.findById(fileId)
                .orElseThrow(() -> new RuntimeException("File not found with ID: " + fileId));

        // Optional: Check if the file belongs to the user if necessary
        if (!file.getUser().getUsername().equals(username)) {
            throw new RuntimeException("Access denied: File does not belong to the user.");
        }

        try {
            java.io.File tempFile = java.io.File.createTempFile("downloaded_", ".tmp");
            FileOutputStream fileOutputStream = new FileOutputStream(tempFile);

            objectStorageService.downloadFileUsers(user, file, fileOutputStream);

            InputStreamResource resource = new InputStreamResource(new FileInputStream(tempFile));

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getName() + "\"")
                    .body(resource);

        } catch (IOException e) {
            throw new RuntimeException("Error occurred during file download: " + e.getMessage(), e);
        }
    }


    public ResponseEntity<Resource> downloadFileFromFolder(Long folderId, String filename, String username) {
        Folder folder = folderRepository.findById(folderId)
                .orElseThrow(() -> new RuntimeException("Folder not found: " + folderId));

        return downloadFile(username, filename, folder);
    }


    private void uploadFile(String username, String fileName, String fileType,
                            InputStream fileStream, Folder folder) throws IOException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

        System.out.println("User key: " + user.getUserBucketId());

        boolean fileExists = (folder == null)
                ? fileRepository.existsByName(fileName)
                : folder.getFiles().stream().anyMatch(file -> file.getName().equals(fileName));

        if (fileExists) {
            throw new RuntimeException("A file with the name '" + fileName + "' already exists" +
                    (folder != null ? " in the folder." : "."));
        }

        float fileSizeInGB;
        try {
            fileSizeInGB = fileStream.available() / (1024.0f * 1024.0f * 1024.0f);
        } catch (IOException e) {
            throw new RuntimeException("Error calculating file size: " + e.getMessage(), e);
        }

        if (user.getUsedSpace() + fileSizeInGB > user.getTotalSpace()) {
            throw new RuntimeException("Insufficient space available for the user.");
        }

        File file = new File();
        file.setName(fileName);
        file.setFileType(fileType);
        file.setFileKey(UUID.randomUUID().toString());
        file.setFileSize((double) fileStream.available());
        file.setUser(user);
        file.setCreatedAt(LocalDateTime.now());

        if (folder != null) {
            file.setFolders(List.of(folder));
        }

        fileRepository.save(file);
        objectStorageService.uploadFileUsers(user, file, fileStream);
        if (folder != null) {
            folder.getFiles().add(file);
            folderRepository.save(folder);
        }

        // Update user's used space
        user.setUsedSpace(user.getUsedSpace() + fileSizeInGB);
        userRepository.save(user);
    }


    public void uploadFileUsername(String username, String fileName,
                                   String fileType, InputStream fileStream) throws IOException {
        uploadFile(username, fileName, fileType, fileStream, null);
    }

    private void updateFile(String username, String filename, InputStream fileStream, Folder folder) throws IOException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

        File file;
        if (folder == null) {
            file = fileRepository.findByFileNameAndUser(filename, user)
                    .orElseThrow(() -> new RuntimeException("File not found: " + filename));
        } else {
            if (!folder.getUser().equals(user)) {
                throw new RuntimeException("Access denied: Folder does not belong to the user.");
            }

            file = folder.getFiles().stream()
                    .filter(f -> f.getName().equals(filename))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("File not found in the specified folder: " + filename));
        }

        float newFileSizeInGB;
        try {
            newFileSizeInGB = fileStream.available() / (1024.0f * 1024.0f * 1024.0f);
        } catch (IOException e) {
            throw new RuntimeException("Error calculating file size: " + e.getMessage(), e);
        }

        float oldFileSizeInGB = (float) file.getFileSize() / (1024.0f * 1024.0f * 1024.0f);

        if (user.getUsedSpace() - oldFileSizeInGB + newFileSizeInGB > user.getTotalSpace()) {
            throw new RuntimeException("Insufficient space available for the user.");
        }

        // Update the file and user's used space
        file.setFileSize((double) fileStream.available());
        file.setUpdatedAt(LocalDateTime.now());
        fileRepository.save(file);

        user.setUsedSpace(user.getUsedSpace() - oldFileSizeInGB + newFileSizeInGB);
        userRepository.save(user);

        objectStorageService.updateFileUsers(user, file, fileStream);

        System.out.println("Successfully updated file: " + filename);
    }


    public void updateFileUsername(String username, String filename, InputStream fileStream) throws IOException {
        updateFile(username, filename, fileStream, null);
    }


    private void deleteFile(String username, String filename, Folder folder) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

        File file;
        if (folder == null) {
            file = fileRepository.findByFileNameAndUser(filename, user)
                    .orElseThrow(() -> new RuntimeException("File not found: " + filename + " for user: " + username));
        } else {
            if (!folder.getUser().equals(user)) {
                throw new RuntimeException("Access denied: Folder does not belong to the user.");
            }

            file = folder.getFiles().stream()
                    .filter(f -> f.getName().equals(filename))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("File not found in the specified folder: " + filename));
        }

        objectStorageService.deleteFileUsers(user, file);

        if (folder != null) {
            folder.getFiles().remove(file);
            folderRepository.save(folder);
        }

        fileRepository.delete(file);

        System.out.println("Successfully deleted file: " + filename);
    }

    public void deleteFileUsername(String username, String filename) {
        deleteFile(username, filename, null);
    }

//    public void deleteFileFromFolder(String username, Long folderId, String filename) {
//        Folder folder = folderRepository.findById(folderId)
//                .orElseThrow(() -> new RuntimeException("Folder not found: " + folderId));
//
//        deleteFile(username, filename, folder);
//    }

    public List<FileMetadataDTO> getFilesInFolder(Long folderId, String username) {
        Folder folder = folderRepository.findById(folderId)
                .orElseThrow(() -> new RuntimeException("Folder not found for ID: " + folderId));

        if (!folder.getUser().getUsername().equals(username)) {
            throw new RuntimeException("Access denied to folder: " + folder.getName());
        }

        return folder.getFiles().stream()
                .map(this::convertToFileMetadataDTO)
                .collect(Collectors.toList());
    }

    public void uploadFileToFolder(String username, Long folderId, String filename,
                                   String fileType, InputStream fileContent) throws IOException {
        Folder folder = folderRepository.findById(folderId)
                .orElseThrow(() -> new RuntimeException("Folder not found for ID: " + folderId));

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

        if (folder.getFiles().stream().anyMatch(file -> file.getName().equals(filename))) {
            throw new RuntimeException("File already exists in folder: " + filename);
        }

        // Calculate file size in GB
        float fileSizeInGB = fileContent.available() / (1024.0f * 1024.0f * 1024.0f);

        if (user.getUsedSpace() + fileSizeInGB > user.getTotalSpace()) {
            throw new RuntimeException("Insufficient space available for the user.");
        }

        File file = new File(filename, fileContent.available(), fileType, user);
        file.getFolders().add(folder);
        fileRepository.save(file);
        objectStorageService.uploadFileUsers(user, file, fileContent);

        // Update used space in GB
        user.setUsedSpace(user.getUsedSpace() + fileSizeInGB);
        userRepository.save(user);
    }



    public void updateFileInFolder(String username, Long folderId, String filename, InputStream fileContent) {
        Folder folder = folderRepository.findById(folderId)
                .orElseThrow(() -> new RuntimeException("Folder not found for ID: " + folderId));

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

        File file = folder.getFiles().stream()
                .filter(f -> f.getName().equals(filename))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("File not found in folder: " + filename));

        try {
            float newFileSizeInGB = fileContent.available() / (1024.0f * 1024.0f * 1024.0f);
            float oldFileSizeInGB = (float) file.getFileSize() / (1024.0f * 1024.0f * 1024.0f);

            if (user.getUsedSpace() - oldFileSizeInGB + newFileSizeInGB > user.getTotalSpace()) {
                throw new RuntimeException("Insufficient space available for the user.");
            }

            file.setFileSize((double) fileContent.available());
            file.setUpdatedAt(LocalDateTime.now());
            user.setUsedSpace(user.getUsedSpace() - oldFileSizeInGB + newFileSizeInGB);

            userRepository.save(user);
            fileRepository.save(file);
            objectStorageService.updateFileUsers(user, file, fileContent);
        } catch (IOException e) {
            throw new RuntimeException("Error updating file: " + e.getMessage());
        }
    }


    public void deleteFileFromFolder(String username, Long folderId, String filename) {
        Folder folder = folderRepository.findById(folderId)
                .orElseThrow(() -> new RuntimeException("Folder not found for ID: " + folderId));

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

        File file = folder.getFiles().stream()
                .filter(f -> f.getName().equals(filename))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("File not found in folder: " + filename));

        folder.getFiles().remove(file);
        objectStorageService.deleteFileUsers(user, file);
        fileRepository.delete(file);
        folderRepository.save(folder);

        // Update used space in GB
        float fileSizeInGB = (float) file.getFileSize() / (1024.0f * 1024.0f * 1024.0f);
        user.setUsedSpace(user.getUsedSpace() - fileSizeInGB);
        userRepository.save(user);
    }



    public void moveFileToFolder(String username, Long fileId, Long targetFolderId) {
        // Fetch file and target folder
        File file = fileRepository.findById(fileId)
                .orElseThrow(() -> new RuntimeException("File not found with ID: " + fileId));

        Folder targetFolder = folderRepository.findById(targetFolderId)
                .orElseThrow(() -> new RuntimeException("Target folder not found with ID: " + targetFolderId));

        // Check permissions
        if (!file.getUser().getUsername().equals(username) || !targetFolder.getUser().getUsername().equals(username)) {
            throw new RuntimeException("Access denied to file or folder.");
        }

        // Move file
        file.getFolders().clear(); // Clear current folder associations
        file.getFolders().add(targetFolder);
        fileRepository.save(file);
    }


    private FileMetadataDTO convertToFileMetadataDTO(File file) {
        return new FileMetadataDTO(
                file.getId(),
                file.getName(),
                file.getFileType(),
                file.getFileSize(),
                file.getCreatedAt(),
                file.getUpdatedAt() != null ? file.getUpdatedAt() : null,
                file.getUser().getUsername()
        );
    }

    public List<FileMetadataDTO> searchFilesByUsernameAndQuery(String username, String query) {
        // Use the repository method to search files by username and query
        return fileRepository.searchFiles(username, query);
    }
}
