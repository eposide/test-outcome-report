import React from "react";
import TestSuite from "./TestSuite";


const TestFile = ({ result }) => {
  return (
    <div class="card">
      <div class="card-header">Results file {result.filePath}</div>
       <div class="card-body">
        {result.data.suites.map((suite, index) => (
          <TestSuite key={index} suite={suite} />
        ))}
      </div>
    </div>
  );
};

export default TestFile;