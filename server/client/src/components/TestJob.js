import React, { useContext } from "react";
import { ApplicationContext } from "./ApplicationContext";
import Button from "react-bootstrap/Button";

const TestJob = ({ jobNo }) => {
  const { setJobNo } = useContext(ApplicationContext);
  console.log(jobNo)
  // Display test job
  return (
        <li>
          <Button variant="outline-secondary" onClick={() => setJobNo(jobNo)}>{jobNo}</Button>
        </li>
  );
};

export default TestJob;
