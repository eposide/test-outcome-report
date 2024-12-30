import React from "react";
import { ApplicationContext } from "../context/ApplicationContext";


const TestResultDetail = () => { 

  const { testResult, testResultDetail, setTestResultDetail } = React.useContext(ApplicationContext);

  React.useEffect(() => {

    console.log("React Test Result Detail testResult " + testResult); 
    if (testResult) {
      const url = `/api/result/${testResult}`;
      fetch(url, { method: 'GET' })
      .then((response) => {
          if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);
          return response.json();
        })
        .then((data) => setTestResultDetail(data))
        .catch((error) => console.error("Error fetching test result detail:", error));
    }
  }, [testResult, setTestResultDetail]);


  return (
    <div className="card"  >
    <div className="card-header h100">Details</div>
      <div className="card-body overflow-auto" >
        <pre style={{ fontSize: "10px", maxHeight: "800px" }}><code>{JSON.stringify(testResultDetail, null, 2)}</code></pre>
      </div>
    </div>
  );
};

export default TestResultDetail;
