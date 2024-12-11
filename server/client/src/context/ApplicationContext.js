import React, { createContext, useState } from "react";

export const ApplicationContext = createContext({
  testResult: null,
  setTestResult: () => {},
  testResultDetail: null,
  setTestResultDetail: () => {},
  testSpecs: null,
  setTestSpecs: () => {}
});

export const ApplicationProvider = ({ children }) => {
  
  const [testResult, setTestResult] = useState();
  const [testResultDetail, setTestResultDetail] = useState();
  const [testSpecs, setTestSpecs] = useState([]);
  

  return (
    <ApplicationContext.Provider value={{ testResult, setTestResult, testResultDetail, setTestResultDetail, testSpecs, setTestSpecs }}>
      {children}
    </ApplicationContext.Provider>
  );
};
