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
  
  const [isLoading, setIsLoading] = useState(false)
  const [testResult, setTestResult] = useState();
  const [notification, setNotification] = useState("");
  const [testResultDetail, setTestResultDetail] = useState();
  const [testSpecs, setTestSpecs] = useState([]);
  const [specRuns, setSpecRuns] = useState([]);
  const [filter, setFilter] = useState({specs: []});
  

  return (
    <ApplicationContext.Provider value={{ isLoading, setIsLoading, testResult, setTestResult, testResultDetail, setTestResultDetail, testSpecs, setTestSpecs, specRuns, setSpecRuns, notification, setNotification, filter, setFilter }}>
      {children}
    </ApplicationContext.Provider>
  );
};
