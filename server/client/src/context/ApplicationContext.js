import React, { createContext, useState } from "react";

export const ApplicationContext = createContext({
  testResult: null,
  setTestResult: () => {},
  testResultDetail: null,
  setTestResultDetail: () => {},
  testSpecs: null,
  setTestSpecs: () => {},
  isLoading: () => true
});

export const ApplicationProvider = ({ children }) => {
  
  
  const [testResult, setTestResult] = useState();
  const [notification, setNotification] = useState("");
  const [testResultDetail, setTestResultDetail] = useState();
  const [testSpecs, setTestSpecs] = useState([]);
  const [specRuns, setSpecRuns] = useState([]);
  const [filter, setFilter] = useState({specs: []});
  const [isLoadingData, setIsLoadingData] = useState(false);
  

  return (
    <ApplicationContext.Provider value={{ testResult, setTestResult, testResultDetail, setTestResultDetail, testSpecs, setTestSpecs, specRuns, setSpecRuns, notification, setNotification, filter, setFilter, isLoadingData, setIsLoadingData }}>
      {children}
    </ApplicationContext.Provider>
  );
};
