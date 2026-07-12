# Test Outcome Report 

An application for managing and displaying test run results from various testing frameworks. It provides a REST API for uploading test results and a user interface for viewing test run history and details.
Currently supports Playwright JSON and XML formats, with a flexible architecture for adding new parsers as needed. 

## 🆕 Now with Organization Segregation & Authentication!

This application now includes complete user authentication with BCrypt password hashing, organization-level data segregation, and role-based access control.

**New Features:**
- ✅ User login with BCrypt password hashing
- ✅ User and organization registration
- ✅ Organization-level data segregation
- ✅ Role-based access control (SUPER_ADMIN, ADMIN, USER, VIEWER)
- ✅ Session management with MongoDB backend
- ✅ Dashboard filtered by organization
- ✅ Test history filtered by organization

## Prerequisites

- Java 17 or higher
- Maven 3.8.1 or higher
- MongoDB 5.0 or higher

## Project Structure

```
src/main/java/com/eposide/testoutcomereport/
├── TestOutcomeReportApplication.java    # Spring Boot entry point
├── api/
│   ├── TestRunController.java           # REST API endpoints
│   └── UploadRequest.java               # Request DTO
├── domain/
│   ├── TestRun.java                     # Main domain model
│   ├── TestSuite.java                   # Test suite model
│   ├── TestProject.java                 # Test project model
│   ├── TestCase.java                    # Test case model
│   ├── TestStatus.java                  # Test status enum
│   └── TestSummary.java                 # Summary statistics
├── parsers/
│   ├── TestResultParser.java            # Parser interface
│   ├── ParserContext.java               # Parser context
│   ├── ParserRegistry.java              # Parser registry
│   ├── SuiteUtil.java                   # Utility class for parsing suites
│   ├── playwright/
│   │   ├── PlaywrightJsonParser.java    # Playwright JSON parser implementation
│   │   └── PlaywrightXmlParser.java     # Playwright XML parser implementation
│   └── junit/
│       └── JunitXmlParser.java                 # JUnit XML parser implementation
├── repositories/
│   ├── TestRunRepository.java           # MongoDB repository
│   └── ProjectRepository.java           # MongoDB repository for projects
└── web/
    ├── DashboardController.java         # User interface controller dashboard
    ├── FileUploadController.java        # User Interface controller for file uploads
    ├── TestHistoryController.java       # User Interface controller for test history
    └── TestRunDetailsController.java    # User Interface controller for test run details
    
```

## Building the Project

```bash
mvn clean package
```

## Running the Application

### Development

```bash
mvn spring-boot:run
```

### Production

```bash
java -jar target/test-outcome-report-1.0.0.jar
```

## Configuration

Edit `src/main/resources/application.yml` to configure:

- **MongoDB Connection**: Update `spring.data.mongodb.uri`
- **Server Port**: Modify `server.port` (default: 8080)
- **API Base Path**: Change `server.servlet.context-path` (default: /api)

Example:
```yaml
spring:
  data:
    mongodb:
      uri: mongodb://localhost:27017/test-outcome
```

## API Endpoints

### Upload Test Results

**POST** `/api/test-runs/upload`

Request body:
   request example:
   ```json
 {
  "framework": "playwright-json",
  "project": "my-project",
  "branch": "main",
  "commitId": "abc123def456",
  "environment": "staging",
  "source": "jenkins"
  }
  ```
report string binary file content (e.g., base64-encoded) should be sent as multipart/form-data.

## User interface 
The application includes a user interface for viewing test run results, accessible at `http://localhost:8080/test-outcome/dashboard`. The dashboard provides:

- Recent test runs with status and summary
- Additional menus on the user interface for:
  - Uploading new test results
  - Viewing test history

 By clicking on a test run from either the dashboard or the history page, users can view detailed information about the test run, including individual test cases, their statuses.

Response:
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "message": "Test results uploaded successfully"
}
```


## Adding New Parsers

1. Create a new class implementing `TestResultParser`
2. Implement `supports()` to identify when to use the parser
3. Implement `parse()` to convert test results to `TestRun` model
4. Add `@Component` annotation for Spring auto-discovery

Example:
```java
@Component
@Slf4j
public class JunitParser implements TestResultParser {
    @Override
    public boolean supports(ParserContext context) {
        return "junit-xml".equalsIgnoreCase(context.getFramework());
    }

    @Override
    public TestRun parse(String input, ParserContext context) throws Exception {
        // Implementation here
    }
}
```

## Dependencies

- **Spring Boot 4.0.6**: Web framework and auto-configuration
- **Spring Data MongoDB**: MongoDB integration
- **Lombok**: Boilerplate reduction
- **Jackson**: JSON processing
- **SLF4J/Log4j2**: Logging (via Spring Boot)
- **Java UUID Generator**: UUID generation

## Testing

Run tests with:
```bash
mvn test
```

The project includes embedded MongoDB for testing via `de.flapdoodle.embed:de.flapdoodle.embed.mongo.spring30x`.

## License

ISC

## Author

Frik Briers
