import React, { useContext, useEffect } from "react";
import { ApplicationContext } from "./components/ApplicationContext";
import TestJob from "./components/TestJob";
import TestResults from "./components/TestResults";
import Button from "react-bootstrap/Button";


const App = () => {
  const { jobNo, setTestResults, setJobNo, setTestJobs, testJobs } = useContext(ApplicationContext);
  
  useEffect(() => {
    if (jobNo) {
      const url = `/api/results/${jobNo}`;
      fetch(url, { method: 'GET' })
        .then((response) => {
          if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);
          console.log("response job no :" + response.status);
          return response.json();
        })
        .then((data) => setTestResults(data))
        .catch((error) => console.error("Error fetching test results:", error));
    } else {
      const url = `/api/testjobs`;
      fetch(url, { method: 'GET' })
        .then((response) => {
          if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);
          console.log("response jobs :" + response.status);
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
    
    <ul className="nav nav-tabs" style={{ color: 'grey'}}>
      <li>
          <Button variant="outline-secondary" >Select a test job </Button>
        </li>
      {Array.isArray(testJobs) && testJobs.length > 0 ? (
         testJobs.map((testJob, index) => <TestJob key={testJob} jobNo={testJob} />)
      ) : (
        <p>Loading...</p>
      )}
    </ul>
    {jobNo ? (
        <div>
            <TestResults />
        </div>
        ) : (
          <p>No job selected.</p>
        )}
    </div>
  );
};

export default App;
