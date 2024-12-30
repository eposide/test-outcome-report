# Test Outcome Report 

The server component provides an API for fetching test results.

## Installation

1. Install Node.js and npm (https://nodejs.org/)
2. Clone the repository: `git clone https://github.com/your-username/test-outcome-report.git`
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

### GET /api/results/:jobNo

Fetch test results for a specific job number.

#### Parameters

- `jobNo` (string): The job number for which to fetch test results.

#### Response

- 200 OK: Returns the test results for the specified job number.
- 500 Internal Server Error: If an error occurs while fetching the test results.

### GET /api/results/testJobs

Fetch all the test jobs from the configured path.



#### Response

- 200 OK: Returns the test jobs.
- 500 Internal Server Error: If an error occurs while fetching the test results.



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

