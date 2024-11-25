import React, { useContext } from "react";
import { ApplicationContext } from "./ApplicationContext";
import TestSuite from "./TestSuite";

const TestResults = () => {
  const { testResults } = useContext(ApplicationContext);

  // Display test results here using the TestSuite component
  return (
    <div>
      <h2>Test Results</h2>
      {testResults.length > 0 ? (
        testResults.map((suite, index) => <TestSuite key={index} suite={suite} />)
      ) : (
        <p>No results available.</p>
      )}
    </div>
  );
};

export default TestResults;
