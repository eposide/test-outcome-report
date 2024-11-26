import React from "react";
import TestSpec from "./TestSpec";

const TestSuite = ({ suite }) => {
  return (
    <div>
      <h3>Suite: {suite.title}</h3>
      <p>File: {suite.file}</p>
      <ul>
        {suite.specs.map((spec, index) => (
          <TestSpec key={index} spec={spec} />
        ))}

        
      </ul>
    </div>
  );
};

export default TestSuite;
