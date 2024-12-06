const express = require('express');
const mongoose = require('mongoose');
const fs = require('fs/promises');
const app = express();
const path = require('path');
const { MongoMemoryServer } = require('mongodb-memory-server');
const cors = require('cors');
const TestSpecs = require('./models/testSpecs');
require('dotenv').config();

let mongoServer; 

(async () => {
  
   mongoServer = process.env.MONGODB_IN_MEMORY == 'YES'
    ? await MongoMemoryServer.create({ instance: { dbPath: './data' } })
    : null;
  
  const uri = mongoServer ? mongoServer.getUri() : process.env.MONGODB_URI;
  console.log("database uri " + uri);
  await mongoose.connect(uri, {
    dbName: 'testresultsDB',
  });

  console.log(process.env.MONGODB_IN_MEMORY === 'YES'
    ? "MongoDB connected to in-memory server"
    : "MongoDB connected to external server");
})();


let testResultFiles = [];
function getDateFromData(data) {
  const startTime = data.stats.startTime;
  const date = new Date(startTime);
  return date;
}


async function searchFiles(dir, fileName) {
    try {
        const files = await fs.readdir(dir);

        const statsPromises = files.map(async (file) => {
            const filePath = path.join(dir, file);
            return { filePath, stats: await fs.stat(filePath) };
        });

        const fileStats = await Promise.all(statsPromises);

        for (const { filePath, stats } of fileStats) {
            if (stats.isDirectory()) {
                await searchFiles(filePath, fileName);
            } else if (stats.isFile() && filePath.includes(fileName)) {
                testResultFiles.push(filePath);
            }
        }
    } catch (error) {
        console.error(`Error reading directory ${dir}:`, error);
    }
}

async function getDirectories() {
    const source = process.env.TEST_JOBS_LOCATION;
    const directories = (await fs.readdir(source, { withFileTypes: true }))
        .filter(dirent => dirent.isDirectory())
        .map(dirent => dirent.name);
    return directories;
}

async function getResultsForJob(jobNo) {
    testResultFiles = [];
    const directory = process.env.TEST_JOBS_LOCATION + jobNo + '/archive';
    await searchFiles(directory, 'results.json');

    const resultPromises = testResultFiles.map(async (file) => {
        const data = await fs.readFile(file, 'utf-8');
        return {jobNo: jobNo, filePath: file, data: JSON.parse(data) };
    });

    const results = await Promise.all(resultPromises);
    return results;
}


async function populateTestResultDatabase(jobDirectories) { 

    for (const jobNo of jobDirectories) {

        const jobResults = await getResultsForJob(jobNo);
        
        for (const result of jobResults) {
            const testDate = getDateFromData(result.data);
            const testSuites = result.data.suites;
            for (const testSuite of testSuites) {
                saveSpecs(testSuite, testDate);
            }
        }
    }
}

async function saveSpecs(testSuite, testDate) {

    let specs;
    if (testSuite.specs && testSuite.specs.length > 0) {
        specs = testSuite.specs;
    } else if (testSuite.suites && testSuite.suites.length > 0) {
        specs = testSuite.suites.flatMap(suite => suite.specs);
    }

    for (const testSpec of specs) {
        const savedSpecs = await TestSpecs.find({title:testSpec.title, testDate: testDate});
        if (savedSpecs && savedSpecs.length == 0) {
            const status = testSpec.tests[0].results[0].status;
            const duration = testSpec.tests[0].results[0].duration;
            const testSpecModel = new TestSpecs(
                    {
                        title: testSpec.title,
                        testDate: testDate, 
                        status: status,
                        duration: duration,
                    }
                );
            console.log("saving spec " + testSpecModel);
            await testSpecModel.save();
        }
    }

}


app.get('/api/results/:jobNo', async (req, res) => {
    const jobNo  = req.params.jobNo;
    try {
        const results = await getResultsForJob(jobNo);
        res.json(results);
    } catch (error) {
        res.status(500).send({ error: 'Unable to fetch test results' });
    }
});




app.get('/api/testjobs', async (req, res) => {
    try {
        console.log("Fetching all test jobs");
       
        const directories = await getDirectories();
        
        populateTestResultDatabase(directories);

        console.log(directories);
        res.json(directories);
        
        
    } catch (error) {
        console.log(error);
        res.status(500).send({ error: 'Unable to fetch test results' });
    }
});

// Serve static files from the react app
app.use(express.static(path.join(__dirname, 'client/build')));

app.get('*', (req, res) => {
    res.sendFile(path.join(__dirname, 'client/build/index.html'));
});


const PORT = process.env.PORT || 5000;
app.listen(PORT, () => console.log(`Server running on port ${PORT}`));
