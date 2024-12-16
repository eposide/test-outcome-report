const { MongoMemoryServer } = require('mongodb-memory-server');
const mongoose = require('mongoose');
const fs = require('fs');

const TestSpec = require('./models/TestSpec');
const TestResult = require('./models/TestResult');
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

class DBUtil {
 
  

  async populateTestResultDatabase(files) { 
    
     for (const file of files) {

        const data = await fs.promises.readFile(file, 'utf-8');
        const resultFileRef = {filePath: file, data: JSON.parse(data)};
        
        const testResult = await this.saveTestResult(resultFileRef);
        const testDate = this.getDateFromData(resultFileRef.data);
        const testSuites = resultFileRef.data.suites;
        for (const testSuite of testSuites) {
            this.saveSpecs(testSuite, testDate, testResult);
        }      
    }
  }

  async saveTestResult(resultFileRef) {
        const dateTime = this.getDateFromData(resultFileRef.data)
    let savedResult = await TestResult.find({jobNo:resultFileRef.jobNo, file: resultFileRef.filePath, dateTime: dateTime});
    if (!savedResult || savedResult.length == 0) {
        const testResultModel = new TestResult({
            jobNo: resultFileRef.jobNo,
            file: resultFileRef.filePath,
            dateTime: dateTime,
            results: resultFileRef.data
        });

        console.log("saving test result " + testResultModel);
        await testResultModel.save();
        return testResultModel;
    }
    return savedResult[0];


  } 

  
  async  saveSpecs(testSuite, testDate, testResult) {

    let specs;
    if (testSuite.specs && testSuite.specs.length > 0) {
        specs = testSuite.specs;
    } else if (testSuite.suites && testSuite.suites.length > 0) {
        specs = testSuite.suites.flatMap(suite => suite.specs);
    }

    for (const testSpec of specs) {
        const savedSpecs = await TestSpec.find({title:testSpec.title, testDate: testDate});
        if (savedSpecs && savedSpecs.length == 0) {
          let status = "";
             let duration = 0;
            if (!testSpec.tests[0].results[0]) {
                status = "Skipped";
                duration = 0;
            } else {
              status = testSpec.tests[0].results[0].status;
              duration = testSpec.tests[0].results[0].duration;
            }
            const testSpecModel = new TestSpec(
                    {
                        title: testSpec.title,
                        testDate: testDate, 
                        status: status,
                        duration: duration,
                        testResult: testResult
                    }
              );
            console.log("saving spec " + testSpecModel);
            await testSpecModel.save();
        }
    }
  }

  getDateFromData(data) {
    const startTime = data.stats.startTime;
    const date = new Date(startTime);
    return date;
  }


  async getTestSpecs() { 
    console.log("Fetching all specs");
    const testSpecs = await TestSpec.find();
    const groupTestResults = testSpecs.reduce((acc, suite) => {
            const details = { duration:suite.duration, testDate: suite.testDate, status: suite.status, testResult: suite.testResult}; 
            const title = suite.title;
            if (!acc[title]) {
                acc[title] = [];
            }
            acc[title] = acc[title].concat(details); // Combine test specs for the same title
            return acc;
        }, {});
    return groupTestResults;
  }

  async getTestResult(testResultId) { 
    console.log("Fetching test result by id " + testResultId);
    const testResult = await TestResult.findById(testResultId);
    return testResult.results;
  }

}

module.exports = DBUtil;