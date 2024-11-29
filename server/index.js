const express = require('express');
const fs = require('fs/promises');
const app = express();
const path = require('path');

const cors = require('cors');
require('dotenv').config();

let testResultFiles = [];

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
                console.log(`Found file: ${filePath}`);
                testResultFiles.push(filePath);
            }
        }
    } catch (error) {
        console.error(`Error reading directory ${dir}:`, error);
    }
}

// Configure CORS
app.use(cors({
  origin: 'http://localhost:3000', // Replace with your React app's URL
  methods: ['GET', 'POST', 'PUT', 'DELETE'], // Add allowed methods
  credentials: true, // If cookies or other credentials are used
}));

app.get('/api/results/:jobNo', async (req, res) => {
    const jobNo  = req.params.jobNo;
    try {
        console.log("jobNo:" +jobNo);
     
        testResultFiles = [];
        const directory = process.env.TEST_JOBS_LOCATION + jobNo + '/archive';
        await searchFiles(directory, 'results.json')

        console.log(`Found ${testResultFiles.length} test result files`);

        const resultPromises = testResultFiles.map(async (file) =>  {
            console.log(`Reading file: ${file}`);
            const data = await fs.readFile(file, 'utf-8');
            return {filePath: file, data: JSON.parse(data)};
        });

        const results = await Promise.all(resultPromises);
        
        res.json(results);

    } catch (error) {
        res.status(500).send({ error: 'Unable to fetch test results' });
    }
});

app.get('/api/results', async (req, res) => {
    try {
        //const files = await fs.readdir('/temp/testresults/');
        //console.log(files);
        testResultFiles = [];
        await searchFiles('/temp/testresults/', 'results.json')

        console.log(`Found ${testResultFiles.length} test result files`);

        const resultPromises = testResultFiles.map(async (file) =>  {
            console.log(`Reading file: ${file}`);
            const data = await fs.readFile(file, 'utf-8');
            return JSON.parse(data);
        });

        const results = await Promise.all(resultPromises);
        
        res.json(results);
        
        
    } catch (error) {
        console.log(error);
        res.status(500).send({ error: 'Unable to fetch test results' });
    }
});

app.get('/api/testjobs', async (req, res) => {
    try {
        const files = await fs.readdir('/temp/testresults/');
        console.log(files);
        
        
        res.json(files);
        
        
    } catch (error) {
        console.log(error);
        res.status(500).send({ error: 'Unable to fetch test results' });
    }
});


app.listen(3001, () => {
    console.log('Server running on http://localhost:30001');
});
