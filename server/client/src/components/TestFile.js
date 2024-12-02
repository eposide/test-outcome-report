import React, { useState } from "react";
import TestSuite from "./TestSuite";
import { Collapse } from "react-bootstrap";

const TestFile = ({ result }) => {

  const [open, setOpen] = useState(false);

  const toggleOpen = () => {
    setOpen(!open);
  };

  return (
    <div className="card">
      <div className="card-header" 
        style={{ backgroundColor: 'grey'}}
        onClick={toggleOpen} 
      >
        Results file : {result.filePath} | Duration : {result.data.stats['duration']} 
        | StartTime : {result.data.stats['startTime']} | Expected : {result.data.stats['expected']}
      </div>
      <Collapse in={open}>
       <div className="card-body">
        {result.data.suites.map((suite, index) => (
          <TestSuite key={ index } suite={ suite } />
        ))}
      </div>
      </Collapse>
    </div>
  );
};

export default TestFile;