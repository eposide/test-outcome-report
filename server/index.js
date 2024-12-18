const express = require('express');

const fs = require('fs');

const app = express();
const path = require('path');

const TestSpec = require('./models/TestSpec');
const TestResult = require('./models/TestResult');
const FileUtil = require('./FileUtil');
const DBUtil = require('./DbUtil');
require('dotenv').config();

let clients = []
let noOfFiles = 0;
let reloading = false;
const fileUtil  = new FileUtil();
const dbUtil = new DBUtil();

(async () => {
  
  const initFiles = await fileUtil.searchFiles(process.env.TEST_JOBS_LOCATION, 'results.json');
  noOfFiles = initFiles.length;
  await dbUtil.populateTestResultDatabase(initFiles);

})();



function getDateFromData(data) {
  const startTime = data.stats.startTime;
  const date = new Date(startTime);
  return date;
}

function sendEvent(data) {
  clients.forEach((client) => {
    client.write(`data: ${JSON.stringify({ message: "New test results arrived!" })}\n\n`);
  });
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
  res.setHeader("Connection", "keep-alive");

  // Add current client to the list of connected clients
  clients.push(res);
  console.log("New client connected:", clients.length);

  // Send notification to client if new result files arrived
  fs.watch(process.env.TEST_JOBS_LOCATION, async (eventType, eventSource) => {
        
        console.log(`Event type: ${eventType} on source: ${eventSource}`);
        if (!reloading) {
            console.log('Reloading is false will do check for reload');
            reloading = true; // prevent multiple reloads while waiting for the file change event
            await fileUtil.changeInSourceLocation();
            const files = await fileUtil.searchFiles(process.env.TEST_JOBS_LOCATION, 'results.json');
            console.log('no of files vs files found ' + (noOfFiles != files.length));
            if (noOfFiles != files.length) {
                await dbUtil.populateTestResultDatabase(files);
                sendEvent('New test results arrived!');
                noOfFiles = files.length; // update the number of files to the current count
            }
            reloading = false; // reset the reload flag when the file change event is processed
        }
  });

  req.on("close", () => {
     clients = clients.filter((client) => client !== res);
     console.log("Client disconnected:", clients.length);  
  });
  
});


// Serve static files from the react app
app.use(express.static(path.join(__dirname, 'client/build')));


app.get('*', (req, res) => {
    res.sendFile(path.join(__dirname, 'client/build/index.html'));
});


const PORT = process.env.PORT || 5000;
app.listen(PORT, () => console.log(`Server running on port ${PORT}`));
