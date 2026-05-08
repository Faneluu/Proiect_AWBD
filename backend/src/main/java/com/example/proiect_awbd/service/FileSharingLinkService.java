package com.example.proiect_awbd.service;

import com.example.proiect_awbd.entity.File;
import com.example.proiect_awbd.entity.FileSharingLink;
import com.example.proiect_awbd.enums.AccessType;
import com.example.proiect_awbd.enums.Permissions;
import com.example.proiect_awbd.repository.FileRepository;
import com.example.proiect_awbd.repository.FileSharingLinkRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class FileSharingLinkService {

    private final FileService fileService;
    FileSharingLinkRepository fileSharingLinkRepository;
    FileRepository fileRepository;

    @Autowired
    public FileSharingLinkService(FileSharingLinkRepository fileSharingLinkRepository,
                                  FileRepository fileRepository, FileService fileService) {
        this.fileSharingLinkRepository = fileSharingLinkRepository;
        this.fileRepository = fileRepository;
        this.fileService = fileService;
    }

    /**
     *
     * @param accessType Daca link-ul este public sau privat.
     * @param permissions Daca fisierul poate fi editat sau nu.
     * @param expiresAt Data de expirare a link-ului
     * @return id-ul unic al intrarii in tabelul cu link-uri
     * @throws RuntimeException in caz ca fisierul nu exista sau timpul este setat gresit
     */
    public String createFileSharingLink(Long id, AccessType accessType,
                                        Permissions permissions, LocalDateTime expiresAt)
            throws RuntimeException {

        Optional<File> file = fileRepository.findById(id);

        if (file.isEmpty()) {
            throw new RuntimeException("Fisierul nu exista");
        }

        if (expiresAt.isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Timpul este setat gresit");
        }

        FileSharingLink fileSharingLink = new FileSharingLink(
                UUID.randomUUID().toString(),
                file.orElse(null),
                accessType,
                expiresAt,
                permissions,
                file.get().getUser()
        );

        fileSharingLinkRepository.save(fileSharingLink);

        return fileSharingLink.getLinkId();
    }

    public String getFilenameBySharingLinkId(String sharingLinkId) {
        FileSharingLink fileSharingLink = fileSharingLinkRepository.findByLinkId(sharingLinkId).orElse(null);
        if (fileSharingLink == null) {
            throw new RuntimeException("Fisierul nu exista");
        }
        return fileSharingLink.getFile().getName();
    }

    public Long getFileIdBySharingLinkId(String sharingLinkId) {
        FileSharingLink fileSharingLink = fileSharingLinkRepository.findByLinkId(sharingLinkId).orElse(null);
        if (fileSharingLink == null) {
            throw new RuntimeException("Fisierul nu exista");
        }
        return fileSharingLink.getFile().getId();
    }

    /**
     * Sterge intrarea din baza de date pentru link (dar nu si fisierul).
     * @param linkId
     */
    public void deleteFileSharingLink(String linkId) {
        if (this.fileSharingLinkRepository.existsByLinkId(linkId)) {
            this.fileSharingLinkRepository.deleteByLinkId(linkId);
        }
    }
}
