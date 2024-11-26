import React, { useContext } from "react";
import { ApplicationContext } from "./ApplicationContext";
import TestSuite from "./TestSuite";

const TestResults = () => {
  const { testResults, jobNo } = useContext(ApplicationContext);

  // Display test results here using the TestSuite component
  return (
    <div>
      <h2>Test Results</h2>
      {Array.isArray(testResults) && testResults.length > 0 ? (
        testResults.map((suite, index) => <TestSuite key={suite.id} suite={suite} />)
      ) : (
        <p>No results available.</p>
      )}
    </div>
  );
};

export default TestResults;
