import React from "react";
import TestSpec from "./TestSpec";

const TestSuite = ({ suite }) => {
  console.log("suite", suite);
  return (
    <div className="card">
      <div className="card-header">Suite: {suite.title}</div>
      <div className="card-body">
        <p>File: {suite.file}</p>
        <p>
          {suite.specs.length > 0 ? (
            suite.specs.map((spec, index) => (
              <TestSpec key={index} spec={spec} />
            ))
          ) : (
            suite.suites[0] && suite.suites[0].specs.length > 0 ? (
              suite.suites[0].specs.map((spec, index) => (
                <TestSpec key={index} spec={spec} />
              ))
            ) : (
              <p>No TestSpecs found.</p>
            )
          )}
        </p>
      </div>
    </div>
  );
};

export default TestSuite;
