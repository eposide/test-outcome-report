const express = require('express');

const fs = require('fs');

const log4js = require('log4js');
const {Parser} = require('json2csv');
const app = express();
const path = require('path');

try {
    fs.mkdirSync('./logs');
} catch (err) {
    if (err.code !== 'EEXIST') {
        console.error('Error creating logs directory:', err);
        process.exit(1);
    }
}
log4js.configure('./config/log4js.json');

const log = log4js.getLogger('index');


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


function sendEvent(data) {
  clients.forEach((client) => {
    log.info("writing New test results event to client");
    client.write(`data: ${JSON.stringify({ message: "New test results arrived!" })}\n\n`);
  });
}

async function reloadFiles(sendToClients) {

    if (!reloading) {
        log.debug('Reloading is false will do check for reload');
        reloading = true; // prevent multiple reloads while waiting for the file change event
        fileUtil.initTestResultFiles();
        const files = await fileUtil.searchFiles(process.env.TEST_JOBS_LOCATION, 'results.json');
        log.debug('Should populate database ' + (noOfFiles != files.length));
        if (noOfFiles != files.length) {
            await dbUtil.populateTestResultDatabase(files);
            if (sendToClients) { 
                sendEvent('New test results arrived!');
            }
            noOfFiles = files.length; // update the number of files to the current count
        }
        reloading = false; // reset the reload flag when the file change event is processed
    }
}

app.get('/api/result/:resultId', async (req, res) => {
    const resultId  = req.params.resultId;
    try {
        const resultDetail = await dbUtil.getTestResult(resultId);
        res.json(resultDetail);
    } catch (error) {
        log.error(error);
        res.status(500).send({ error: 'Unable to fetch test results' });
    }
});

app.get('/api/testSpecs', async (req, res) => {

    try { 
        const noOfTestsPerSpec = parseInt(process.env.LIMIT_TEST_RESULTS) || 0;
        const groupTestResults = await dbUtil.getTestSpecsGrouped(noOfTestsPerSpec);
        res.json(groupTestResults);
    } catch (error) {
        log.error(error);
        res.status(500).send({ error: 'Unable to fetch test specs' });
    }
});

app.get('/api/reload', async (req, res) => {

    try {
        await reloadFiles(false);
        res.json({ message: 'Files reloaded' });
    } catch (error) {
        log.error(error);
        res.status(500).send({ error: 'Unable to reload files' });
    }
});

app.get('/api/report', async (req, res) => { 
    try {
        const reportData = await dbUtil.getAllTestSpecs();

        const data = Object.values(reportData).map(spec => ({

            Title: spec.title,
            TestDate: spec.testDate,
            Duration: spec.duration,
            Status: spec.status,
        }));
        
        const fields = ['Title', 'TestDate', 'Duration', 'Status'];
        
        const parser = new Parser({ fields });
        const separatorLine = '"sep=,"' + '\r\n';
        const csv = separatorLine + parser.parse(data);


        //save the csv as a file
        const filePath = path.join(__dirname, 'test-results.csv');
        fs.writeFileSync(filePath, csv);

        res.download(filePath); // Set disposition and send it.

       
    } catch (error) {
        log.error(error);
        res.status(500).send({ error: 'Unable to fetch report data' });
    } 
});

app.get("/events", async (req, res) => {
  res.setHeader("Content-Type", "text/event-stream");
  res.setHeader("Cache-Control", "no-cache");
  res.setHeader("Connection", "keep-alive");

  // Add current client to the list of connected clients
  clients.push(res);
  log.info("New client connected:", clients.length);

  // Send notification to client if new result files arrived
  fs.watch(process.env.TEST_JOBS_LOCATION, async (eventType, eventSource) => {
        
        log.debug(`Event type: ${eventType} on source: ${eventSource}`);
        
        if (!reloading) {
            reloadFiles(true);
        }
  });

  req.on("close", () => {
     clients = clients.filter((client) => client !== res);
     log.debug("Client disconnected. Still connected: ", clients.length);  
  });
  
});


// Serve static files from the react app
app.use(express.static(path.join(__dirname, 'client/build')));


app.get('*', (req, res) => {
    res.sendFile(path.join(__dirname, 'client/build/index.html'));
});


const PORT = process.env.PORT || 5000;
app.listen(PORT, () => log.info(`Server running on port ${PORT}`));
