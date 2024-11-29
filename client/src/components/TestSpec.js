import React from "react";
import TestCase from "./TestCase";

const TestSpec = ({ spec }) => {
  return (
    <li>
      <h6 className="alert alert-secondary" style={{ backgroundColor: 'grey'}}>Spec: {spec.title}</h6>
      <ul>
        {spec.tests.map((test, index) => (
          <TestCase key={index} test={test} />
        ))}
      </ul>
    </li>
  );
};

export default TestSpec;
