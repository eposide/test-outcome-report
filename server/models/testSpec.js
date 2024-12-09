const mongoose = require('mongoose');

const testSpec = new mongoose.Schema({
  title: String,
  testDate:  Date, 
  status: String,
  duration: Number,
  testResult: {type: mongoose.Types.ObjectId, ref: 'TestResult'}
});

module.exports = mongoose.model('TestSpec', testSpec);
