import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import Cookies from "js-cookie";
import Navbar from "../../components/Navbar";
import SideBar from "../../components/SideBar";
import DisplayContainer from "../../components/DisplayContainer";
import "../../css/HomeUser.css";
import { SearchProvider } from "../../contexts/SearchContext";

function HomeUser() {
  const navigate = useNavigate();
  const [currentPath, setCurrentPath] = useState(() => {
      const savedPath = localStorage.getItem("currentPath");
      return savedPath ? JSON.parse(savedPath) : { id: null, path: "/" };
    });

  useEffect(() => {
    const jwt = Cookies.get("JWT");
    if (!jwt) {
      navigate("/login");
    }
  }, [navigate]);


  return (
    <>
      <Navbar />
      <div id="mainCont">
        <SideBar currentPath={currentPath} />
        <DisplayContainer currentPath={currentPath} setCurrentPath={setCurrentPath} />
      </div>
    </>
  );
}

export default HomeUser;
