import React from "react";
import SpecsResults from "./components/SpecsResults";
import Loader from "./components/Loader";
import { ApplicationContext } from "./context/ApplicationContext";

import 'bootstrap/dist/css/bootstrap.min.css';

const App = () => {
  const { isLoading } = React.useContext(ApplicationContext);
  return (
    <>
    { isLoading ? <Loader /> : <SpecsResults /> }
    </>
  )};

export default App;
