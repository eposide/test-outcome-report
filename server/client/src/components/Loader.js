import React from "react";
import Spinner from 'react-bootstrap/Spinner'

const Loader = () => {

    console.log("Loading spinners...");

    return (
        <div className="d-flex justify-content-center align-items-center">
            <Spinner animation="border" variant="primary" />
            <p className="m-2">Test data loading...</p>
        </div>
    );

};

export default Loader;
