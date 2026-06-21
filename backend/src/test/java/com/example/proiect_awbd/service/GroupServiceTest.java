package com.example.proiect_awbd.service;

import com.example.proiect_awbd.dto.GroupDTO;
import com.example.proiect_awbd.entity.Group;
import com.example.proiect_awbd.entity.User;
import com.example.proiect_awbd.repository.GroupRepository;
import com.example.proiect_awbd.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Teste unitare pentru {@link GroupService} (JUnit 5 + Mockito).
 */
@ExtendWith(MockitoExtension.class)
class GroupServiceTest {

    @Mock
    private GroupRepository groupRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private GroupService groupService;

    @Test
    @DisplayName("createGroup salveaza un grup nou cand numele este liber si liderul exista")
    void createGroup_savesWhenNameFreeAndLeaderExists() {
        User leader = buildUser("leader");
        when(groupRepository.findByName("devs")).thenReturn(Optional.empty());
        when(userRepository.findByUsername("leader")).thenReturn(Optional.of(leader));

        groupService.createGroup("devs", "leader");

        verify(groupRepository).save(argThat(g ->
                "devs".equals(g.getName()) && g.getLeader() == leader));
    }

    @Test
    @DisplayName("createGroup arunca exceptie cand grupul exista deja")
    void createGroup_throwsWhenGroupExists() {
        when(groupRepository.findByName("devs")).thenReturn(Optional.of(new Group()));

        assertThrows(IllegalArgumentException.class,
                () -> groupService.createGroup("devs", "leader"));
        verify(groupRepository, never()).save(any());
    }

    @Test
    @DisplayName("createGroup arunca exceptie cand liderul nu exista")
    void createGroup_throwsWhenLeaderNotFound() {
        when(groupRepository.findByName("devs")).thenReturn(Optional.empty());
        when(userRepository.findByUsername("leader")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> groupService.createGroup("devs", "leader"));
        verify(groupRepository, never()).save(any());
    }

    @Test
    @DisplayName("deleteGroup sterge grupul cand exista si solicitantul e liderul")
    void deleteGroup_deletesWhenLeaderOwnsGroup() {
        Group group = new Group();
        when(groupRepository.findByNameAndLeaderUsername("devs", "leader"))
                .thenReturn(Optional.of(group));

        groupService.deleteGroup("devs", "leader");

        verify(groupRepository).delete(group);
    }

    @Test
    @DisplayName("deleteGroup arunca exceptie cand grupul nu exista sau nu esti liderul")
    void deleteGroup_throwsWhenNotFoundOrNotLeader() {
        when(groupRepository.findByNameAndLeaderUsername("devs", "intruder"))
                .thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> groupService.deleteGroup("devs", "intruder"));
    }

    @Test
    @DisplayName("addUserToGroup adauga un membru nou si salveaza grupul")
    void addUserToGroup_addsNewMember() {
        Group group = new Group();
        User member = buildUser("member");
        when(groupRepository.findByNameAndLeaderUsername("devs", "leader"))
                .thenReturn(Optional.of(group));
        when(userRepository.findByUsername("member")).thenReturn(Optional.of(member));

        groupService.addUserToGroup("devs", "leader", "member");

        assertTrue(group.getUsers().contains(member));
        verify(groupRepository).save(group);
    }

    @Test
    @DisplayName("addUserToGroup arunca exceptie cand utilizatorul e deja membru")
    void addUserToGroup_throwsWhenAlreadyMember() {
        Group group = new Group();
        User member = buildUser("member");
        group.getUsers().add(member);
        when(groupRepository.findByNameAndLeaderUsername("devs", "leader"))
                .thenReturn(Optional.of(group));
        when(userRepository.findByUsername("member")).thenReturn(Optional.of(member));

        assertThrows(IllegalArgumentException.class,
                () -> groupService.addUserToGroup("devs", "leader", "member"));
        verify(groupRepository, never()).save(any());
    }

    @Test
    @DisplayName("removeUserFromGroup elimina un membru existent")
    void removeUserFromGroup_removesExistingMember() {
        Group group = new Group();
        User member = buildUser("member");
        group.getUsers().add(member);
        when(groupRepository.findByNameAndLeaderUsername("devs", "leader"))
                .thenReturn(Optional.of(group));
        when(userRepository.findByUsername("member")).thenReturn(Optional.of(member));

        groupService.removeUserFromGroup("devs", "leader", "member");

        assertFalse(group.getUsers().contains(member));
        verify(groupRepository).save(group);
    }

    @Test
    @DisplayName("removeUserFromGroup arunca exceptie cand userul nu e membru")
    void removeUserFromGroup_throwsWhenNotMember() {
        Group group = new Group();
        User member = buildUser("member");
        when(groupRepository.findByNameAndLeaderUsername("devs", "leader"))
                .thenReturn(Optional.of(group));
        when(userRepository.findByUsername("member")).thenReturn(Optional.of(member));

        assertThrows(IllegalArgumentException.class,
                () -> groupService.removeUserFromGroup("devs", "leader", "member"));
        verify(groupRepository, never()).save(any());
    }

    @Test
    @DisplayName("getGroupsByUsername mapeaza grupurile in DTO-uri")
    void getGroupsByUsername_mapsToDtos() {
        User leader = buildUser("leader");
        Group group = new Group(1, "devs", leader);
        when(groupRepository.findGroupsByMemberUsername("leader"))
                .thenReturn(List.of(group));

        List<GroupDTO> result = groupService.getGroupsByUsername("leader");

        assertEquals(1, result.size());
        assertEquals("devs", result.get(0).getName());
        assertEquals("leader", result.get(0).getLeader());
    }

    private User buildUser(String username) {
        User user = new User();
        user.setUsername(username);
        return user;
    }
}
