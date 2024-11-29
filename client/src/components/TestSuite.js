import React from "react";
import TestSpec from "./TestSpec";

const TestSuite = ({ suite }) => {
  return (
    <div>
      <h5 className="alert alert-secondary">Suite: {suite.title}</h5>
      <p >File: {suite.file}</p>
      <ul>
        {suite.specs.map((spec, index) => (
          <TestSpec key={index} spec={spec} />
        ))}

        
      </ul>
    </div>
  );
};

export default TestSuite;
