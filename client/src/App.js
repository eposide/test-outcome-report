import React, { useContext, useEffect } from "react";
import { ApplicationContext } from "./components/ApplicationContext";
import TestResults from "./components/TestResults";
import Button from "react-bootstrap/Button";

const App = () => {
  const { jobNo, setTestResults, setJobNo } = useContext(ApplicationContext);
  
  useEffect(() => {
    if (jobNo) { // Ensure jobNo is set before making the fetch
      const url = `${process.env.REACT_APP_API_URL}/api/results/${jobNo}`;
      console.log("Fetching from URL:", url);

      fetch(url, { method: 'GET' })
        .then((response) => {
          if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
          }
          return response.json();
        })
        .then((data) => setTestResults(data.suites))
        .catch((error) =>
          console.error("Error fetching test results:", error)
        );
    }
  }, [jobNo, setTestResults, setJobNo]); // Add `jobNo` to the dependency array

  return (
  <div>
    <h1 className="header">Job Number: {jobNo}</h1>
    <Button onClick={() => setJobNo(jobNo + 1)}>Increment Job Number</Button>
    <TestResults />
  </div>)
};

export default App;
