const { MongoMemoryServer } = require('mongodb-memory-server');
const mongoose = require('mongoose');
const fs = require('fs');
const log = require('log4js').getLogger('dbutil');

const TestSpec = require('./models/TestSpec');
const TestResult = require('./models/TestResult');
require('dotenv').config();


let mongoServer; 

(async () => {
  
   mongoServer = process.env.MONGODB_IN_MEMORY == 'YES'
    ? await MongoMemoryServer.create({ instance: { dbPath: './data' } })
    : null;
  
  const uri = mongoServer ? mongoServer.getUri() : process.env.MONGODB_URI;
  log.info("database uri " + uri);
  await mongoose.connect(uri, {
    dbName: 'testresultsDB',
  });

  log.info(process.env.MONGODB_IN_MEMORY === 'YES'
    ? "MongoDB connected to in-memory server"
    : "MongoDB connected to external server");
})();

class DBUtil {
 
  

  async populateTestResultDatabase(files) { 
    try {
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
    } catch (error) {
        log.error("Error populating test result database:", error);
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

        log.debug("saving test result " + testResultModel);
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
            log.debug("saving spec " + testSpecModel);
            await testSpecModel.save();
        }
    }
  }

  getDateFromData(data) {
    const startTime = data.stats.startTime;
    const date = new Date(startTime);
    return date;
  }


  async getAllTestSpecs() { 
    log.debug("Fetching all specs");
    const testSpecs = await TestSpec.find().sort({title: 1, testDate: -1});
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


  /**
   * Retrieves all test specs from the database. noOfTests will be applied to the number of tests per spec
   * If no number is provided, it fetches all tests for specs.
   * 
   * @param {number} [noOfTests] - The number of tests per spec to retrieve. If not provided, all tests per spec will be fetched.
   * @returns {Object} - An object containing grouped test specs. Each key is a test spec title, and the value is an array of test spec details.
   * 
   * @example
   * // Fetch all tests for specs
   * const allTestSpecs = await dbUtil.getTestSpecs();
   * 
   * // Fetch the first 10 tests per spec
   * const first10TestSpecs = await dbUtil.getTestSpecs(10);
   */
  async getTestSpecs(noOfTests) { 

      if (!noOfTests || noOfTests === 0 || noOfTests < 0) {
        return this.getAllTestSpecs();
      }

      log.debug("Fetching " + noOfTests + " tests per spec");

      const testSpecs = await TestSpec.find().sort({title: 1, testDate: -1});
      const groupTestResults = testSpecs.reduce((acc, suite, index) => {
            const details = { duration:suite.duration, testDate: suite.testDate, status: suite.status, testResult: suite.testResult}; 
            const title = suite.title;
            if (!acc[title]) {
              acc[title] = [];
            }
            if (acc[title].length < noOfTests) {
              acc[title] = acc[title].concat(details); // Combine test specs for the same title
            }
            return acc;
          }, {});
      return groupTestResults;
  }

  

  async getTestResult(testResultId) { 
    log.debug("Fetching test result by id " + testResultId);
    const testResult = await TestResult.findById(testResultId);
    return testResult.results;
  }

}

module.exports = DBUtil;