import React, { useState, useEffect } from "react";
import Navbar from "../../components/Navbar";
import SideBar from "../../components/SideBar";
import Cookies from "js-cookie";
import API from "../../components/AxiosConfig";
import { CiCircleAlert } from "react-icons/ci";
import "../../css/UserShared.css";

function UserShared() {
  const [username, setUsername] = useState("");
  const [groups, setGroups] = useState([]);
  const [error, setError] = useState("");
  const [newGroupName, setNewGroupName] = useState("");
  const [newMembers, setNewMembers] = useState({});
  const [groupErrors, setGroupErrors] = useState({});
  const [selectedFile, setSelectedFile] = useState(null);


  const parseJWT = (token) => {
    try {
      const base64Payload = token.split(".")[1];
      const decodedPayload = atob(base64Payload);
      return JSON.parse(decodedPayload);
    } catch (e) {
      console.error("Invalid JWT:", e);
      return null;
    }
  };


  const fetchGroups = async () => {
    try {
      const token = Cookies.get("JWT");
      if (token) {
        const decodedToken = parseJWT(token);
        setUsername(decodedToken.sub);
      }
      const response = await API.get("/groups/");
      setGroups(response.data);
    } catch (err) {
      console.error("Error fetching groups:", err.response?.data || err.message);
      setError(err.response?.data || "Failed to load groups");
    }
  };

  const handleNewMemberChange = (groupName, e) => {
    setNewMembers((prevNewMembers) => ({
      ...prevNewMembers,
      [groupName]: e.target.value,
    }));
  };


  useEffect(() => {
    fetchGroups();
  }, []);

  const handleCreateGroup = async () => {
    if (newGroupName.trim()) {
      try {
        await API.post(`/groups/${newGroupName}`);
        window.location.reload();
      } catch (error) {
        console.error("Error creating group:", error);
        setError(error.response.data);
      }
    }
  };

  const handleDeleteGroup = async (groupName) => {
    await API.delete(`/groups/${groupName}`);
    window.location.reload();
  };

  const handleAddMember = async (groupName) => {
    const memberName = newMembers[groupName];
    try {
      if (memberName == username) {
        setGroupErrors((prevErrors) => ({
          ...prevErrors,
          [groupName]: "You are already in the group.",
        }));
      } else {
        await API.post(`/groups/${groupName}/${memberName}`);
        window.location.reload();
      }
    } catch (err) {
      setGroupErrors((prevErrors) => ({
        ...prevErrors,
        [groupName]: err.response?.data || "Failed to add member",
      }));
    }
  };

  const handleRemoveMember = async(groupName, memberName) => {
    try {
      await API.delete(`/groups/${groupName}/${memberName}`);
      setGroups(
        groups.map((group) =>
          group.name === groupName
            ? { ...group, members: group.members.filter((m) => m !== memberName) }
            : group
        )
      );
      setGroupErrors((prevErrors) => ({ ...prevErrors, [groupName]: "" }));
    } catch (err) {
      setGroupErrors((prevErrors) => ({
        ...prevErrors,
        [groupName]: err.response?.data || "Failed to remove member.",
      }));
    }
  };

  const handleLeaveGroup = async (groupName) => {
    try {
      await API.delete(`/groups/leave/${groupName}/${username}`);
      window.location.reload();
    } catch (error) {
      setGroupErrors((prevErrors) => ({
        ...prevErrors,
        [groupName]: error.response?.data || "Error leaving group.",
      }));
    }
  };

  const handleFileSelection = (event) => {
    setSelectedFile(event.target.files[0]);
  };

  return (
    <>
      <Navbar />
      <div id="mainCont">
        <SideBar />
        <div id="content">
          <h1>Manage Groups</h1>

          <div className="group-actions">
            <input
              type="text"
              value={newGroupName}
              onChange={(e) => setNewGroupName(e.target.value)}
              placeholder="Enter group name"
            />
            <button className="btn btn-primary" onClick={handleCreateGroup}>
              Create Group
            </button>
          </div>
          {error && <div className="error-message"><CiCircleAlert /> {error}</div>}
          <div className="group-list">
            {groups.map((group) => (
              <div key={group.name} className="group-item card">
                <h2 className="group-title">{group.name}</h2>
                <p className="group-owner">Owner: {group.leader}</p>
                {groupErrors[group.name] && (
                  <p className="error-message">
                    <CiCircleAlert /> {groupErrors[group.name]}
                  </p>
                )}
                <div className="members">
                  <h3>Members:</h3>
                  {group.members.length > 0 ? (
                    group.members.map((member) => (
                      <div key={member} className="member-item">
                        {member}
                        {group.leader === username && member !== group.leader && (
                          <button
                            className="btn btn-sm btn-danger"
                            onClick={() => handleRemoveMember(group.name, member)}
                          >
                            Remove
                          </button>
                        )}
                      </div>
                    ))
                  ) : (
                    <p>No members</p>
                  )}
                </div>
                {group.leader === username && (
                  <div className="add-member">
                    <input
                      type="text"
                      value={newMembers[group.name] || ""}
                      onChange={(e) => handleNewMemberChange(group.name, e)}
                      placeholder="Enter member name"
                    />
                    <button className="btn btn-success" onClick={() => handleAddMember(group.name)}  >
                      Add Member
                    </button>
                  </div>
                )}
                <div className="files">
                  <h3>Files:</h3>
                    <div className="add-file">
                      <input
                        type="file"
                        onChange={handleFileSelection}
                        style={{ display: "inline-block", marginRight: "10px" }}
                      />
                      <button
                        className="btn btn-success"
                        disabled={!selectedFile}
                      >
                        Add File
                      </button>
                    </div>
                </div>
                <div className="group-options" style={{ marginTop: "20px" }}>
                  {group.leader === username ? (
                    <button
                      className="btn btn-danger"
                      onClick={() => handleDeleteGroup(group.name)}
                    >
                      Delete Group
                    </button>
                  ) : (
                    <button
                      className="btn btn-warning"
                      onClick={() => handleLeaveGroup(group.name)}
                    >
                      Leave Group
                    </button>
                  )}
                </div>
              </div>
            ))}
          </div>
        </div>
      </div>
    </>
  );
}

export default UserShared;