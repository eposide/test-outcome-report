import React, { useContext, useEffect, useState } from "react";
import { ApplicationContext } from "./ApplicationContext";
import { Collapse } from "react-bootstrap";

const SpecsResults = () => {

  const [openTitle, setOpenTitle] = useState(null); //toggle collapse 
  const { setTestSpecs, testSpecs } = useContext(ApplicationContext);
  
  useEffect(() => {

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
    <div className="container mt-4">
       {Object.keys(testSpecs).map((title) => (
        <div key={title} className="mb-3">
          <button
            className="btn btn-primary"
            onClick={() => setOpenTitle(openTitle === title ? null : title)}
            aria-expanded={openTitle === title}
          >
            {title} ({testSpecs[title].length} tests)
          </button>

          <Collapse in={openTitle === title}>
            <div className="mt-2">
              <table className="table table-striped">
                <thead>
                  <tr>
                    <th>Date</th>
                    <th>Duration (ms)</th>
                    <th>Status</th>
                  </tr>
                </thead>
                <tbody>
                  {testSpecs[title].map((test, index) => (
                    <tr key={index}>
                      <td>{test.testDate}</td>
                      <td>{test.duration}</td>
                      <td
                        style={{
                          color: test.status === "passed" ? "green" : "red",
                        }}
                      >
                        {test.status}
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </Collapse>
        </div>
      ))}
    </div>
  );
};

export default SpecsResults;
