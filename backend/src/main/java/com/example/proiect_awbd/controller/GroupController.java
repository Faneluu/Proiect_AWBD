package com.example.proiect_awbd.controller;


import com.example.proiect_awbd.dto.GroupDTO;
import com.example.proiect_awbd.service.GroupService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/v1/groups")
public class GroupController {

    private static final Logger logger = LoggerFactory.getLogger(GroupController.class);

    @Autowired
    private GroupService groupService;


    @GetMapping("/")
    public ResponseEntity<List<GroupDTO>> getGroupsByUsername() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        List<GroupDTO> groups = groupService.getGroupsByUsername(username);
        return ResponseEntity.ok(groups);
    }


    @PostMapping("/{groupName}")
    public ResponseEntity<String> createGroup(@PathVariable String groupName) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        logger.info("Creare grup '{}' de catre utilizatorul '{}'", groupName, username);
        groupService.createGroup(groupName, username);
        return ResponseEntity.ok("Group created successfully");
    }


    @DeleteMapping("/{groupName}")
    public ResponseEntity<String> deleteGroup(@PathVariable String groupName) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        logger.info("Stergere grup '{}' de catre utilizatorul '{}'", groupName, username);
        groupService.deleteGroup(groupName, username);
        return ResponseEntity.ok("Group deleted successfully");
    }


    @PostMapping("/{groupName}/{username}")
    public ResponseEntity<String> addUserToGroup(@PathVariable String groupName, @PathVariable String username) {
        String leaderUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        logger.info("Adaugare utilizator '{}' in grupul '{}' de catre liderul '{}'", username, groupName, leaderUsername);
        groupService.addUserToGroup(groupName, leaderUsername, username);
        return ResponseEntity.ok("User added to group successfully");
    }


    @DeleteMapping("/{groupName}/{username}")
    public ResponseEntity<String> removeUserFromGroup(@PathVariable String groupName, @PathVariable String username) {
        String leaderUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        logger.info("Eliminare utilizator '{}' din grupul '{}' de catre liderul '{}'", username, groupName, leaderUsername);
        groupService.removeUserFromGroup(groupName, leaderUsername, username);
        return ResponseEntity.ok("User removed from group successfully");
    }

    @DeleteMapping("/leave/{groupName}/{username}")
    public ResponseEntity<String> leaveUserFromGroup(@PathVariable String groupName, @PathVariable String username) {
        String leaderUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        groupService.leaveUserFromGroup(groupName, username);
        return ResponseEntity.ok("User removed from group successfully");
    }
}
