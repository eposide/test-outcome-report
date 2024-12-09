import React, { useContext, useEffect } from "react";
import { ApplicationContext } from "./components/ApplicationContext";
import TestJob from "./components/TestJob";
import TestResults from "./components/TestResults";
import SpecsResults from "./components/SpecsResults";
import Button from "react-bootstrap/Button";



const App = () => {
  const { jobNo, setTestResults, setJobNo, setTestJobs, testJobs } = useContext(ApplicationContext);
  
  
  return (  
    <div>
       <SpecsResults />
    </div>
  );
};

export default App;
