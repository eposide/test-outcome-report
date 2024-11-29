import React from "react";

const TestCase = ({ test }) => {
  return (
    <li>
      <p>Test: {test.projectName}</p>
      <p
        style={{
          color: test.results[0].status === "passed" ? "green" : "red",
        }}
      >
        Status: {test.results[0].status}
      </p>
      <p>Duration: {test.results[0].duration}ms</p>
    </li>
  );
};

export default TestCase;
