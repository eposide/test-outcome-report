const mongoose = require('mongoose');

const testSpecs = new mongoose.Schema({
  title: String,
  testDate:  Date, 
  status: String,
  duration: Number,
});

module.exports = mongoose.model('testSpecs', testSpecs);
