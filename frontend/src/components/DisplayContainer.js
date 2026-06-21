import React, { useEffect, useState, useContext } from "react";
import Breadcrumb from "react-bootstrap/Breadcrumb";
import ListGroup from "react-bootstrap/ListGroup";
import Pagination from "react-bootstrap/Pagination";
import Button from "react-bootstrap/Button"; 
import "../css/DisplayContainer.css"; 
import ListItem from "./ListItem";
import API from "../components/AxiosConfig";
import { SearchContext } from "../contexts/SearchContext";

export default function DisplayContainer({ currentPath, setCurrentPath }) {
  const [folders, setFolders] = useState([]);
  const [files, setFiles] = useState([]);
  const { searchResults } = useContext(SearchContext);
  const [currentPage, setCurrentPage] = useState(1);

  const [sortOrder, setSortOrder] = useState("asc");
  const [sortBy, setSortBy] = useState("name");

  const itemsPerPage = 10;

  useEffect(() => {
    if (searchResults.length > 0) {
      setFolders([]);
      setFiles(searchResults);
    } else {
      fetchData();
    }
    localStorage.setItem("currentPath", JSON.stringify(currentPath));
    setCurrentPage(1);
  }, [currentPath, searchResults]);

  const fetchData = async () => {
    try {
      const folderResponse =
        currentPath.id === null
          ? await API.get("/folders/")
          : await API.get(`/folders/contents`, { params: { id: currentPath.id } });

      const fileResponse =
        currentPath.id === null
          ? await API.get("/files/")
          : await API.get(`/files/${currentPath.id}/files`);

      setFolders(folderResponse.data);
      setFiles(fileResponse.data);
    } catch (error) {
      console.error("Error fetching data:", error);
    }
  };

  const navigateTo = (folder) => {
    const newPath = `${currentPath.path === "/" ? "" : currentPath.path}/${folder.name}`;
    setCurrentPath({ id: folder.id, path: newPath });
  };

  const handleBreadcrumbClick = async (index) => {
    const pathParts = currentPath.path.split("/").filter((folder) => folder);
    const newPath = "/" + pathParts.slice(0, index + 1).join("/");
  
    try {
      // Fetch the target folder ID by querying the API
      const folderName = pathParts[index];
      const folderResponse = await API.get("/folders/all"); // Adjust the endpoint as necessary
      const targetFolder = folderResponse.data.find((folder) => folder.name === folderName);
  
      if (targetFolder) {
        setCurrentPath({ id: targetFolder.id, path: newPath });
      } else {
        setCurrentPath({ id: null, path: newPath });
      }
    } catch (error) {
      console.error("Error resolving breadcrumb path:", error);
    }
  };
  

  const handleDelete = async (item) => {
    try {
      if (item.type === "folder") {
        await API.delete(`/folders/${item.id}`);
      } else {
        const endpoint =
          currentPath.path === "/"
            ? `/files/${item.name}`
            : `/files/${currentPath.id}/${item.name}`;
        await API.delete(endpoint);
      }
      fetchData();
    } catch (error) {
      console.error("Error deleting item:", error);
    }
  };

  const handleDownload = async (item) => {
    try {
      const endpoint =
        item.type === "folder"
          ? `/folders/files`
          : currentPath.path === "/"
          ? `/files/${item.name}`
          : `/files/${currentPath.id}/${item.name}`;
      const response = await API.get(endpoint, { responseType: "blob" });

      const url = window.URL.createObjectURL(new Blob([response.data]));
      const link = document.createElement("a");
      link.href = url;
      link.setAttribute("download", item.name);
      document.body.appendChild(link);
      link.click();
      link.remove();
    } catch (error) {
      console.error("Error downloading item:", error);
    }
  };

  const combinedItems = [
    ...folders.map((folder) => ({ ...folder, type: "folder" })),
    ...files.map((file) => ({ ...file, type: "file" })),
  ];

  const sortedItems = [...combinedItems].sort((a, b) => {
    let compareValue = 0;
    if (sortBy === "name") {
      compareValue = a.name.localeCompare(b.name);
    } else if (sortBy === "size") {
      compareValue = a.fileSize - b.fileSize;
    } else if (sortBy === "type") {
      compareValue = a.type.localeCompare(b.type);
    }

    return sortOrder === "asc" ? compareValue : -compareValue;
  });

  const totalPages = Math.ceil(sortedItems.length / itemsPerPage);
  const currentItems = sortedItems.slice(
    (currentPage - 1) * itemsPerPage,
    currentPage * itemsPerPage
  );

  const handlePageChange = (pageNumber) => {
    setCurrentPage(pageNumber);
  };

  const handleSort = (sortBy) => {
    setSortBy(sortBy);
    setSortOrder((prevSortOrder) => (prevSortOrder === "asc" ? "desc" : "asc"));
  };

  return (
    <div id="displayCont">
      {/* Breadcrumb Navigation - afisat doar in interiorul unui folder */}
      {currentPath.path.split("/").filter((folder) => folder).length > 0 && (
        <Breadcrumb className="custom-breadcrumb">
          {currentPath.path
            .split("/")
            .filter((folder) => folder)
            .map((folder, index) => (
              <Breadcrumb.Item key={index} onClick={() => handleBreadcrumbClick(index)}>
                {folder}
              </Breadcrumb.Item>
            ))}
        </Breadcrumb>
      )}

      {/* Sorting Controls */}
      <div className="sorting-controls">
        <Button
          variant="outline-primary"
          size="sm"
          onClick={() => handleSort("name")}
        >
          Sort by Name
        </Button>
        <Button
          variant="outline-primary"
          size="sm"
          onClick={() => handleSort("size")}
        >
          Sort by Size
        </Button>
        <Button
          variant="outline-primary"
          size="sm"
          onClick={() => handleSort("type")}
        >
          Sort by Type
        </Button>
      </div>

      {/* File/Folder List */}
      <ListGroup as="div" className="list-content">
        {currentItems.map((item, index) => (
          <ListItem
            key={`${item.type}-${index}`}
            item={item}
            type={item.type}
            onNavigate={item.type === "folder" ? navigateTo : undefined}
            onAction={(item) => console.log("Right-clicked on:", item)}
            onDownload={handleDownload}
            onDelete={handleDelete}
            fetchData={fetchData}
          />
        ))}
      </ListGroup>

      {/* Pagination */}
      {combinedItems.length > 0 && (
        <div className="pagination-container">
          <Pagination>
            {[...Array(totalPages)].map((_, pageIndex) => (
              <Pagination.Item
                key={pageIndex + 1}
                active={pageIndex + 1 === currentPage}
                onClick={() => handlePageChange(pageIndex + 1)}
              >
                {pageIndex + 1}
              </Pagination.Item>
            ))}
          </Pagination>
        </div>
      )}
    </div>
  );
}
