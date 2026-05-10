import axios from "axios";
import Cookies from "js-cookie";


const isInK8sCluster = () => {
  try {
    return window && window.location.hostname !== "localhost";
  } catch (e) {
    return false;
  }
};

const API = axios.create({
  baseURL: isInK8sCluster() ? "https://atmfilesharing-api.apps.fsisc.ro/api/v1" : "http://localhost:8080/api/v1",
});

API.interceptors.request.use(
  (config) => {
    const token = Cookies.get("JWT");
    // console.log(token);
    
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

export default API;
