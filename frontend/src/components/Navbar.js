import React, { useEffect, useState, useContext } from "react";
import logo from "../assets/images/logo.png";
import search from "../assets/images/search.png";
import profile_icon from "../assets/images/profile_icon.png";
import settings from "../assets/images/settings.png";
import "../css/Navbar.css";
import "../css/AnimatedText.css";
import "../css/DarkModeButton.css";
import "bootstrap/dist/css/bootstrap.min.css";
import { Dropdown } from "react-bootstrap";
import Cookies from "js-cookie";
import { useNavigate } from "react-router-dom";
import API from "./AxiosConfig";
import { SearchContext } from "../contexts/SearchContext";

export default function Navbar() {
  const [isDarkMode, setIsDarkMode] = useState(false);
  const [searchQuery, setSearchQuery] = useState("");
  const { setSearchResults } = useContext(SearchContext); // Use context
  const navigate = useNavigate();

  useEffect(() => {
    const savedMode = localStorage.getItem("isDarkMode");

    if (savedMode === "false") {
      setIsDarkMode(false);
      document.body.classList.add("light-mode");
      document.body.classList.remove("dark-mode");
    } else {
      setIsDarkMode(true);
      document.body.classList.add("dark-mode");
      document.body.classList.remove("light-mode");
    }
  }, []);



  const handleLogout = () => {
    Cookies.remove("JWT");
    navigate("/login");
    console.log("User logged out");
    alert("Logged out!");
  };

  const handleSearch = async (e) => {
    e.preventDefault();
    try {
      const response = await API.get("/files/search", {
        params: { query: searchQuery },
      });
      setSearchResults(response.data);
    } catch (error) {
      console.error("Error searching files:", error);
    }
  };

  return (
    <nav>
      <ul>
        <li>
          <a href="/home">
            <div id="icon">
              <img src={logo} alt="File Sharing Logo" />
              <p className="animated-text">File Sharing</p>
            </div>
          </a>
        </li>

        <li>
          <div id="searchBar">
            <button type="submit">
                <img src={search} alt="Search" className="opacity" onClick={handleSearch} />
            </button>
            <form onSubmit={handleSearch}>         
              <input
                type="text"
                placeholder="Search"
                value={searchQuery}
                onChange={(e) => setSearchQuery(e.target.value)}
              />
            </form>
          </div>
        </li>

        <li>
          <div id="navOptions">

            {/* Profile Dropdown */}
            <Dropdown align="end">
              <Dropdown.Toggle variant="secondary" id="dropdown-basic">
                <img src={profile_icon} alt="Profile Icon" className="opacity" />
              </Dropdown.Toggle>

              <Dropdown.Menu>
                <Dropdown.Item onClick={handleLogout}>Logout</Dropdown.Item>
              </Dropdown.Menu>
            </Dropdown>

            <button>
              <img src={settings} alt="Settings" className="opacity" />
            </button>
          </div>
        </li>
      </ul>
    </nav>
  );
}
