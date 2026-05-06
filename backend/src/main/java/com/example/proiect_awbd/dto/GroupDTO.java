package com.example.proiect_awbd.dto;

import java.util.List;


public class GroupDTO {


    private int groupId;

    private String name;


    private String leader;


    private List<String> members;



    public GroupDTO() {
    }

    public GroupDTO(int groupId, String name, String leader, List<String> members) {
        this.groupId = groupId;
        this.name = name;
        this.leader = leader;
        this.members = members;
    }


    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLeader() {
        return leader;
    }

    public void setLeader(String leader) {
        this.leader = leader;
    }

    public List<String> getMembers() {
        return members;
    }

    public void setMembers(List<String> members) {
        this.members = members;
    }

    @Override
    public String toString() {
        return "GroupDTO{" +
                "groupId=" + groupId +
                ", name='" + name + '\'' +
                ", leader='" + leader + '\'' +
                ", members=" + members +
                '}';
    }
}
