import React, { createContext, useState } from "react";

export const ApplicationContext = createContext({
  testResults: [],
  setTestResults: () => {},
  jobNo: 1,
  setJobNo: () => {}
});

export const ApplicationProvider = ({ children }) => {
  const [testResults, setTestResults] = useState([]);
  const [jobNo, setJobNo] = useState(1); // Use state for jobNo

  return (
    <ApplicationContext.Provider value={{ testResults, setTestResults, jobNo, setJobNo }}>
      {children}
    </ApplicationContext.Provider>
  );
};
