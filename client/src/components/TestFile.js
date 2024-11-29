import React from "react";
import TestSuite from "./TestSuite";


const TestFile = ({ result }) => {
  return (
    <li>
      <h4 className="alert alert-secondary" style={{ backgroundColor: 'grey'}}>Results file {result.filePath}</h4>
      <ul>
        {result.data.suites.map((suite, index) => (
          <TestSuite key={index} suite={suite} />
        ))}
      </ul>
    </li>
  );
};

export default TestFile;