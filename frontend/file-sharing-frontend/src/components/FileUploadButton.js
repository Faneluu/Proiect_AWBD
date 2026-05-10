import React, { useState, useRef } from "react";
import API from "./AxiosConfig";
import new_file from "../assets/images/drive_icon.png";
import { Alert } from "react-bootstrap";
import "../css/FileUploadButton.css"; // Add custom styles
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faFile } from "@fortawesome/free-solid-svg-icons";

function FileUploadButton({ currentPath, onUploadStart, onUploadEnd }) {
  const [alert, setAlert] = useState(null);
  const fileInputRef = useRef();

  const handleButtonClick = () => {
    fileInputRef.current.click();
  };

  const handleFileChange = async (event) => {
    const file = event.target.files[0];
    if (file) {
      const formData = new FormData();
      formData.append("file", file);

      try {
        onUploadStart(); // Start spinner
        let response;
        if (currentPath.path === "/") {
          response = await API.post(`/files/${file.name}`, formData);
        } else {
          response = await API.post(`/files/${currentPath.id}/${file.name}`, formData);
        }
        console.log("Upload successful:", response.data);
        setAlert({ variant: "success", message: "Upload successful!" });

        setTimeout(() => {
          setAlert(null);
        }, 3000);
      } catch (error) {
        console.error("Error uploading file:", error);
        setAlert({ variant: "danger", message: "Error uploading file." });
        setTimeout(() => {
          setAlert(null);
        }, 3000);
      } finally {
        onUploadEnd();
      }
    }
  };

  return (
    <div className="file-upload-container">
      {alert && (
        <Alert variant={alert.variant} className="upload-alert">
          {alert.message}
        </Alert>
      )}
      <button id="linkBtn" onClick={handleButtonClick}>
        <FontAwesomeIcon
          icon={faFile}
          style={{ color: "gray"}}
        />
        <p>New</p>
      </button>
      <input
        type="file"
        ref={fileInputRef}
        style={{ display: "none" }}
        onChange={handleFileChange}
      />
    </div>
  );
}

export default FileUploadButton;
