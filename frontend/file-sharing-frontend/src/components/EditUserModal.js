import React, { useState, useEffect } from 'react';
import { Modal, Form } from 'react-bootstrap';

const EditUserModal = ({ show, handleClose, user, handleUpdateUser }) => {
    const [localSpace, setLocalSpace] = useState(user?.space.value || 0);

    useEffect(() => {
        // Sincronizează valoarea locală cu prop-ul user când se schimbă
        if (user?.space.value !== undefined) {
            setLocalSpace(user.space.value);
        }
    }, [user]);

    const handleSpaceChange = (e) => {
        const updatedSpace = parseInt(e.target.value, 10);
        if (!isNaN(updatedSpace)) {
            setLocalSpace(updatedSpace); // Actualizează starea locală
            handleUpdateUser({ ...user, space: { value: updatedSpace } });
        } else {
            setLocalSpace(''); // Permite ștergerea temporară a valorii
        }
    };

    return (
        <Modal show={show} onHide={handleClose}>
            <Modal.Header closeButton>
                <Modal.Title>Edit User</Modal.Title>
            </Modal.Header>
            <Modal.Body>
                <p>
                    <strong>Name:</strong> {user?.name}
                </p>
                <p>
                    <strong>Email:</strong> {user?.email}
                </p>
                <p>
                    <strong>Space Used:</strong> {user?.usage.value} GB
                </p>
                <Form.Group controlId="formNewSpace">
                    <Form.Label>Total Space (GB)</Form.Label>
                    <Form.Control
                        type="number"
                        value={localSpace}
                        onChange={handleSpaceChange}
                    />
                </Form.Group>
            </Modal.Body>
        </Modal>
    );
};

export default EditUserModal;
