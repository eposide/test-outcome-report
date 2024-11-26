import React, { useContext, useEffect } from "react";
import { ApplicationContext } from "./components/ApplicationContext";
import TestJob from "./components/TestJob";
import TestResults from "./components/TestResults";


const App = () => {
  const { jobNo, setTestResults, setJobNo, setTestJobs, testJobs } = useContext(ApplicationContext);
  
  useEffect(() => {
    if (jobNo) {
      const url = `${process.env.REACT_APP_API_URL}/api/results/${jobNo}`;
      fetch(url, { method: 'GET' })
        .then((response) => {
          if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);
          return response.json();
        })
        .then((data) => setTestResults(data[0]?.suites || []))
        .catch((error) => console.error("Error fetching test results:", error));
    } else {
      const url = `${process.env.REACT_APP_API_URL}/api/testjobs`;
      fetch(url, { method: 'GET' })
        .then((response) => {
          if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);
          return response.json();
        })
        .then((data) => setTestJobs(data))
        .catch((error) => console.error("Error fetching test jobs:", error));
    }
  }, [jobNo, setTestResults, setTestJobs]);

  useEffect(() => {
    if (testJobs.length > 0 && !jobNo) {
      setJobNo(testJobs[0]); // Assuming each testJob has a unique `id`.
    }
  }, [testJobs, jobNo, setJobNo]);

  
  return (
    <div>
    
    <ul className="nav nav-tabs">
      {Array.isArray(testJobs) && testJobs.length > 0 ? (
         testJobs.map((testJob, index) => <TestJob key={testJob} jobNo={testJob} />)
      ) : (
        <p>Loading...</p>
      )}
    </ul>
    {jobNo ? (
        <div>
        <h1 className="header">Job Number: {jobNo}</h1>
            <TestResults />
        </div>
        ) : (
          <p>No job selected.</p>
        )}
    </div>
  );
};

export default App;
