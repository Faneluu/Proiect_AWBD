package com.example.proiect_awbd.service;

import com.example.proiect_awbd.dto.FolderDTO;
import com.example.proiect_awbd.entity.Folder;
import com.example.proiect_awbd.entity.User;
import com.example.proiect_awbd.repository.FileRepository;
import com.example.proiect_awbd.repository.FolderRepository;
import com.example.proiect_awbd.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Teste unitare pentru {@link FolderService} (JUnit 5 + Mockito).
 */
@ExtendWith(MockitoExtension.class)
class FolderServiceTest {

    @Mock
    private FolderRepository folderRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private FileRepository fileRepository;

    @Mock
    private ObjectStorageService objectStorageService;

    @InjectMocks
    private FolderService folderService;

    @Test
    @DisplayName("createFolder creeaza un folder root cand numele este liber")
    void createFolder_createsRootFolderWhenNameFree() {
        User user = buildUser("john");
        FolderDTO dto = new FolderDTO();
        dto.setName("docs");
        dto.setOwnerUsername("john");

        when(userRepository.findByUsername("john")).thenReturn(Optional.of(user));
        when(folderRepository.existsByNameAndUserAndParentId("docs", user, null)).thenReturn(false);
        Folder saved = new Folder(1L, "docs", user);
        when(folderRepository.save(any(Folder.class))).thenReturn(saved);

        FolderDTO result = folderService.createFolder(dto);

        assertEquals("docs", result.getName());
        verify(folderRepository).save(any(Folder.class));
    }

    @Test
    @DisplayName("createFolder arunca exceptie cand utilizatorul nu exista")
    void createFolder_throwsWhenUserNotFound() {
        FolderDTO dto = new FolderDTO();
        dto.setName("docs");
        dto.setOwnerUsername("ghost");
        when(userRepository.findByUsername("ghost")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> folderService.createFolder(dto));
        verify(folderRepository, never()).save(any());
    }

    @Test
    @DisplayName("createFolder arunca exceptie cand exista deja un folder cu acelasi nume")
    void createFolder_throwsWhenDuplicateName() {
        User user = buildUser("john");
        FolderDTO dto = new FolderDTO();
        dto.setName("docs");
        dto.setOwnerUsername("john");
        when(userRepository.findByUsername("john")).thenReturn(Optional.of(user));
        when(folderRepository.existsByNameAndUserAndParentId("docs", user, null)).thenReturn(true);

        assertThrows(RuntimeException.class, () -> folderService.createFolder(dto));
        verify(folderRepository, never()).save(any());
    }

    @Test
    @DisplayName("moveFolder muta folderul cand utilizatorul detine ambele foldere")
    void moveFolder_movesWhenUserOwnsBoth() {
        User user = buildUser("john");
        Folder toMove = new Folder(1L, "src", user);
        Folder target = new Folder(2L, "dest", user);
        when(folderRepository.findById(1L)).thenReturn(Optional.of(toMove));
        when(folderRepository.findById(2L)).thenReturn(Optional.of(target));

        folderService.moveFolder(1L, 2L, "john");

        assertEquals(target, toMove.getParentId());
        verify(folderRepository).save(toMove);
    }

    @Test
    @DisplayName("moveFolder arunca exceptie cand folderele apartin altui utilizator")
    void moveFolder_throwsWhenAccessDenied() {
        Folder toMove = new Folder(1L, "src", buildUser("john"));
        Folder target = new Folder(2L, "dest", buildUser("mary"));
        when(folderRepository.findById(1L)).thenReturn(Optional.of(toMove));
        when(folderRepository.findById(2L)).thenReturn(Optional.of(target));

        assertThrows(RuntimeException.class, () -> folderService.moveFolder(1L, 2L, "john"));
        verify(folderRepository, never()).save(any());
    }

    @Test
    @DisplayName("moveFolder arunca exceptie cand un folder ar deveni propriul parinte")
    void moveFolder_throwsWhenMovingIntoItself() {
        User user = buildUser("john");
        Folder folder = new Folder(1L, "src", user);
        // Acelasi obiect pentru ambele ID-uri => folder == target
        when(folderRepository.findById(1L)).thenReturn(Optional.of(folder));

        assertThrows(RuntimeException.class, () -> folderService.moveFolder(1L, 1L, "john"));
        verify(folderRepository, never()).save(any());
    }

    @Test
    @DisplayName("deleteFolderAndFiles sterge un folder gol al utilizatorului")
    void deleteFolderAndFiles_deletesEmptyOwnedFolder() {
        User user = buildUser("john");
        Folder folder = new Folder(1L, "docs", user);
        when(folderRepository.findById(1L)).thenReturn(Optional.of(folder));
        when(folderRepository.findByParentId(folder)).thenReturn(Collections.emptyList());

        folderService.deleteFolderAndFiles(1L, "john");

        verify(folderRepository).delete(folder);
    }

    @Test
    @DisplayName("deleteFolderAndFiles arunca exceptie cand folderul nu apartine utilizatorului")
    void deleteFolderAndFiles_throwsWhenUnauthorized() {
        Folder folder = new Folder(1L, "docs", buildUser("mary"));
        when(folderRepository.findById(1L)).thenReturn(Optional.of(folder));

        assertThrows(RuntimeException.class,
                () -> folderService.deleteFolderAndFiles(1L, "john"));
        verify(folderRepository, never()).delete(any());
    }

    @Test
    @DisplayName("getFoldersInFolder returneaza subfolderele cand utilizatorul are acces")
    void getFoldersInFolder_returnsChildrenWhenAuthorized() {
        User user = buildUser("john");
        Folder parent = new Folder(1L, "parent", user);
        Folder child = new Folder(2L, "child", user, parent);
        when(folderRepository.findById(1L)).thenReturn(Optional.of(parent));
        when(folderRepository.findByParentId(parent)).thenReturn(List.of(child));

        List<FolderDTO> result = folderService.getFoldersInFolder(1L, "john");

        assertEquals(1, result.size());
        assertEquals("child", result.get(0).getName());
    }

    @Test
    @DisplayName("getFoldersInFolder arunca exceptie cand utilizatorul nu are acces")
    void getFoldersInFolder_throwsWhenAccessDenied() {
        Folder parent = new Folder(1L, "parent", buildUser("mary"));
        when(folderRepository.findById(1L)).thenReturn(Optional.of(parent));

        assertThrows(RuntimeException.class,
                () -> folderService.getFoldersInFolder(1L, "john"));
    }

    @Test
    @DisplayName("updateFolder redenumeste folderul utilizatorului")
    void updateFolder_renamesOwnedFolder() {
        User user = buildUser("john");
        Folder folder = new Folder(1L, "old", user);
        FolderDTO dto = new FolderDTO();
        dto.setId(1L);
        dto.setName("new");
        when(folderRepository.findByIdAndUsername(1L, "john")).thenReturn(folder);
        when(folderRepository.save(folder)).thenReturn(folder);

        FolderDTO result = folderService.updateFolder("john", dto);

        assertEquals("new", folder.getName());
        assertEquals("new", result.getName());
    }

    private User buildUser(String username) {
        User user = new User();
        user.setUsername(username);
        return user;
    }
}
