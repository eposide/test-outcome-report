# Test Outcome Report API - Usage Examples

## Prerequisites

Ensure MongoDB is running locally on port 27017, or update the connection string in `application.yml`.

## Start the Application

```bash
mvn spring-boot:run
```

The API will be available at: http://localhost:8080/api

## Example API Calls

### 1. Upload Playwright Test Results

```bash
curl -X POST http://localhost:8080/api/test-runs/upload \
  -H "Content-Type: application/json" \
  -d @examples/upload-playwright-results.json
```

### 2. Get All Test Runs

```bash
curl http://localhost:8080/api/test-runs
```

### 3. Get Test Runs by Project

```bash
curl "http://localhost:8080/api/test-runs?project=my-project"
```

### 4. Get Test Runs by Project and Branch

```bash
curl "http://localhost:8080/api/test-runs?project=my-project&branch=main"
```

### 5. Get Specific Test Run

```bash
curl http://localhost:8080/api/test-runs/{testRunId}
```

## Example Request Payloads

### Upload Playwright Results

```json
{
  "framework": "playwright-json",
  "payload": {
    "suites": [
      {
        "title": "E2E Tests",
        "specs": [
          {
            "title": "Login Test",
            "file": "tests/login.spec.ts",
            "tests": [
              {
                "results": [
                  {
                    "status": "passed",
                    "duration": 2500,
                    "error": null
                  }
                ]
              }
            ]
          },
          {
            "title": "Checkout Test",
            "file": "tests/checkout.spec.ts",
            "tests": [
              {
                "results": [
                  {
                    "status": "failed",
                    "duration": 5000,
                    "error": {
                      "message": "Expected element not found",
                      "stack": "at checkout.spec.ts:45"
                    }
                  }
                ]
              }
            ]
          }
        ]
      }
    ]
  },
  "project": "e-commerce-app",
  "branch": "feature/payments",
  "commitId": "abc123def456",
  "environment": "staging",
  "source": "jenkins"
}
```

## Response Examples

### Successful Upload Response

```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "message": "Test results uploaded successfully"
}
```

### Error Response

```json
{
  "error": "framework and payload are required"
}
```

### Get Test Run Response

```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "project": "e-commerce-app",
  "branch": "feature/payments",
  "commitId": "abc123def456",
  "environment": "staging",
  "source": "jenkins",
  "framework": "playwright",
  "timestamp": "2026-05-13T14:30:00",
  "suites": [
    {
      "name": "E2E Tests",
      "testCases": [
        {
          "name": "Login Test",
          "status": "PASSED",
          "durationMs": 2500,
          "filePath": "tests/login.spec.ts"
        },
        {
          "name": "Checkout Test",
          "status": "FAILED",
          "durationMs": 5000,
          "errorMessage": "Expected element not found",
          "stackTrace": "at checkout.spec.ts:45",
          "filePath": "tests/checkout.spec.ts"
        }
      ]
    }
  ]
}
```

## Using with Jenkins

In your Jenkins pipeline, you can post test results like this:

```groovy
post {
    always {
        script {
            def testResults = readJSON file: 'test-results.json'
            
            sh '''
                curl -X POST http://localhost:8080/api/test-runs/upload \
                  -H "Content-Type: application/json" \
                  -d '{
                    "framework": "playwright-json",
                    "payload": '"$(cat test-results.json)"',
                    "project": "'${PROJECT_NAME}'",
                    "branch": "'${GIT_BRANCH}'",
                    "commitId": "'${GIT_COMMIT}'",
                    "environment": "testing",
                    "source": "jenkins"
                  }'
            '''
        }
    }
}
```

## Troubleshooting

### Connection Refused to MongoDB

Ensure MongoDB is running:
```bash
# macOS with Homebrew
brew services start mongodb-community

# Linux with systemd
sudo systemctl start mongod

# Docker
docker run -d -p 27017:27017 --name mongodb mongo
```

### Port Already in Use

Change the port in `application.yml`:
```yaml
server:
  port: 8081
```

### Framework Not Supported

Check if the parser is implemented. Currently supported:
- `playwright-json`
- `playwright`

To add support for other frameworks, create a new parser class implementing `TestResultParser`.
