import React, { useState } from "react";
import API from "./AxiosConfig";
import new_folder from "../assets/images/folder.png"; // Add an appropriate icon for the folder button
import { Modal, Form, Alert } from "react-bootstrap";
import "../css/FolderCreateButton.css";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faFolder } from "@fortawesome/free-solid-svg-icons";

function FolderCreateButton({ currentPath}) {
  const [showModal, setShowModal] = useState(false);
  const [folderName, setFolderName] = useState("");
  const [alert, setAlert] = useState(null);

  const handleCreateFolder = async () => {
    if (!folderName) {
      setAlert({ variant: "danger", message: "Folder name cannot be empty." });
      return;
    }

    try {
      let response;
      if (currentPath.path === "/") {
        response = await API.post("/folders/", { name: folderName });
      } else {
        response = await API.post(`/folders/${currentPath.id}/child`, { name: folderName });
      }

      setAlert({
        variant: "success",
        message: `Folder "${folderName}" created successfully!`,
      });
      setFolderName("");
      setShowModal(false);
      setTimeout(() => {
        setAlert(null);
      }, 3000);

    } catch (err) {
      setAlert({
        variant: "danger",
        message: "Failed to create folder. Please try again.",
      });
      console.error(err);
      setTimeout(() => {
        setAlert(null);
      }, 3000);
    }
  };

  return (
    <>
      {alert && (
        <Alert variant={alert.variant} className="upload-alert">
          {alert.message}
        </Alert>
      )}
      <button id="linkBtn" onClick={() => setShowModal(true)}>
        <FontAwesomeIcon
                    icon={faFolder}
                    style={{ color: "Dodgerblue"}}
                  />
        <p>New Folder</p>
      </button>

      {/* Modal */}
      <Modal show={showModal} onHide={() => setShowModal(false)}>
        <Modal.Header closeButton>
          <Modal.Title>Create New Folder</Modal.Title>
        </Modal.Header>
        <Modal.Body>
          <Form>
            <Form.Group controlId="folderName">
              <Form.Label>Folder Name</Form.Label>
              <Form.Control
                type="text"
                placeholder="Enter folder name"
                value={folderName}
                onChange={(e) => setFolderName(e.target.value)}
              />
            </Form.Group>
          </Form>
        </Modal.Body>
        <Modal.Footer>
          <button
            className="btn btn-secondary"
            onClick={() => setShowModal(false)}
          >
            Cancel
          </button>
          <button className="btn btn-primary" onClick={handleCreateFolder}>
            Create Folder
          </button>
        </Modal.Footer>
      </Modal>
    </>
  );
}

export default FolderCreateButton;
