const mongoose = require('mongoose');


const testResults = new mongoose.Schema({
  jobNo: String, 
  file: String,
  dateTime: Date,
  results: mongoose.Schema.Types.Mixed, // To store any type of JSON object
});

module.exports = mongoose.model('testResults', testResults);
