import React from "react";
import { ApplicationContext } from "./ApplicationContext";
import { Accordion, Card, Button, Table } from "react-bootstrap";
import { v4 as uuidv4 } from 'uuid'; // Import uuid library

const SpecsResultsAccordion = () => {

  const { setTestSpecs, testSpecs } = React.useContext(ApplicationContext);
  
  React.useEffect(() => {

    const url = `/api/testSpecs`;
    fetch(url, { method: 'GET' })
        .then((response) => {
          if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);
          console.log("response testSpecs:" + response.status);
          return response.json();
        })
        .then((data) => setTestSpecs(data))
        .catch((error) => console.error("Error fetching test results:", error));
    
  }, [setTestSpecs]);

  return (
    <Accordion>
      {Object.keys(testSpecs).map((title) => (
        <Card key={title} >
          <Card.Header>
            <Accordion.Toggle as={Button} variant="link" eventKey={title} >
              {title} ({testSpecs[title].length} tests)
            </Accordion.Toggle>
          </Card.Header>
          <Accordion.Collapse eventKey={title}>
            <Card.Body>
              <Table striped bordered hover>
                <thead>
                  <tr>
                    <th>Date</th>
                    <th>Duration (ms)</th>
                    <th>Status</th>
                  </tr>
                </thead>
                <tbody>
                 {testSpecs[title].map((test, index) => (
                    <tr key={uuidv4()} > // Use uuidv4() to generate a unique key
                      <td>{test.testDate}</td>
                      <td>{test.duration}</td>
                      <td
                        style={{
                          color: test.status === "passed" ? "green" : "red",
                        }}
                      >
                        {test.status}
                      </td>
                    </tr>
                  ))}
                </tbody>
              </Table>
            </Card.Body>
          </Accordion.Collapse>
        </Card>
      ))};
    </Accordion> 
    );
};

export default SpecsResultsAccordion;
  
