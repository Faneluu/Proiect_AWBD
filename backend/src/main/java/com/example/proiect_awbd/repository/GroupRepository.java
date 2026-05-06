package com.example.proiect_awbd.repository;

import com.example.proiect_awbd.entity.Group;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface GroupRepository extends JpaRepository<Group, Integer> {

    @Query("""
    SELECT g FROM Group g 
    LEFT JOIN g.users u 
    WHERE u.username = :username OR g.leader.username = :username
""")
    List<Group> findGroupsByMemberUsername(@Param("username") String username);

    Optional<Group> findByNameAndLeaderUsername(String groupName, String leaderUsername);

    Optional<Group> findByName(String groupName);

    @NotNull List<Group> findAll();
}
