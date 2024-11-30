import React from "react";
import TestSpec from "./TestSpec";

const TestSuite = ({ suite }) => {
  return (
    <div class="card">
      <div class="card-header">Suite: {suite.title}</div>
      <div class="card-body">  
        <p >File: {suite.file}</p>
        <p>
        {suite.specs.map((spec, index) => (
          <TestSpec key={index} spec={spec} />
        ))}
      </p>
      </div>
    </div>
  );
};

export default TestSuite;
