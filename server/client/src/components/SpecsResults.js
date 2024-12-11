import React from "react";
import { ApplicationContext } from "../context/ApplicationContext";
import TestResultDetail from "./TestResultDetail";
import { Collapse } from "react-bootstrap";
import { v4 as uuidv4 } from 'uuid'; // Import uuid library

const SpecsResults = () => {

  const [openTitle, setOpenTitle] = React.useState(null); //toggle collapse 
  const { setTestSpecs, testSpecs , setTestResult, testResult} = React.useContext(ApplicationContext);
  
  

  React.useEffect(() => {

    const url = `/api/testSpecs`;
    fetch(url, { method: 'GET' })
        .then((response) => {
          if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);
          console.log("response testSpecs:" + response.status);
          return response.json();
        })
        .then((data) => setTestSpecs(data))
        .catch((error) => console.error("Error fetching test results:", error));
    
  }, [setTestSpecs]);



  
 return (
    <div className="container h100">
       {Object.keys(testSpecs).map((title) => (
        <div key={title} className="card">
          <div 
            className="card-header bg-info"
            onClick={() => setOpenTitle(openTitle === title ? null : title)}
            aria-expanded={openTitle === title}
          >
            {title} ({testSpecs[title].length} tests)
          </div>

          <Collapse in={openTitle === title}>
            <div className="card-body">
              <table className="table table-striped">
                <thead>
                  <tr>
                    <th>Date</th>
                    <th>Duration (ms)</th>
                    <th>Status</th>
                    <th>View Results</th>
                  </tr>
                </thead>
                <tbody>
                  {testSpecs[title].map((test, index) => (
                   <tr key={uuidv4()} > 
                      <td>{test.testDate}</td>
                      <td>{test.duration}</td>
                      <td
                        style={{
                          color: test.status === "passed" ? "green" : "red",
                        }}
                      >
                        {test.status}
                      </td>
                      <td>
                      <button type="button" className="btn btn-outline-info" onClick={() => setTestResult(test.testResult)}>Details</button>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </Collapse>
        </div>
      ))}
      {testResult && <TestResultDetail testResult={testResult}  />}
  </div>
  );
};

export default SpecsResults;
