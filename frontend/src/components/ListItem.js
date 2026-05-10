import React, { useState } from "react";
import { Dropdown, ButtonGroup } from "react-bootstrap";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faFolder, faFile } from "@fortawesome/free-solid-svg-icons";
import "../css/ListItem.css";
import API from "../components/AxiosConfig";
import MoveModal from "./MoveModal";
import ShareModal from "./ShareModal";

export default function ListItem({ item, type, onNavigate, onAction, onDownload, onMove, onDelete, fetchData }) {
  const [showShareModal, setShowShareModal] = useState(false);
  const [showMoveModal, setShowMoveModal] = useState(false);
  const [itemToMove, setItemToMove] = useState(null);
  const [folders, setFolders] = useState([]);
  const [loadingFolders, setLoadingFolders] = useState(false);

  const handleMoveClick = async (item) => {
    setItemToMove(item);
    setShowMoveModal(true);

    setLoadingFolders(true);
    try {
      const response = await API.get("/folders/all");
      setFolders(response.data);
    } catch (error) {
      console.error("Error fetching folders:", error);
      setFolders([]);
    }
    setLoadingFolders(false);
  };

  const handleMove = async (targetFolderId) => {
    try {
      console.log(itemToMove);
      console.log(targetFolderId);
      if (itemToMove.type === "file") {
        await API.put(`/files/${itemToMove.fileId}/move?targetFolderId=${targetFolderId}`);
      } else if (itemToMove.type === "folder") {
        await API.put(`/folders/${itemToMove.id}/move?targetFolderId=${targetFolderId}`);
      }
      fetchData(); // Refresh the list
      setShowMoveModal(false);
      setItemToMove(null);
    } catch (error) {
      console.error("Error moving item:", error);
    }
  };
  
  

  const formatFileSize = (size) => {
    if (size >= 100 * 1024) {
      return `${(size / (1024 * 1024)).toFixed(2)} MB`;
    }
    return `${(size / 1024).toFixed(2)} KB`;
  };

  return (
    <>
      <div
        className="list-item d-flex align-items-center"
        onClick={() => type === "folder" && onNavigate(item)}
        onContextMenu={(e) => {
          e.preventDefault();
          onAction(item);
        }}
      >
        {/* Icon and Name Section */}
        <div className="list-item-name d-flex align-items-center" style={{ width: "20%" }}>
          <FontAwesomeIcon
            icon={type === "folder" ? faFolder : faFile}
            className="me-3"
            style={{ color: type === "folder" ? "Dodgerblue" : "gray" }}
          />
          <div className="scrolling-text-container">
            <span className={item.name.length > 38 ? "scrolling-text" : undefined}>{item.name}</span>
          </div>
        </div>

        {/* File Details Section */}
        {type === "file" ? (
          <div className="list-item-details d-flex justify-content-start" style={{ width: "70%" }}>
            <span style={{ width: "40%" }}>{new Date(item.updatedAt).toLocaleString()}</span>
            <span style={{ width: "30%" }}>{formatFileSize(item.fileSize)}</span>
            <span style={{ width: "30%" }}>{item.fileType}</span>
          </div>
        ) : (
          <span style={{ width: "100%" }}></span>
        )}

        {/* Action Section */}
        <div className="list-item-actions" style={{ width: "10%", textAlign: "right" }}>
          <Dropdown as={ButtonGroup} onClick={(e) => e.stopPropagation()}>
            <Dropdown.Toggle variant="secondary" id={`dropdown-${item.name}`} size="sm">
              Options
            </Dropdown.Toggle>
            <Dropdown.Menu>
              <Dropdown.Item onClick={(e) => { e.stopPropagation(); onDownload(item); }}>Download</Dropdown.Item>
              <Dropdown.Item onClick={(e) => { e.stopPropagation(); handleMoveClick(item); }}>Move</Dropdown.Item>
              <Dropdown.Item onClick={(e) => { e.stopPropagation(); onDelete(item); }}>Delete</Dropdown.Item>
              <Dropdown.Item onClick={(e) => { e.stopPropagation(); setShowShareModal(true); }}>Share</Dropdown.Item>
            </Dropdown.Menu>
          </Dropdown>
        </div>
      </div>

      {/* Modals */}
      <ShareModal
        show={showShareModal}
        onHide={() => setShowShareModal(false)}
        item={item}
      />

      <MoveModal
        show={showMoveModal}
        onHide={() => setShowMoveModal(false)}
        onMove={handleMove}
        folders={folders}
        loading={loadingFolders}
        setItemToMove={setItemToMove}
      />
    </>
  );
}
