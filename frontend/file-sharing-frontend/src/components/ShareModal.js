import React, { useState } from "react";
import { Modal, Form, Button, Alert } from "react-bootstrap";
import API from "../components/AxiosConfig";

export default function ShareModal({ show, onHide, item }) {
  const [shareData, setShareData] = useState({
    fileId: item?.fileId || null,
    permissions: "READ",
    accessType: "PRIVATE",
    expiresAt: "",
  });
  const [shareLink, setShareLink] = useState("");
  const [error, setError] = useState("");

  const handleShareSubmit = async () => {
    try {
      const response = await API.post("/links", shareData);
      setShareLink(response.data); // Assuming the API returns the share link in the response
      setError("");
    } catch (error) {
      console.error("Error creating share link:", error);
      setError("Failed to create the share link. Please try again.");
    }
  };

  return (
    <Modal show={show} onHide={onHide}>
      <Modal.Header closeButton>
        <Modal.Title>Share {item?.name}</Modal.Title>
      </Modal.Header>
      <Modal.Body>
        {shareLink && (
          <Alert variant="success">
            Share link created: <strong>/links/{shareLink}</strong>
            <br />
            Please copy and save this link!
          </Alert>
        )}
        {!shareLink && (
          <Form>
            <Form.Group controlId="permissions">
              <Form.Label>Permissions</Form.Label>
              <Form.Control
                as="select"
                value={shareData.permissions}
                onChange={(e) => setShareData({ ...shareData, permissions: e.target.value })}
              >
                <option value="READ">Read</option>
                <option value="WRITE">Write</option>
                <option value="EXECUTE">Execute</option>
              </Form.Control>
            </Form.Group>
            <Form.Group controlId="accessType" className="mt-3">
              <Form.Label>Access Type</Form.Label>
              <Form.Control
                as="select"
                value={shareData.accessType}
                onChange={(e) => setShareData({ ...shareData, accessType: e.target.value })}
              >
                <option value="PRIVATE">Private</option>
                <option value="PUBLIC">Public</option>
              </Form.Control>
            </Form.Group>
            <Form.Group controlId="expiresAt" className="mt-3">
              <Form.Label>Expiration Date</Form.Label>
              <Form.Control
                type="datetime-local"
                value={shareData.expiresAt}
                onChange={(e) => setShareData({ ...shareData, expiresAt: e.target.value })}
              />
            </Form.Group>
          </Form>
        )}
        {error && <Alert variant="danger">{error}</Alert>}
      </Modal.Body>
      <Modal.Footer>
        <Button variant="secondary" onClick={onHide}>
          Close
        </Button>
        {!shareLink && (
          <Button variant="primary" onClick={handleShareSubmit}>
            Generate Link
          </Button>
        )}
      </Modal.Footer>
    </Modal>
  );
}
