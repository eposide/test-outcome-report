import React from "react";
import TestCase from "./TestCase";

const TestSpec = ({ spec }) => {
  return (
    <li>
      <h4>Spec: {spec.title}</h4>
      <ul>
        {spec.tests.map((test, index) => (
          <TestCase key={index} test={test} />
        ))}
      </ul>
    </li>
  );
};

export default TestSpec;
