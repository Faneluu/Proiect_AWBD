package com.example.proiect_awbd.service;

import com.example.proiect_awbd.dto.UserStatsDTO;
import com.example.proiect_awbd.entity.User;
import com.example.proiect_awbd.repository.UserRepository;
import org.springframework.stereotype.Service;

/**
 * Service pentru calcularea statisticilor unui utilizator.
 * Returneaza informatii despre spatiul folosit, fisiere, foldere si grupuri.
 */
@Service
public class UserStatsService {

    private final UserRepository userRepository;

    public UserStatsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Calculeaza si returneaza statisticile pentru un utilizator dat.
     *
     * @param username numele utilizatorului
     * @return un obiect UserStatsDTO cu statisticile utilizatorului
     */
    public UserStatsDTO getUserStats(String username) {
        User user = userRepository.findById(username)
                .orElseThrow(() -> new RuntimeException("Utilizatorul nu a fost gasit: " + username));

        int fileCount = user.getFiles().size();
        int folderCount = user.getFolders().size();
        int groupCount = user.getLedGroups().size();

        return new UserStatsDTO(
                user.getUsername(),
                user.getTotalSpace(),
                user.getUsedSpace(),
                fileCount,
                folderCount,
                groupCount
        );
    }
}