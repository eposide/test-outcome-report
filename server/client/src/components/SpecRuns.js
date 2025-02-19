import React from "react";
import { ApplicationContext } from "../context/ApplicationContext";
import { v4 as uuidv4 } from 'uuid'; // Import uuid library

const SpecRuns = () => {

  const { specRuns, setTestResult} = React.useContext(ApplicationContext);
  
  return (
        <div className="card-body">
            <table className="table table-striped" style={{ fontSize: "0.8rem" }}>
                <thead>
                  <tr>
                    <th>Date</th>
                    <th>Duration (ms)</th>
                    <th>Status</th>
                    <th>View Results</th>
                  </tr>
                </thead>
                <tbody>
                  {specRuns.map(test => (
                   <tr key={uuidv4()} > 
                      <td>{test.testDate}</td>
                      <td>{test.duration}</td>
                      <td
                        style={{
                          color: test.status === "passed" ? "green" : "red",
                        }}
                      >
                        {test.status}
                      </td>
                      <td>
                      <button type="button" className="btn btn-outline-info" onClick={() => setTestResult(test.testResult)}>Details</button>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
        
      );
};


export default SpecRuns;
