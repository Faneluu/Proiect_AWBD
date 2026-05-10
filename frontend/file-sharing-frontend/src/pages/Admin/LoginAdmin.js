import React, { useState } from "react";
import Cookies from "js-cookie";
import { Container, Row, Col, Form, Button, Alert } from "react-bootstrap";
import { useNavigate } from "react-router-dom";
import API from "../../components/AxiosConfig";
import { jwtDecode } from 'jwt-decode';


function AdminLogin() {
  const [loginData, setLoginData] = useState({ username: "", password: "" });
  const [loginError, setLoginError] = useState("");
  const [isLoading, setIsLoading] = useState(false);
  const navigate = useNavigate();

  const handleLogin = async () => {
    const { username, password } = loginData;

    if (!username || !password) {
      setLoginError("Please fill in all fields.");
      return;
    }

    setIsLoading(true);
    try {
      const response = await API.post("/admin/login", { username, password });

      const token = response.headers["authorization"];
      if (!token) {
        setLoginError("Authorization token not received from server.");
        setIsLoading(false);
        return;
      }

      Cookies.set("JWT", token, { expires: 7, secure: true, sameSite: "Strict" });
      console.log("Received token:", token);
      const decodedToken = jwtDecode(token);
      console.log("Decoded token:", decodedToken);
      setLoginError("");
      console.log("Admin login successful");
      navigate("/home_admin");
    } catch (error) {
      setLoginError(error.response?.data?.message || "Failed to login. Please try again.");
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <Container>
      <Row className="justify-content-center">
        <Col md={6} className="shadow p-4 bg-white rounded">
          <Form>
            <Form.Group controlId="formUsername" className="mb-3">
              <Form.Label>Username</Form.Label>
              <Form.Control
                type="text"
                placeholder="Enter your username"
                value={loginData.username}
                onChange={(e) => setLoginData({ ...loginData, username: e.target.value })}
              />
            </Form.Group>
            <Form.Group controlId="formPassword" className="mb-3">
              <Form.Label>Password</Form.Label>
              <Form.Control
                type="password"
                placeholder="Enter your password"
                value={loginData.password}
                onChange={(e) => setLoginData({ ...loginData, password: e.target.value })}
              />
            </Form.Group>
            {loginError && <Alert variant="danger">{loginError}</Alert>}
            <Button
              variant="primary"
              className="w-100"
              onClick={handleLogin}
              disabled={isLoading}
            >
              {isLoading ? "Logging in..." : "Log in as Admin"}
            </Button>
          </Form>
        </Col>
      </Row>
    </Container>
  );
}

export default AdminLogin;
