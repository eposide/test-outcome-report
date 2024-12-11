import React from "react";
import SpecsResults from "./components/SpecsResults";
import TestResultDetail from "./components/TestResultDetail";
import { ApplicationContext } from "./context/ApplicationContext";

import 'bootstrap/dist/css/bootstrap.min.css';

const App = () => {
  const {testResult} = React.useContext(ApplicationContext);

  return (  
    <div className="container">
       <SpecsResults />
    </div>
  );
};

export default App;
