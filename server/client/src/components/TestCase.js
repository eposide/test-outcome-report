import React from "react";

const TestCase = ({ test }) => {
  return (
    <div class="card">
      <div class="card-header">Test: {test.projectName}</div>
      <div class="card-body">
      <p
        style={{
          color: test.results[0].status === "passed" ? "green" : "red",
        }}
      >
        Status: {test.results[0].status}
      </p>
      <p>Duration: {test.results[0].duration}ms</p>

      </div>
    </div>
  );
};

export default TestCase;
