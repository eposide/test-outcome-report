import React, { useContext } from "react";
import { ApplicationContext } from "./ApplicationContext";
import TestFile from "./TestFile";

const TestResults = () => {
  const { testResults } = useContext(ApplicationContext);

  console.log("TestResults" + testResults);
  // Display test results here using the TestSuite component
  return (
    <div>
      {Array.isArray(testResults) && testResults.length > 0 ? (
        testResults.map((result, index) => 
        
        <TestFile key={result.id} result={result} />)
      ) : (
        <p>No results available.</p>
      )}
    </div>
  );
};

export default TestResults;
