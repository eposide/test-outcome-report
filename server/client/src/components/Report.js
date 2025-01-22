import React from "react";

const Report = () => {
  const handleDownloadReport = () => {
    fetch("/api/report", {
      method: "GET",
      headers: {
        "Content-Type": "application/octet-stream",
      },
    })
      .then((response) => {
        if (!response.ok) {
          throw new Error(`HTTP error! status: ${response.status}`);
        }
        return response.blob();
      })
      .then((blob) => {
        const url = window.URL.createObjectURL(new Blob([blob]));
        const link = document.createElement("a");
        link.href = url;
        link.setAttribute("download", "test-results.csv"); // or any other file extension
        document.body.appendChild(link);
        link.click();
      })
      .catch((error) => {
        console.error("Error downloading report:", error);
      });
  };

  return (
    <div>
      <button type="button" className="btn btn-outline-secondary btn-sm" onClick={handleDownloadReport}>
        Download Report
      </button>
    </div>
  );
};

export default Report;