const fs = require('fs');
const path = require('path');
require('dotenv').config();


// can be a filename or a directory...
const source = process.env.TEST_JOBS_LOCATION;

class FileUtil {
 
  constructor() {
      console.log('source ' + source);
      this.testResultFiles = [];
      fs.watch(source, (eventType, source) => {
        console.log(`Event type: ${eventType}`);
        this.testResultFiles = [];
        this.searchFiles(source, 'results.json');
    });
  }
  async readDirectories() {  
    try {
      const directories = (await fs.promises.readdir(source, { withFileTypes: true }))
        .filter(dirent => dirent.isDirectory())
        .map(dirent => dirent.name);
      return directories;
    } catch (error) {
      console.error(`Error reading directories ${source}:`, error);
    }
  
  }
  async searchFiles(dir, fileName) {
    try {
        const files = await fs.promises.readdir(dir);

        const statsPromises = files.map(async (file) => {
            const filePath = path.join(dir, file);
            return { filePath, stats: await fs.promises.stat(filePath) };
        });

        const fileStats = await Promise.all(statsPromises);

        for (const { filePath, stats } of fileStats) {
            if (stats.isDirectory()) {
                await this.searchFiles(filePath, fileName);
            } else if (stats.isFile() && filePath.includes(fileName)) {
                this.testResultFiles.push(filePath);
            }
        }
    } catch (error) {
        console.error(`Error reading directory ${dir}:`, error);
    }
    return this.testResultFiles;
  }



}
    


module.exports = FileUtil;
