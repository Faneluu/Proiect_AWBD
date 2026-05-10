import React, { useState } from 'react';
import { Modal, Button, Form } from 'react-bootstrap';

const AddUserModal = ({ show, handleClose, newUser, setNewUser, handleAddUser }) => {
    const [error, setError] = useState('');

    const handleInputChange = (e) => {
        const { name, value } = e.target;
        setNewUser((prevUser) => ({
            ...prevUser,
            [name]: value,
        }));
        setError(''); // Reset the error message when user types
    };

    const validateEmail = (email) => {
        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        return emailRegex.test(email);
    };

    const validateForm = () => {
        const { name, email, password } = newUser;
        if (!name || !email || !password) {
            setError('Toate câmpurile sunt obligatorii!');
            return false;
        }
        if (!validateEmail(email)) {
            setError('Adresa de email nu este validă!');
            return false;
        }
        return true;
    };

    const handleAddUserClick = () => {
        if (validateForm()) {
            handleAddUser();
        }
    };

    return (
        <Modal show={show} onHide={handleClose}>
            <Modal.Header closeButton>
                <Modal.Title>Adaugă Utilizator</Modal.Title>
            </Modal.Header>
            <Modal.Body>
                <Form>
                    <Form.Group controlId="formName" className="mb-3">
                        <Form.Label>Username</Form.Label>
                        <Form.Control
                            type="text"
                            name="name"
                            value={newUser.name}
                            onChange={handleInputChange}
                            placeholder="Enter username"
                        />
                    </Form.Group>
                    <Form.Group controlId="formEmail" className="mb-3">
                        <Form.Label>Email</Form.Label>
                        <Form.Control
                            type="email"
                            name="email"
                            value={newUser.email}
                            onChange={handleInputChange}
                            placeholder="Enter email"
                        />
                    </Form.Group>
                    <Form.Group controlId="formPassword" className="mb-3">
                        <Form.Label>Password</Form.Label>
                        <Form.Control
                            type="password"
                            name="password"
                            value={newUser.password}
                            onChange={handleInputChange}
                            placeholder="Create a password"
                        />
                    </Form.Group>
                    {error && <p className="text-danger">{error}</p>}
                </Form>
            </Modal.Body>
            <Modal.Footer>
                <Button variant="secondary" onClick={handleClose}>
                    Închide
                </Button>
                <Button
                    variant="primary"
                    onClick={handleAddUserClick}
                    disabled={!newUser.name || !newUser.email || !newUser.password}
                >
                    Adaugă Utilizator
                </Button>
            </Modal.Footer>
        </Modal>
    );
};

export default AddUserModal;
