import React from "react";
import TestCase from "./TestCase";

const TestSpec = ({ spec }) => {
  return (
    <div class="card">
      <div class="card-header" style={{ backgroundColor: 'grey'}}>Spec: {spec.title}</div>
      <div class="card-body">
        {spec.tests.map((test, index) => (
          <TestCase key={index} test={test} />
        ))}
      </div>
    </div>
  );
};

export default TestSpec;
