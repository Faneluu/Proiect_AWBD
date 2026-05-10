import React from "react";
import { useNavigate } from "react-router-dom";
import { Button, Container, Row, Col } from "react-bootstrap";

const ErrorLinks = () => {
    const navigate = useNavigate();

    const handleGoBack = () => {
        navigate("/home"); // Redirect back to home or another safe route
    };

    return (
        <Container className="vh-100 d-flex justify-content-center align-items-center">
            <Row>
                <Col className="text-center">
                    <h1 className="text-danger">Error!</h1>
                    <p>There was an issue processing your download link. Please try again or contact support.</p>
                    <Button variant="primary" onClick={handleGoBack}>
                        Go Back to Home
                    </Button>
                </Col>
            </Row>
        </Container>
    );
};

export default ErrorLinks;
