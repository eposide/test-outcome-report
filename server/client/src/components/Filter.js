import React from "react";
import { ApplicationContext } from "../context/ApplicationContext";
import Form from 'react-bootstrap/Form';
import Row from 'react-bootstrap/Row';
import Col from 'react-bootstrap/Col';

import Collapse from "react-bootstrap/Collapse";
import DatePicker from "react-datepicker";

import "react-datepicker/dist/react-datepicker.css";



const Filter = () => { 

  const [open, setOpen] = React.useState(false);
  const { testSpecs, setTestSpecs, filter, setFilter } = React.useContext(ApplicationContext);

  
  //initialize the filter
 
  React.useEffect(() => {
    if (testSpecs &&  Object.entries(testSpecs).length > 0 && filter?.specs?.length === 0) {
      const updatedFilter = { ...filter };
      const specs = Object.keys(testSpecs).map(spec => ({title: spec, filtered: true}));
      updatedFilter.specs = specs
      setFilter(updatedFilter);
    }
  }, [filter, testSpecs]);

  // Update the filter state when a checkbox is clicked
  const handleTitleChange = (event) => {
    const { name, checked } = event.target;
    
    const updatedFilter = { ...filter };
    updatedFilter.specs =  updatedFilter.specs.map(spec =>
      (spec.title === name || name === 'all') ? { ...spec, filtered: checked } : spec
    );
    setFilter(updatedFilter);
    applyFilterToData(updatedFilter);
  };

  const handleDateFromChange = (date) => {
     const updatedFilter = { ...filter };
     updatedFilter.dateFrom = date;
     setFilter(updatedFilter);
     applyFilterToData(updatedFilter);
  }

  const handleDateToChange = (date) => {
     const updatedFilter = {...filter };
     updatedFilter.dateTo = date;
     setFilter(updatedFilter);
     applyFilterToData(updatedFilter);
  }

  const applyFilterToData = (updatedFilter) => {
    
    
    let filteredSpecs = { ...testSpecs};
    if (updatedFilter && updatedFilter.specs.length > 0) {
      // Create a new array with only the filtered titles
      const filteredTitles = updatedFilter.specs.filter(spec => !spec.filtered);
      if (filteredTitles && filteredTitles.length > 0) { 
        filteredTitles.forEach(spec => {
          delete filteredSpecs[spec.title];
        });
      }
    }
    //for the remaining specs filter the runs based on the dateFrom and dateTo
    if (updatedFilter.dateFrom && updatedFilter.dateTo) {
      Object.keys(filteredSpecs).forEach(title => {
        filteredSpecs[title] = filteredSpecs[title].filter(specRun => {
           return new Date(specRun.testDate).getTime() >= new Date(updatedFilter.dateFrom).getTime() && 
           new Date(specRun.testDate).getTime() <= new Date(updatedFilter.dateTo).getTime();
        }
         );
       });
    }
   
    setTestSpecs(filteredSpecs);
  
 };


  
  return (
    <div className="card">
       <div className="row">
            <div className="col-md-4">
              <button type="button" className="btn btn-outline-secondary btn-sm" onClick={() => setOpen(!open)}
                aria-controls="open-close-filter"
                aria-expanded={open}>Filter</button>
            </div>
        </div>
        <Collapse in={open}>
            <div className="row">
                <Form>
                 <Row >
                    <Col>
                      <Form.Group controlId="selectedTitles" title="Titles" style={{ height: "150px", overflowY: "auto" }}>
                          <Form.Check
                              reverse
                              type="checkbox"
                              name="all"
                              label="Select all"
                              id="all"
                              onChange={handleTitleChange}
                              checked={filter?.specs?.every(spec => spec.filtered)}
                          />
                      {filter?.specs?.length > 0 && filter.specs.map(spec => (
                          <Form.Check
                              reverse 
                              key={spec.title}
                              type="checkbox"
                              name={spec.title}
                              label={spec.title}
                              id={`title-${spec.title}`}
                              checked={spec.filtered}
                              onChange={handleTitleChange}
                          />
                          ))}
                      </Form.Group>
                    </Col>
                  <Col >
                   <Form.Group controlId="dates" style={{ display: "flex", flexDirection: "column" }}>
                    <Row>
                    <Col>
                        <label htmlFor="dateFrom">From:</label>
                        <DatePicker
                          selected={filter.dateFrom}
                          onChange={handleDateFromChange}
                          placeholderText="Select a date"
                          id="dateFrom"
                        />
                    </Col>
                    <Col>
                     
                        <label htmlFor="dateTo">To:</label>
                        <DatePicker
                          selected={filter.dateTo}
                          onChange={handleDateToChange}
                          placeholderText="Select a date"
                          id="dateTo"
                        />
                    
                    </Col>
                    
                    </Row>
                    </Form.Group>
                  </Col>
                  </Row>
                </Form>
              </div>
            </Collapse>
          </div>
  );
};

export default Filter;