import React from "react";
import { ApplicationContext } from "../context/ApplicationContext";
import TestResultDetail from "./TestResultDetail";
import SpecRuns from "./SpecRuns";
import Filter from "./Filter";


const SpecsResults = () => {

 
  const { setTestSpecs, testSpecs , setTestResult, testResult, notification, setNotification, setSpecRuns, specRuns, setFilter, filter} = React.useContext(ApplicationContext);
  
  React.useEffect(() => {

    if (!testSpecs || testSpecs.length === 0) {
    const url = `/api/testSpecs`;
    fetch(url, { method: 'GET' })
        .then((response) => {
          if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);
          console.log("response testSpecs:" + response.status);
          return response.json();
        })
        .then((data) => applyFilterToSpecs(data))
       // .then((data) => setTestSpecs(data))
        .catch((error) => console.error("Error fetching test results:", error));
    }
  }, [setTestSpecs, testSpecs, setFilter]);

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

 const applyFilterToSpecs = (data) => {
    let filteredSpecs = data;
    if (filter && filter.specs.length > 0) {
      // Create a new array with only the filtered titles
      const filteredTitles = filter.specs.filter(spec => !spec.filtered);

      // Remove the unfiltered titles from the filteredSpecs map
      filteredTitles.forEach(spec => {
        delete filteredSpecs[spec.title];
      });
    }
    setTestSpecs(filteredSpecs);
  
 };

 const openTitleAndClearDetails = (title) => {
  
   setSpecRuns(testSpecs[title]);
   setTestResult(null);
 };

 const handleRefresh = () => {

    const url = `/api/reload`;
    fetch(url, { method: 'GET' })
        .then((response) => {
          if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);
          console.log("response reload:" + response.status);
          return response.json();
        })
        .then(() => {
          setTestSpecs([])
          setTestResult(null);
          setNotification("");
        })
        .catch((error) => console.error("Error reloading tests:", error));
  };

  const hasFailedTest = (testSpec) => {
    return testSpec.some(test => test['status'] !== "passed");
  };

 return (
    <div className="container">
      
       <div className="row">
       <Filter />
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
       <div className="h-5 card-body overflow-auto " style={{ maxHeight: "400px" }}>
       {Object.keys(testSpecs).map((title) => (
         <div key={title} className="card">
          <div role="button" 
            className={`card-header ${hasFailedTest(testSpecs[title]) ? 'bg-danger' : 'bg-info'}`}
            onClick={() => openTitleAndClearDetails(title)}
          >
            {title} ({testSpecs[title].length} tests)
          </div>

        </div>
      ))}
      </div>
      <div className="col-md-15">
        {specRuns && specRuns.length > 0 && <SpecRuns specRuns={specRuns}/>}
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
