import React, { useState } from 'react';
import { IoSearch } from "react-icons/io5";
import { Line } from 'react-chartjs-2';
import { Chart as Chartjs, Title, Tooltip, LineElement, Legend, CategoryScale, LinearScale, PointElement } from 'chart.js';
import { CircularProgressbar } from 'react-circular-progressbar';
import { Card, Dropdown, Button } from 'react-bootstrap';
import 'react-circular-progressbar/dist/styles.css';
import logo from '../../assets/images/logo.png';
import '../../css/HomeAdmin.css';
import 'bootstrap/dist/css/bootstrap.min.css';
import AddUserModal from '../../components/AddUserModal';
import ConfirmDeleteModal from '../../components/ConfirmDeleteModal';
import EditUserModal from '../../components/EditUserModal';
import  { useEffect } from 'react';
import API from "../../components/AxiosConfig";
import Cookies from 'js-cookie';
import { jwtDecode } from 'jwt-decode';

Chartjs.register(
    Title, Tooltip, LineElement, Legend, CategoryScale, LinearScale, PointElement
);

const HomeAdmin = () => {
    const [searchItem, setSearchTerm] = useState('');
    const [showModal, setShowModal] = useState(false);
    const [newUser, setNewUser] = useState({ name: '', email: '', password: '' });

    const [showDeleteModal, setShowDeleteModal] = useState(false);
    const [selectedUser, setSelectedUser] = useState(null);

    const [showEditModal, setShowEditModal] = useState(false);
    const [currentUser, setCurrentUser] = useState(null);

    const [users, setUsers] = useState([]);

    const handleShowEditModal = (user) => {
        setCurrentUser(user);
        setShowEditModal(true);
    };
    
    const handleUpdateUser = async (updatedUser) => {
        try {
            await API.post('/api/v1/admin/allocate-space', null, {
                params: {
                    username: updatedUser.username, 
                    space: updatedUser.space.value,
                },
                headers: {
                    Authorization: `Bearer ${token}`,
                },
            });
            setUsers(users.map((user) => (user.id === updatedUser.id ? updatedUser : user)));
            setShowEditModal(false);
        } catch (error) {
            console.error("Error updating user space:", error);
        }
    };
    
    

    const handleShowDeleteModal = (user) => {
        setSelectedUser(user);
        setShowDeleteModal(true);
    };

    const handleDeleteUser = async () => {
        if (!selectedUser || !selectedUser.username) {
            console.error("No user selected for deletion.");
            return;
        }
        try {
            await API.delete(`/api/v1/admin/users/${selectedUser.username}`, {
                headers: {
                    Authorization: `Bearer ${token}`,
                },
            });
            setUsers(users.filter((user) => user.id !== selectedUser.id));
            setShowDeleteModal(false);
        } catch (error) {
            console.error("Error deleting user:", error);
        }
    };
    
    


    const handleChange = (e) => {
        setSearchTerm(e.target.value);
    };

    const handleDownload = (filename) => {
        const element = document.createElement("a");
        element.href = `/path/to/files/${filename}`;
        element.download = filename;
        document.body.appendChild(element);
        element.click();
        document.body.removeChild(element);
    };

    const data = {
        labels: ['13.11.2024', '14.11.2024', '15.11.2024', '16.11.2024'],
        datasets: [
            {
                data: [10, 45, 20, 30],
                borderColor: 'white',
                backgroundColor: 'black',
            },
        ],
    };

    const options = {
        responsive: true,
        scales: {
            x: {
                title: {
                    display: true,
                    text: 'Days',
                    align: 'end',
                    color: 'white',
                },
                ticks: {
                    padding: 10,
                    color: 'white',
                },
                offset: true,
                grid: {
                    color: 'grey',
                    borderColor: 'white',
                    borderWidth: 2,
                    lineWidth: 2,
                    drawOnChartArea: true,
                    drawTicks: true,
                },
            },
            y: {
                title: {
                    display: true,
                    text: 'GB',
                    position: 'top',
                    align: 'end',
                    color: 'white',
                },
                ticks: {
                    padding: 10,
                    stepSize: 10,
                    color: 'white',
                },
                offset: true,
                grid: {
                    color: 'grey',
                    borderColor: 'white',
                    borderWidth: 2,
                    lineWidth: 2,
                    drawOnChartArea: true,
                    drawTicks: true,
                },
            },
        },
        plugins: {
            legend: {
                display: false,
            },
            title: {
                display: true,
                text: 'Used space',
                position: 'bottom',
                color: 'white',
            },
        },
    };

    

    const token = Cookies.get("JWT");

    const fetchUsers = async () => {
    try {
        const response = await API.get("/api/v1/admin/users", {
        headers: {
            Authorization: `Bearer ${token}`, 
        },
        });
        console.log(response.data); 
    } catch (error) {
        console.error("Error fetching users:", error);
    }
    };
        
    useEffect(() => {
        fetchUsers();
    }, []);

 

    const filteredUsers = users.filter(user =>
        user.name.toLowerCase().includes(searchItem.toLowerCase()) ||
        user.email.toLowerCase().includes(searchItem.toLowerCase())
    );
    const handleAddUser = async () => {
        try {
            const response = await API.post('/api/v1/admin/', newUser, {
                headers: {
                    Authorization: `Bearer ${token}`,
                },
            });
            setUsers([...users, response.data]); 
            setShowModal(false);
            setNewUser({ name: '', email: '', password: '' });
        } catch (error) {
            console.error("Error adding user:", error);
        }
    };
    
    const handleCloseModal = () => setShowModal(false);
    const handleShowModal = () => setShowModal(true);

    return (
        <div>
            <header className="header">
                <div className="logo-container">
                    <img src={logo} alt="Logo" className="logo-image" />
                    <p className="animated-text" style={{ color: "black" }}>ATM File Sharing</p>
                </div>
                <div className="admin-account">ADMIN ACCOUNT</div>
                
            </header>

            <div className="content">
                <div className="charts">
                    <Card className="chart-card">
                        <Card.Body>
                            <Line data={data} options={options} className="used-space-chart" />
                        </Card.Body>
                    </Card>
                </div>

                <div className="progress-container">
                    <Card  className="progress-card">
                        <Card.Body>
                            <div className="progress-bars">
                                <CircularProgressbar value={20} text={`${20}%`} className="progress-bar" />
                                <CircularProgressbar value={90} text={`${90}%`} className="progress-bar" />
                                <CircularProgressbar value={60} text={`${60}%`} className="progress-bar" />
                            </div>
                            <div className="bar-info">
                                Buckets
                            </div>
                        </Card.Body>
                    </Card>

                    <Card className="table-card">
                        <Card.Body>
                            <table className="data-table">
                                <tbody>
                                    <tr>
                                        <td>1</td>
                                        <td>Back-up bucket_1</td>
                                        <td className="download1" onClick={() => handleDownload('backup1.zip')}>10.11.2024</td>
                                    </tr>
                                    <tr>
                                        <td>2</td>
                                        <td>Back-up bucket_2</td>
                                        <td className="download2" onClick={() => handleDownload('backup2.zip')}>1.1.2024</td>
                                    </tr>
                                    <tr>
                                        <td>3</td>
                                        <td>Back-up bucket_3</td>
                                        <td className="download3" onClick={() => handleDownload('backup3.zip')}>2.08.2024</td>
                                    </tr>
                                </tbody>
                            </table>
                        </Card.Body>
                    </Card>
                    
                </div>
            </div>
                <div className="container">
                    <Card className="table-card">
                        <Card.Body>
                            <h5 style={{ color: '#b0b0b0' }}>User Table</h5>
                            <div className="d-flex justify-content-between align-items-center mb-3">
                                <div className="search-container">
                                    <IoSearch className="search-icon" />
                                    <input
                                        className="search-bar"
                                        onChange={handleChange}
                                        type="search"
                                        placeholder="Search..."
                                        value={searchItem}
                                    />
                                </div>
                                <Button variant="primary" onClick={handleShowModal}>
                                    Add User
                                </Button>
                                <AddUserModal
                                    show={showModal}
                                    handleClose={handleCloseModal}
                                    newUser={newUser}
                                    setNewUser={setNewUser}
                                    handleAddUser={handleAddUser}
                                />
                            </div>
                            <div className="table-scroll-container">
                                <table className="table table-hover table-bordered">
                                    <thead className="table-light">
                                        <tr>
                                            <th>User</th>
                                            <th>Email</th>
                                            <th>Space</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        {(searchItem === '' ? users : filteredUsers).map((user, index) => (
                                            <tr key={index}>
                                                <td>
                                                    <strong>{user.username}</strong>
                                                    <br />
                                                </td>
                                                <td>{user.email}</td>
                                                <td>
                                                    <div className="d-flex align-items-center">
                                                    <div className="me-2">
                                                        {user.total_space !== 0 ? ((user.used_space / user.total_space) * 100).toFixed(2) : 0}%
                                                    </div>
                                                        <div className="progress w-100" style={{ height: '5px' }}>
                                                            <div
                                                                className="progress-bar bg-success"
                                                                role="progressbar"
                                                                style={{
                                                                    width: `${(user.used_space / user.total_space) * 100}%`,
                                                                }}
                                                                aria-valuenow={user.total_space !== 0 ? (user.used_space / user.total_space) * 100 : 0}
                                                                aria-valuemin="0"
                                                                aria-valuemax="100"
                                                            ></div>
                                                        </div>
                                                    </div>
                                                </td>
                                                <td style={{ width: '50px', padding: '0' }}>
                                                    <Dropdown>
                                                        <Dropdown.Toggle variant="link" id="dropdown-basic">
                                                            &#8226;&#8226;&#8226; {/* 3 vertical dots */}
                                                        </Dropdown.Toggle>

                                                        <Dropdown.Menu>
                                                            <Dropdown.Item onClick={() => handleShowEditModal(user)}>
                                                                Edit
                                                            </Dropdown.Item>
                                                            <EditUserModal
                                                                show={showEditModal}
                                                                handleClose={() => setShowEditModal(false)}
                                                                user={currentUser}
                                                                handleUpdateUser={handleUpdateUser}
                                                            />
                                                            <Dropdown.Item onClick={() => handleShowDeleteModal(user)}>
                                                                Delete
                                                            </Dropdown.Item>
                                                            <ConfirmDeleteModal
                                                                show={showDeleteModal}
                                                                handleClose={() => setShowDeleteModal(false)}
                                                                handleConfirm={handleDeleteUser}
                                                                userName={selectedUser?.name}
                                                            />
                                                        </Dropdown.Menu>
                                                    </Dropdown>
                                                </td>
                                            </tr>
                                        ))}
                                    </tbody>
                                </table>
                            </div>
                        </Card.Body>
                    </Card>
                </div>
            </div>
    );
    
};

export default HomeAdmin;
