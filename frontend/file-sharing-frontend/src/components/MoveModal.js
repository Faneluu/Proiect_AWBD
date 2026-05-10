import React, { useState, useEffect } from "react";
import { Modal, Button, ListGroup } from "react-bootstrap";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faFolder } from "@fortawesome/free-solid-svg-icons";
import API from "../components/AxiosConfig";

const MoveModal = ({ show, onHide, onMove, setItemToMove }) => {
  const [selectedFolderId, setSelectedFolderId] = useState(null);
  const [folders, setFolders] = useState([]);
  const [folderTree, setFolderTree] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    const fetchFolders = async () => {
      try {
        const response = await API.get("/folders/all"); // Fetch all folders
        setFolders(response.data);
        setFolderTree(buildFolderTree(response.data)); // Build folder hierarchy
        setLoading(false);
      } catch (err) {
        console.error("Error fetching folders:", err);
        setError("Failed to load folders. Please try again.");
        setLoading(false);
      }
    };

    if (show) fetchFolders(); // Fetch folders only when modal is shown
  }, [show]);

  // Helper function to build folder hierarchy
  const buildFolderTree = (folders) => {
    const folderMap = {};
    const tree = [];

    // Map folders by their ID
    folders.forEach((folder) => {
      folderMap[folder.id] = { ...folder, children: [] };
    });

    // Build tree structure
    folders.forEach((folder) => {
      if (folder.parentId) {
        folderMap[folder.parentId]?.children.push(folderMap[folder.id]);
      } else {
        tree.push(folderMap[folder.id]); // Root-level folders
      }
    });

    return tree;
  };

  // Recursive component to render folder hierarchy
  const renderFolderTree = (folders, depth = 0) => {
    return (
      <ListGroup>
        {folders.map((folder) => (
          <div key={folder.id} style={{ paddingLeft: `${depth * 20}px` }}>
            <ListGroup.Item
              action
              onClick={() => setSelectedFolderId(folder.id)}
              active={selectedFolderId === folder.id}
            >
              <span style={{ display: "flex", alignItems: "center" }}>
                <FontAwesomeIcon icon={faFolder} style={{ marginRight: "10px", color: "goldenrod" }} />
                {folder.name}
              </span>
            </ListGroup.Item>
            {folder.children.length > 0 && renderFolderTree(folder.children, depth + 1)}
          </div>
        ))}
      </ListGroup>
    );
  };

  return (
    <Modal show={show} onHide={onHide}>
      <Modal.Header closeButton>
        <Modal.Title>Select Target Folder</Modal.Title>
      </Modal.Header>
      <Modal.Body>
        {loading && <p>Loading folders...</p>}
        {error && <p className="text-danger">{error}</p>}
        {!loading && !error && renderFolderTree(folderTree)}
      </Modal.Body>
      <Modal.Footer>
        <Button variant="secondary" onClick={onHide}>
          Cancel
        </Button>
        <Button
          variant="primary"
          onClick={() => {
            onMove(selectedFolderId);
            onHide();
          }}
          disabled={!selectedFolderId}
        >
          Move Here
        </Button>
      </Modal.Footer>
    </Modal>
  );
};

export default MoveModal;
