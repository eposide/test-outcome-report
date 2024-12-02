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
                testResultFiles.push(filePath);
            }
        }
    } catch (error) {
        console.error(`Error reading directory ${dir}:`, error);
    }
}


app.get('/api/results/:jobNo', async (req, res) => {
    const jobNo  = req.params.jobNo;
    try {
       
        testResultFiles = [];
        const directory = process.env.TEST_JOBS_LOCATION + jobNo + '/archive';
        await searchFiles(directory, 'results.json')
       
        const resultPromises = testResultFiles.map(async (file) =>  {
           
            const data = await fs.readFile(file, 'utf-8');
            return {filePath: file, data: JSON.parse(data)};
        });

        const results = await Promise.all(resultPromises);
        
        res.json(results);

    } catch (error) {
        res.status(500).send({ error: 'Unable to fetch test results' });
    }
});


app.get('/api/testjobs', async (req, res) => {
    try {
        console.log("Fetching all test jobs");
       // const files = await fs.readdir(process.env.TEST_JOBS_LOCATION);
        const source = process.env.TEST_JOBS_LOCATION;
        const directories = 
            (await fs.readdir(source, {withFileTypes: true}))
             .filter(dirent => dirent.isDirectory())
             .map(dirent => dirent.name);

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
