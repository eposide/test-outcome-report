import React from "react";
import { ApplicationContext } from "../context/ApplicationContext";
import Form from 'react-bootstrap/Form';
import Button from 'react-bootstrap/Button';
import Collapse from "react-bootstrap/Collapse";



const Filter = () => { 

  const [open, setOpen] = React.useState(false);

  const { testSpecs, filter, setFilter } = React.useContext(ApplicationContext);

  //initialize the filter
 
  React.useEffect(() => {
    if (testSpecs &&  Object.entries(testSpecs).length > 0 && filter?.specs?.length === 0) {
      const specs = Object.keys(testSpecs).map(spec => ({title: spec, filtered: true}));
      setFilter({ specs: specs });
    }
  }, [testSpecs, filter, setFilter]);

  // Update the filter state when a checkbox is clicked
  const handleTitleChange = (event) => {
    const { name, checked } = event.target;
    const updatedFilter = { ...filter };

    updatedFilter.specs = updatedFilter.specs.map(spec =>
      (spec.title === name || name === 'all') ? { ...spec, filtered: checked } : spec
    );

    setFilter(updatedFilter);
  };

  
  return (
    <div>
        <Button 
            variant="outline-secondary"
            onClick={() => setOpen(!open)}
            aria-controls="open-close-filter"
            aria-expanded={open}
        >Filter
        </Button>
        <div>
            <Collapse in={open}>
                <Form>
                    <Form.Group controlId="selectedTitles" title="Titles">
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
                </Form>
            </Collapse>
        </div>
    </div>
  );
};

export default Filter;