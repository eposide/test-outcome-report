import React from "react";
import { ApplicationContext } from "../context/ApplicationContext";
import TestResultDetail from "./TestResultDetail";
import SpecRuns from "./SpecRuns";
import Filter from "./Filter";
import Loader from "./Loader";
import Report from "./Report";
import TestStatusMeter from "./TestStatusMeter";
import Container from 'react-bootstrap/Container';
import "./SpecResults.css";

const SpecsResults = () => {
 
  const {setTestSpecs, testSpecs , setTestResult, testResult, notification, setNotification, setSpecRuns, specRuns, isLoadingData, setIsLoadingData, setFilter, selectedTitle, setSelectedTitle } = React.useContext(ApplicationContext);
  

  React.useEffect(() => {

    if (!testSpecs || testSpecs.length === 0) {
      setIsLoadingData(true);
      const url = `/api/testSpecs`;
      fetch(url, { method: 'GET' })
        .then((response) => {
          if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);
          console.log("response testSpecs:" + response.status);
          return response.json();
        })
         .then((data) => 
         {
          setTestSpecs(data);
          setIsLoadingData(false); 
         } 
        )         
        .catch((error) => {
          console.error("Error fetching test results:", error);
          setIsLoadingData(false);
        })
      }
  }, [testSpecs, isLoadingData, setIsLoadingData]);

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
  
   setSpecRuns(testSpecs[title]);
   setTestResult(null);
 };

 const handleRefresh = () => {

    setTestSpecs([])
    setTestResult(null);
    setFilter({specs: [], dateFrom: null, dateTo: null});
    setNotification("");
    
  };

  

   //build a list of boolean statuses from a testSpec
    const testStatuses = (testSpec) => {
      let acc = [];

      testSpec.forEach(function (test) {
        acc[test['testDate']] = test['status'] == "passed";
      });

      return acc;
      
   };

 return (
  
    <Container fluid={true} className="p-0">
      
       <div className="row" style={{ width: "100%" }}>
      
       <div className="col-md-5">
       <div className="card">
       <div className="card-header h100">
          <div className="row">         
              <div className="col-md-4">
               <Report />
              </div> 
          </div>
          <div className="row">
              <div className="col-md-4">
                 <button type="button" className="btn btn-outline-secondary btn-sm" onClick={() => handleRefresh()}>Refresh Tests</button>
              </div>
          </div>
          <Filter />
       </div>

     <div className="h-5 card-body overflow-auto " style={{ maxHeight: "400px" }}>
         {isLoadingData ? <Loader /> : (
           Object.keys(testSpecs)
            .map((title) => (
            <div key={title} className="card" style={{ height: "50px", fontSize: "0.8rem" }}>
              <div role="button" 
                className={`card-header ${title === selectedTitle ? 'selected' : ''}`}
              onClick={() => {
                openTitleAndClearDetails(title);
                setSelectedTitle(title);
              }}
              >
            {title} ({testSpecs[title].length} tests)
            <TestStatusMeter testStatuses={testStatuses(testSpecs[title])} />
          </div>
        </div>
      ))
  )}
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
  </Container>
  );
};


export default SpecsResults;
