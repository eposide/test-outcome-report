const express = require('express');
const fs = require('fs');

const app = express();
const path = require('path');

const TestSpec = require('./models/TestSpec');
const TestResult = require('./models/TestResult');
const FileUtil = require('./FileUtil');
const DBUtil = require('./DbUtil');
require('dotenv').config();

let noOfFiles = 0;
const fileUtil  = new FileUtil();
const dbUtil = new DBUtil();

(async () => {
  
  const initfiles = await fileUtil.searchFiles(process.env.TEST_JOBS_LOCATION, 'results.json');
  noOfFiles = initfiles.length;
  await dbUtil.populateTestResultDatabase(initfiles);

})();


let testResultFiles = [];

function getDateFromData(data) {
  const startTime = data.stats.startTime;
  const date = new Date(startTime);
  return date;
}

app.get('/api/result/:resultId', async (req, res) => {
    const resultId  = req.params.resultId;
    try {
        const resultDetail = await dbUtil.getTestResult(resultId);
        res.json(resultDetail);
    } catch (error) {
        res.status(500).send({ error: 'Unable to fetch test results' });
    }
});

app.get('/api/testSpecs', async (req, res) => {

    try { 
        
        const groupTestResults = await dbUtil.getTestSpecs();

        res.json(groupTestResults);
        
    } catch (error) {
        console.log(error);
        res.status(500).send({ error: 'Unable to fetch test specs' });
    }
});

app.get("/events", async (req, res) => {
  res.setHeader("Content-Type", "text/event-stream");
  res.setHeader("Cache-Control", "no-cache");

  // Send notification to client if new result files arrived
  fs.watch(process.env.TEST_JOBS_LOCATION, async (eventType, eventSource) => {
        console.log(`Event type: ${eventType} on source: ${eventSource}`);
        fileUtil.changeInSourceLocation();
        const files = await fileUtil.searchFiles(process.env.TEST_JOBS_LOCATION, 'results.json');
        if (noOfFiles != files.length) {
            await dbUtil.populateTestResultDatabase(files);
            res.write(`data: ${JSON.stringify({ message: "New test results arrived!" })}\n\n`);
        }
  });
  
});

// Serve static files from the react app
app.use(express.static(path.join(__dirname, 'client/build')));


app.get('*', (req, res) => {
    res.sendFile(path.join(__dirname, 'client/build/index.html'));
});


const PORT = process.env.PORT || 5000;
app.listen(PORT, () => console.log(`Server running on port ${PORT}`));
