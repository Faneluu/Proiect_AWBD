package com.example.proiect_awbd.service;


import com.example.proiect_awbd.dto.GroupDTO;
import com.example.proiect_awbd.entity.Group;
import com.example.proiect_awbd.entity.User;
import com.example.proiect_awbd.repository.GroupRepository;
import com.example.proiect_awbd.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
public class GroupService {

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private UserRepository userRepository;

    public List<GroupDTO> getGroupsByUsername(String username) {
        List<Group> groups = groupRepository.findGroupsByMemberUsername(username);
        return groups.stream().map(this::toDTO).collect(Collectors.toList());
    }


    @Transactional
    public void createGroup(String groupName, String leaderUsername) {
        if (groupRepository.findByName(groupName).isPresent()) {
            throw new IllegalArgumentException("Group with the name '" + groupName + "' already exists");
        }

        Optional<User> leader = userRepository.findByUsername(leaderUsername);
        if (leader.isEmpty()) {
            throw new IllegalArgumentException("Leader user not found");
        }

        Group group = new Group();
        group.setName(groupName);
        group.setLeader(leader.get());
        groupRepository.save(group);
    }



    @Transactional
    public void deleteGroup(String groupName, String leaderUsername) {
        Group group = groupRepository.findByNameAndLeaderUsername(groupName, leaderUsername)
                .orElseThrow(() -> new IllegalArgumentException("Group not found or you are not the leader"));

        groupRepository.delete(group);
    }


    @Transactional
    public void addUserToGroup(String groupName, String leaderUsername, String username) {
        Group group = groupRepository.findByNameAndLeaderUsername(groupName, leaderUsername)
                .orElseThrow(() -> new IllegalArgumentException("Group not found or you are not the leader"));

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (group.getUsers().contains(user)) {
            throw new IllegalArgumentException("User is already in the group");
        }

        group.getUsers().add(user);
        groupRepository.save(group);
    }

    @Transactional
    public void removeUserFromGroup(String groupName, String leaderUsername, String username) {
        Group group = groupRepository.findByNameAndLeaderUsername(groupName, leaderUsername)
                .orElseThrow(() -> new IllegalArgumentException("Group not found or you are not the leader"));

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (!group.getUsers().remove(user)) {
            throw new IllegalArgumentException("User is not a member of the group");
        }

        groupRepository.save(group);
    }

    @Transactional
    public void leaveUserFromGroup(String groupName, String username) {
        Group group = groupRepository.findByName(groupName)
                .orElseThrow(() -> new IllegalArgumentException("Group not found "));

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (!group.getUsers().remove(user)) {
            throw new IllegalArgumentException("User is not a member of the group");
        }

        groupRepository.save(group);
    }


    private GroupDTO toDTO(Group group) {
        return new GroupDTO(
                group.getId(),
                group.getName(),
                group.getLeader().getUsername(),
                group.getUsers().stream().map(User::getUsername).collect(Collectors.toList())
        );
    }
}
