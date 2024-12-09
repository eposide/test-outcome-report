import React, { createContext, useState } from "react";

export const ApplicationContext = createContext({
  testResults: [],
  setTestResults: () => {},
  jobNo: 0,
  setJobNo: () => {}
});

export const ApplicationProvider = ({ children }) => {
  const [testJobs, setTestJobs] = useState([]);
  const [testResults, setTestResults] = useState([]);
  const [testSpecs, setTestSpecs] = useState([]);
  const [jobNo, setJobNo] = useState(); // Use state for jobNo

  return (
    <ApplicationContext.Provider value={{ testResults, setTestResults, jobNo, setJobNo, testJobs, setTestJobs, testSpecs, setTestSpecs }}>
      {children}
    </ApplicationContext.Provider>
  );
};
