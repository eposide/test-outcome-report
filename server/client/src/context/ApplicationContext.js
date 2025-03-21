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
  const [filter, setFilter] = useState({specs: [], dateFrom: null, dateTo: null});
  const [isLoadingData, setIsLoadingData] = useState(false);
  const [selectedTitle, setSelectedTitle] = useState("");
  

  return (
    <ApplicationContext.Provider value={{ testResult, setTestResult, testResultDetail, setTestResultDetail, testSpecs, setTestSpecs, specRuns, setSpecRuns, notification, setNotification, filter, setFilter, isLoadingData, setIsLoadingData, selectedTitle, setSelectedTitle }}>
      {children}
    </ApplicationContext.Provider>
  );
};
