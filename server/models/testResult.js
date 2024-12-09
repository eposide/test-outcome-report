const mongoose = require('mongoose');

const testResult = new mongoose.Schema({
  jobNo: String, 
  file: String,
  dateTime: Date,
  results: mongoose.Schema.Types.Mixed, // To store any type of JSON object
});

module.exports = mongoose.model('TestResult', testResult);
