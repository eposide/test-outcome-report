# Test Outcome Report 

The server component provides an API for fetching test results.

## Installation

1. Install Node.js and npm (https://nodejs.org/)
2. Clone the repository
3. Navigate to the server directory: `cd test-outcome-report/server`
4. Install dependencies: `npm run install-app`
5. Run the application: `npm start`

## Configuration
These configured parameters are required
TEST_JOBS_LOCATION=c:/temp/testrunner/
PORT=5000
MONGODB_URI=mongodb://localhost:27017/
LIMIT_TEST_RESULTS=5

## Usage

1. Start the server: `node index.js`
2. 
2. The server will start listening on `http://localhost:[PORT]`

## API Endpoints

### GET /api/testSpecs
Fetches all the testSpecs that has been loaded in the database. It will limit the number of runs according to the configured number LIMIT_TEST_RESULTS

#### Response

- 200 OK: Returns the test specs 
- 500 Internal Server Error: If an error occurs while fetching the test specs.


### GET /api/result/:resultId

Fetch the test result for a result id. The response will be the test result output with all the details of the test

#### Parameters

- `resultId` (string): The id of the test result which was is associated with the test spec

#### Response

- 200 OK: Returns the test results for the specified resultId.
- 500 Internal Server Error: If an error occurs while fetching the test results.

### GET /api/reload 

Will reload all test results from the configured path TEST_JOBS_LOCATION

#### Response

- 200 OK: reload was successful
- 500 Internal Server Error: If an error occurs while reloading test results.

### GET /api/report

Downloads a csv file containing all tests in the database. The csv file has the following columns 
  Title = name of the test 
  TestDate = when the test was run 
  Duration = how long it took to complete the test
  Status = outcome of the test

#### Response

- 200 OK: a csv file which is downloaded to the client 
- 500 Internal Server Error: If an error occurs while retrieving the report.

# Test Outcome Report Client

This is the client component of the Test Outcome Report application. It displays test results fetched from the server.

## Installation

1. Install Node.js and npm (https://nodejs.org/)
2. Clone the repository: `git clone https://github.com/your-username/test-outcome-report.git`
3. Navigate to the client directory: `cd test-outcome-report/client`
4. Install dependencies: `npm install`
5. Create an optimised build of the client: `npm run build` 

## Usage

1. Start the client: `npm start`
2. The client will start in development mode and open in your default browser at `http://localhost:[PORT]`

