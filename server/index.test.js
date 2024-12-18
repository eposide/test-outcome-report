const fs = require('fs');
const { promisify } = require('util');
const watch = promisify(fs.watch);

// Mock required modules and functions
jest.mock('fs');
jest.mock('util');
jest.mock('./FileUtil');
jest.mock('./DbUtil');

const FileUtil = require('./FileUtil');
const DbUtil = require('./DbUtil');

const fileUtilMock = new FileUtil();
const dbUtilMock = new DbUtil();

// Mock environment variable
process.env.TEST_JOBS_LOCATION = undefined;

// Mock functions
FileUtil.prototype.changeInSourceLocation = jest.fn();
FileUtil.prototype.searchFiles = jest.fn().mockResolvedValue([]);
DbUtil.prototype.populateTestResultDatabase = jest.fn();

// Test case
test('Should handle case when TEST_JOBS_LOCATION is not set', async () => {
  const res = {
    setHeader: jest.fn(),
    write: jest.fn(),
  };

  const appGetEvents = require('./index').app.get('/events');

  await appGetEvents({}, res);

  expect(res.setHeader).toHaveBeenCalledWith('Content-Type', 'text/event-stream');
  expect(res.setHeader).toHaveBeenCalledWith('Cache-Control', 'no-cache');

  // Verify that fs.watch is not called when TEST_JOBS_LOCATION is not set
  expect(watch).not.toHaveBeenCalled();
});

test('Should handle case fs.watch is called multiple times for the same directory', async () => {

// Unit test for selected code in index.js

// Mock fs.watch to simulate multiple file change events
fs.watch.mockImplementation((dir, listener) => {
  listener('change', 'new-results-1.json');
  listener('change', 'new-results-2.json');
  return {
    close: jest.fn(),
  };
});

// Mock fileUtil.searchFiles to simulate new files being added
FileUtil.prototype.searchFiles.mockResolvedValueOnce([
  'new-results-1.json',
  'new-results-2.json',
  'old-results.json',
]);

// Mock dbUtil.populateTestResultDatabase to ensure it's called with new files
DbUtil.prototype.populateTestResultDatabase.mockResolvedValue();

// Initialize Express app and simulate GET request to /events
const req = {};
const res = {
  setHeader: jest.fn(),
  write: jest.fn(),
};

const appGetEvents = require('./index').app.get('/events');

await appGetEvents(req, res);

// Verify that fs.watch is called with the correct directory
expect(fs.watch).toHaveBeenCalledTimes(1);
expect(fs.watch).toHaveBeenCalledWith(
  process.env.TEST_JOBS_LOCATION,
  expect.any(Function)
);

// Verify that res.write is called with the correct notification message for each new file
expect(res.write).toHaveBeenCalledTimes(2);
expect(res.write).nthCalledWith(1, `data: ${JSON.stringify({ message: 'New test results arrived!' })}\n\n`);
expect(res.write).nthCalledWith(2, `data: ${JSON.stringify({ message: 'New test results arrived!' })}\n\n`);

// Verify that dbUtil.populateTestResultDatabase is called with new files
expect(dbUtil.populateTestResultDatabase).toHaveBeenCalledTimes(1);
expect(dbUtil.populateTestResultDatabase).toHaveBeenCalledWith([
  'new-results-1.json',
  'new-results-2.json',
  'old-results.json',
]);

});