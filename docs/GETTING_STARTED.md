# Getting Started with Test Outcome Report

## Quick Start (5 minutes)

### Option 1: Docker (Recommended)

```bash
# Navigate to project root
cd test-outcome-report-java

# Build and start with Docker Compose
docker-compose up --build

# Application will be available at http://localhost:8080/api
```

### Option 2: Local Development

#### Prerequisites
- Java 17+
- Maven 3.8.1+
- MongoDB 5.0+

#### Steps

1. **Start MongoDB** (if not already running):
   ```bash
   # Using Homebrew (macOS)
   brew services start mongodb-community

   # Or using Docker
   docker run -d -p 27017:27017 --name mongodb mongo
   ```

2. **Build the project**:
   ```bash
   mvn clean package
   ```

3. **Run the application**:
   ```bash
   mvn spring-boot:run
   ```

4. **Verify it's running**:
   ```bash
   curl http://localhost:8080/api/test-runs
   # Should return: []
   ```

## First Test Upload

Save the following to a file called `test.json`:

```json
{
  "framework": "playwright-json",
  "payload": {
    "suites": [
      {
        "title": "Sample Tests",
        "specs": [
          {
            "title": "First Test",
            "file": "test.spec.ts",
            "tests": [{
              "results": [{
                "status": "passed",
                "duration": 1000
              }]
            }]
          }
        ]
      }
    ]
  },
  "project": "my-project",
  "branch": "main",
  "commitId": "abc123",
  "environment": "dev",
  "source": "manual"
}
```

Upload it:
```bash
curl -X POST http://localhost:8080/api/test-runs/upload \
  -H "Content-Type: application/json" \
  -d @test-payload.json
```

Expected response:
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "message": "Test results uploaded successfully"
}
```

## Project Structure Overview

```
test-outcome-report-java/
├── pom.xml                              # Maven configuration
├── README.md                            # Full documentation
├── API_EXAMPLES.md                      # API usage examples
├── GETTING_STARTED.md                   # This file
├── docker-compose.yml                   # Docker setup
├── Dockerfile                           # Application container
└── src/
    ├── main/
    │   ├── java/com/eposide/testoutcomereport/
    │   │   ├── TestOutcomeReportApplication.java
    │   │   ├── api/                     # REST controllers
    │   │   ├── domain/                  # Domain models
    │   │   ├── parsers/                 # Test result parsers
    │   │   └── repositories/            # MongoDB repositories
    │   └── resources/
    │       └── application.yml          # Configuration
    └── test/
        └── java/                        # Unit tests
```

## Next Steps

1. **Review the API**: Check [API_EXAMPLES.md](API_EXAMPLES.md) for all available endpoints
2. **Add More Parsers**: Implement parsers for other test frameworks (JUnit, TestNG, etc.)
3. **Configure MongoDB**: Update connection string in `application.yml` for production
4. **Add Authentication**: Implement Spring Security for API endpoints
5. **Deploy**: Use Docker or deploy JAR directly to your infrastructure

## Common Tasks

### Run Tests
```bash
mvn test
```

### Build Only (Skip Tests)
```bash
mvn clean package -DskipTests
```

### View Logs
```bash
# When running with Maven
mvn spring-boot:run

# When running as JAR
java -jar target/test-outcome-report-1.0.0.jar
```

### Clean MongoDB
```bash
# Using Docker
docker exec -it test-outcome-report-mongo mongosh

# In the shell
use test-outcome-report
db.test_runs.deleteMany({})
```

### Change MongoDB Connection
Edit `src/main/resources/application.yml`:
```yaml
spring:
  data:
    mongodb:
      uri: mongodb://user:password@hostname:27017/database?authSource=admin
```

## Troubleshooting

| Issue | Solution |
|-------|----------|
| Port 8080 in use | Change port in `application.yml` or kill process on port 8080 |
| MongoDB connection refused | Ensure MongoDB is running on localhost:27017 |
| Tests failing | Check MongoDB is running and accessible |
| Build fails | Ensure Java 17+ is installed: `java -version` |

## Support

- See [README.md](README.md) for comprehensive documentation
- Check [API_EXAMPLES.md](API_EXAMPLES.md) for API usage patterns
- Review test files in `src/test/java` for integration examples

## Architecture Notes

The application uses a **parser pattern** to support multiple test frameworks:

1. **ParserRegistry**: Discovers and routes to the correct parser
2. **ParserContext**: Contains metadata about the test run
3. **TestResultParser**: Interface for framework-specific implementations
4. **PlaywrightJsonParser**: Example implementation for Playwright

To add support for a new framework, create a class implementing `TestResultParser` and annotate it with `@Component`.
