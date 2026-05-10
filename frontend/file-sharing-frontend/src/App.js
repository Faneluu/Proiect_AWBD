import './App.css';
//Routing
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';

import 'bootstrap/dist/css/bootstrap.min.css';

import React, { useState, useEffect } from 'react';

// Pages
import HomeAdmin from './pages/Admin/HomeAdmin';
import LoginAdmin from './pages/Admin/LoginAdmin';
import HomeUser from './pages/User/HomeUser';
import LoginRegisterUser from './pages/User/LoginRegisterUser';
import UserShared from './pages/User/UserShared';
import UserTrash from './pages/User/UserTrash';
import UserStorage from './pages/User/UserStorage';
import { SearchProvider } from './contexts/SearchContext';
import SharingLinks from './pages/User/SharingLinks';
import ErrorLinks from './pages/User/ErrorLinks';

function App() {
  const [isDarkMode, setIsDarkMode] = useState(true);

  const updateDarkModeFromLocalStorage = () => {
    const storedPreference = localStorage.getItem('isDarkMode');
    setIsDarkMode(storedPreference === 'true');
  };

  useEffect(() => {
    updateDarkModeFromLocalStorage();

    const handleStorageChange = (event) => {
      if (event.key === 'isDarkMode') {
        updateDarkModeFromLocalStorage();
      }
    };

    window.addEventListener('storage', handleStorageChange);

    const handleCustomEvent = () => {
      updateDarkModeFromLocalStorage();
    };
    window.addEventListener('darkModeUpdated', handleCustomEvent);

    return () => {
      window.removeEventListener('storage', handleStorageChange);
      window.removeEventListener('darkModeUpdated', handleCustomEvent);
    };
  }, []);

  return (
    <div className={`bg-dark text-light min-vh-100`}>
      <SearchProvider>
        <Router>
          <Routes>
            <Route path="/" element={<Navigate to="/login" />} />
            <Route path="/login" element={<LoginRegisterUser />} />
            <Route path="/home" element={<HomeUser />} />
            <Route path="/login_admin" element={<LoginAdmin />} />
            <Route path="/home_admin" element={<HomeAdmin />} />
            <Route path="/shared" element={<UserShared />} />
            <Route path="/links/:linkId" element={<SharingLinks />} />
            <Route path="/error_links" element={<ErrorLinks />} />
          </Routes>
        </Router>
      </SearchProvider>
    </div>
  );
}

export default App;
