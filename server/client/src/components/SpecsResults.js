import React from "react";
import { ApplicationContext } from "../context/ApplicationContext";
import TestResultDetail from "./TestResultDetail";
import { Collapse } from "react-bootstrap";
import { v4 as uuidv4 } from 'uuid'; // Import uuid library

const SpecsResults = () => {

  const [openTitle, setOpenTitle] = React.useState(null); //toggle collapse 
  const { setTestSpecs, testSpecs , setTestResult, testResult, notification, setNotification} = React.useContext(ApplicationContext);
  
  

  React.useEffect(() => {

    if (!testSpecs || testSpecs.length === 0) {
    const url = `/api/testSpecs`;
    fetch(url, { method: 'GET' })
        .then((response) => {
          if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);
          console.log("response testSpecs:" + response.status);
          return response.json();
        })
        .then((data) => setTestSpecs(data))
        .catch((error) => console.error("Error fetching test results:", error));
    }
  }, [setTestSpecs, testSpecs]);

  React.useEffect(() => { 

    const eventSource = new EventSource("/events");

    eventSource.onmessage = (event) => {
      console.log('received event ', event);
      const data = JSON.parse(event.data);
      setNotification(data.message);
    };

    eventSource.onerror = (event) => {
      console.error(`EventSource error: ${event.message}`);
    }

    return () => {
      console.log('EventSource being closed');
      eventSource.close();
    }
  }, [setNotification, notification]);

 const openTitleAndClearDetails = (title) => {
   setOpenTitle(openTitle === title? null : title);
   setTestResult(null);
 };

 const handleRefresh = () => {
   setTestSpecs([]);
   setTestResult(null);
   setNotification("");
  };

 return (
    <div className="container">
       <div className="row">
       <div className="col-md-5">
       <div className="card">
       <div className="card-header h100">
          <div className="row">
              <div className="col-md-4">
                 <button type="button" className="btn btn-outline-secondary btn-sm" onClick={() => handleRefresh()}>Refresh Tests</button>
              </div>
              <div className="col">
               <p className="card-text">{notification}</p>
              </div> 
          </div>
       </div>
       <div className="card-body overflow-auto">
       {Object.keys(testSpecs).map((title) => (
         <div key={title} className="card">
          <div role="button" 
            className="card-header bg-info"
            onClick={() => openTitleAndClearDetails(title)}
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
      </div>
    </div>
    </div>
      <div className="col-md-7">
      {testResult && <TestResultDetail testResult={testResult}  />}
      </div>
  </div>
  </div>
  );
};


export default SpecsResults;
