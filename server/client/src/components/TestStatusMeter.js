import React from "react";
import ProgressBar from "react-bootstrap/ProgressBar";

const TestStatusMeter = ({ testStatuses }) => {
  const totalTests = Object.values(testStatuses).length;
  const failedTests = Object.values(testStatuses).reduce((a, item) => a + (item === false ? 1 : 0), 0);
  const passedTests = Object.values(testStatuses).reduce((a, item) => a + (item === true ? 1 : 0), 0)
  const percentagePassed = (passedTests / totalTests * 100) +"%";
  
  return (
      <div>
          <ProgressBar label={percentagePassed}>
            <ProgressBar variant="success" now={passedTests} key={1} max={totalTests}/>
            <ProgressBar variant="danger" now={failedTests} key={2} max={totalTests}/>
          </ProgressBar>
      </div>
  );
};

export default TestStatusMeter;