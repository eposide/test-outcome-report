# Test Outcome Report Server

This is the server component of the Test Outcome Report application. It provides an API for fetching test results.

## Installation

1. Install Node.js and npm (https://nodejs.org/)
2. Clone the repository: `git clone https://github.com/your-username/test-outcome-report.git`
3. Navigate to the server directory: `cd test-outcome-report/server`
4. Install dependencies: `npm install`

## Usage

1. Start the server: `npm start`
2. The server will start listening on `http://localhost:3001`

## API Endpoints

### GET /api/results/:jobNo

Fetch test results for a specific job number.

#### Parameters

- `jobNo` (string): The job number for which to fetch test results.

#### Response

- 200 OK: Returns the test results for the specified job number.
- 500 Internal Server Error: If an error occurs while fetching the test results.

### GET /api/results

Fetch all test results.

#### Response

- 200 OK: Returns an array of test results.
- 500 Internal Server Error: If an error occurs while fetching the test results.


# Test Outcome Report Client

This is the client component of the Test Outcome Report application. It displays test results fetched from the server.

## Installation

1. Install Node.js and npm (https://nodejs.org/)
2. Clone the repository: `git clone https://github.com/your-username/test-outcome-report.git`
3. Navigate to the client directory: `cd test-outcome-report/client`
4. Install dependencies: `npm install`

## Usage

1. Start the client: `npm start`
2. The client will start in development mode and open in your default browser at `http://localhost:3000`

## Configuration

You can configure the API URL by creating a `.env` file in the root of the client directory. Add the following line:

REACT_APP_API_URL=http://localhost:3001
