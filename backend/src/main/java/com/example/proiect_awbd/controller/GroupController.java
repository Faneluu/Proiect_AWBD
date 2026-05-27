package com.example.proiect_awbd.controller;


import com.example.proiect_awbd.dto.GroupDTO;
import com.example.proiect_awbd.service.GroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/v1/groups")
public class GroupController {

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
        groupService.createGroup(groupName, username);
        return ResponseEntity.ok("Group created successfully");
    }


    @DeleteMapping("/{groupName}")
    public ResponseEntity<String> deleteGroup(@PathVariable String groupName) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        groupService.deleteGroup(groupName, username);
        return ResponseEntity.ok("Group deleted successfully");
    }


    @PostMapping("/{groupName}/{username}")
    public ResponseEntity<String> addUserToGroup(@PathVariable String groupName, @PathVariable String username) {
        String leaderUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        groupService.addUserToGroup(groupName, leaderUsername, username);
        return ResponseEntity.ok("User added to group successfully");
    }


    @DeleteMapping("/{groupName}/{username}")
    public ResponseEntity<String> removeUserFromGroup(@PathVariable String groupName, @PathVariable String username) {
        String leaderUsername = SecurityContextHolder.getContext().getAuthentication().getName();
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
