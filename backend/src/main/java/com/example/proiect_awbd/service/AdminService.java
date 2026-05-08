package com.example.proiect_awbd.service;

import com.example.proiect_awbd.dto.StatisticsDTO;
import com.example.proiect_awbd.entity.User;
import com.example.proiect_awbd.repository.FileRepository;
import com.example.proiect_awbd.repository.GroupRepository;
import com.example.proiect_awbd.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AdminService {

    private final UserRepository userRepository;
    private final FileRepository fileRepository;
    private final GroupRepository groupRepository;

    @Autowired
    public AdminService(UserRepository userRepository, FileRepository fileRepository, GroupRepository groupRepository) {
        this.userRepository = userRepository;
        this.fileRepository = fileRepository;
        this.groupRepository = groupRepository;
    }

    public void allocateSpace(String username, Float space) {
        User user = userRepository.findById(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found."));

        if (space < 0) {
            throw new IllegalArgumentException("Space allocation must be positive.");
        }

        user.setTotalSpace(space);
        userRepository.save(user);
    }

    public Map<String, List<StatisticsDTO<?>>> getStatistics() {
        Map<String, List<StatisticsDTO<?>>> graphData = new HashMap<>();

        List<StatisticsDTO<?>> fileData = fileRepository.findAll().stream()
                .map(file -> new StatisticsDTO<>(file.getName(), file.getFileSize()))
                .collect(Collectors.toList());
        graphData.put("Files", fileData);

        List<StatisticsDTO<?>> groupData = groupRepository.findAll().stream()
                .map(group -> new StatisticsDTO<>(group.getName(), group.getUsers().size() + 1))
                .collect(Collectors.toList());
        graphData.put("Groups", groupData);

        return graphData;
    }
}
