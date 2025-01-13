import React from "react";
import SpecsResults from "./components/SpecsResults";
import Loader from "./components/Loader";
import { ApplicationContext } from "./context/ApplicationContext";

import 'bootstrap/dist/css/bootstrap.min.css';

const App = () => {
  const { isLoadingData } = React.useContext(ApplicationContext);
  return (
    <>
    { isLoadingData ? <Loader /> : <SpecsResults /> }
    </>
  )};

export default App;
