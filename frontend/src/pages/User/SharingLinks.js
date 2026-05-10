import React, { useEffect } from "react";
import { useParams, useNavigate } from "react-router-dom";
import axios from "axios";
import Cookies from "js-cookie";

import API from "../../components/AxiosConfig";

const SharingLinks = () => {
    const { linkId } = useParams();
    const navigate = useNavigate();

    useEffect(() => {
        const fetchFile = async () => {

            const jwt = Cookies.get("JWT");
            if (!jwt) {
                console.error("JWT not found. Redirecting to login.");
                navigate(`/login?redirect=/links/${linkId}`);
                return;
            }

            try {
                const response = await API.get(`/links/${linkId}`, {
                    responseType: "blob",
                });

                // Create a temporary download link
                const url = window.URL.createObjectURL(new Blob([response.data]));
                const link = document.createElement("a");

                // Use content disposition header for filename
                const contentDisposition = response.headers["content-disposition"];
                let filename = "downloaded-file";
                if (contentDisposition) {
                    const match = contentDisposition.match(/filename="?(.+?)"?$/);
                    if (match) filename = match[1];
                }

                link.href = url;
                link.setAttribute("download", filename);
                document.body.appendChild(link);
                link.click();
                link.remove();

                navigate("/home");
            } catch (error) {
                console.error("Error downloading file:", error);
                navigate("/error_links");
            }
        };

        fetchFile();
    }, [linkId, navigate]);

    return (
        <div>
            <p>Processing download... Please wait.</p>
        </div>
    );
};

export default SharingLinks;
