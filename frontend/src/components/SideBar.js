import React, { useState, useEffect } from "react";
import shared from "../assets/images/shared.png";
import trash from "../assets/images/trash.png";
import "../css/SideBar.css";
import files from "../assets/images/folder.png";
import FileUploadButton from "./FileUploadButton";
import FolderCreateButton from "./FolderCreateButton";
import { useNavigate } from "react-router-dom";
import { Spinner, ProgressBar } from "react-bootstrap";
import API from "./AxiosConfig";

export default function SideBar({ currentPath, fetchData }) {
  const [activeButton, setActiveButton] = useState(null);
  const [isDarkMode, setIsDarkMode] = useState(false);
  const [isUploading, setIsUploading] = useState(false);
  const [storageDetails, setStorageDetails] = useState({ totalSpace: 0, usedSpace: 0 });
  const navigate = useNavigate();

  useEffect(() => {
    // Fetch storage details
    fetchStorageDetails();
  }, []);

  const fetchStorageDetails = async () => {
    try {
      const response = await API.get("/user/storage");
      setStorageDetails(response.data);
      console.log(response.data);
    } catch (error) {
      console.error("Error fetching storage details:", error);
    }
  };

  const menuOptions = [
    { id: "My Files", icon: files, label: "My Files" },
    { id: "Shared with me", icon: shared, label: "Shared with me" },
  ];

  const handlePages = (id) => {
    if (id === "My Files") {
      navigate("/home");
    }
    if (id === "Shared with me") {
      navigate("/shared");
    }
    if (id === "Trash") {
      navigate("/bin");
    }
  };

  const storageUsedPercentage = 
    storageDetails.totalSpace > 0 
      ? (storageDetails.usedSpace / storageDetails.totalSpace) * 100 
      : 0;

  return (
    <div id="sideBar">
      {/* File Upload Button with spinner */}
      <div style={{ display: "flex", alignItems: "center", position: "relative" }}>
        <FileUploadButton
          currentPath={currentPath}
          onUploadStart={() => setIsUploading(true)} // Show spinner
          onUploadEnd={() => setIsUploading(false)} // Hide spinner
          fetchData={fetchData}
        />
        {isUploading && (
          <Spinner
            animation="grow"
            style={{
              marginLeft: "10px", // Add some spacing between the button and the spinner
            }}
          />
        )}
      </div>

      {/* Folder Create Button */}
      <FolderCreateButton currentPath={currentPath} fetchData={fetchData} />

      {/* Sidebar Options */}
      <div id="sideBarOpt">
        {menuOptions.map((option) => (
          <button
            key={option.id}
            className={`sideBarOptions ${
              activeButton === option.id ? "activeSideOpt" : ""
            }`}
            onClick={() => {
              setActiveButton(option.id);
              handlePages(option.id);
            }}
          >
            <img
              src={option.icon}
              alt={option.label}
              className={`opacity ${isDarkMode ? "dark-icon" : "light-icon"}`}
            />
            <h3>{option.label}</h3>
          </button>
        ))}
      </div>

      {/* Storage Info with ProgressBar */}
      <div id="storageInfo">
      <p>
        {(storageDetails.usedSpace || 0).toFixed(2)} GB of {(storageDetails.totalSpace || 0).toFixed(2)} GB Used
      </p>

        <ProgressBar
          now={storageUsedPercentage}
          label={`${Math.round(storageUsedPercentage)}%`}
          striped
          variant="info"
        />
      </div>
    </div>
  );
}
