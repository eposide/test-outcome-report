package com.eposide.testoutcomereport.parsers.playwright;

import com.eposide.testoutcomereport.domain.TestCase;
import com.eposide.testoutcomereport.domain.TestRun;
import com.eposide.testoutcomereport.domain.TestStatus;
import com.eposide.testoutcomereport.domain.TestSuite;
import com.eposide.testoutcomereport.parsers.ParserContext;
import com.eposide.testoutcomereport.parsers.ParserUtil;
import com.eposide.testoutcomereport.parsers.TestResultParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class PlaywrightJsonReader  {

    private final ObjectMapper objectMapper = new ObjectMapper();


    public List<TestSuite> readSuites(String payload) throws IOException {

        JsonNode rootNode = null;

        rootNode = objectMapper.readTree(payload.getBytes());

        return extractSuites(rootNode);
    }


    private List<TestSuite> extractSuites(JsonNode suitesNode) {
        List<TestSuite> result = new ArrayList<>();

        if (suitesNode == null || suitesNode.isMissingNode()) {
            return result;
        }

        JsonNode suitesArray = suitesNode.get("suites");
        if (suitesArray != null) {
            if (suitesArray.isArray()) {
                for (JsonNode suiteNode : suitesArray) {
                    walkSuite(suiteNode, result, null);
                }
            } else {
                // Single testsuite
                walkSuite(suitesArray, result, null);
            }
        }

        return result;
    }

    /**
     * Recursive traversal for nested suites
     */
    private void walkSuite(JsonNode suiteNode, List<TestSuite> result, String parentName) {
        String title = suiteNode.get("title").asText();
        String suiteName = parentName != null ? parentName + " > " + title : title;

        // Handle specs (actual test containers)
        JsonNode specsNode = suiteNode.get("specs");
        if (specsNode != null && specsNode.isArray()) {
            List<TestCase> testCases = new ArrayList<>();
            for (JsonNode specNode : specsNode) {
                testCases.add(extractTestCase(specNode));
            }

            TestSuite suite = new TestSuite();
            suite.setName(suiteName);
            suite.setTestCases(testCases);
            result.add(suite);
        }

        // Recurse into nested suites
        JsonNode nestedSuitesNode = suiteNode.get("suites");
        if (nestedSuitesNode != null && nestedSuitesNode.isArray()) {
            for (JsonNode childSuiteNode : nestedSuitesNode) {
                walkSuite(childSuiteNode, result, suiteName);
            }
        }
    }

    private TestCase extractTestCase(JsonNode specNode) {
        TestCase testCase = new TestCase();
        testCase.setName(specNode.get("title").asText());
        testCase.setFilePath(specNode.get("file").asText());

        // Get the first test result
        JsonNode testsNode = specNode.get("tests");
        if (testsNode == null || !testsNode.isArray() || testsNode.size() == 0) {
            testCase.setStatus(TestStatus.SKIPPED);
            testCase.setDurationMs(0);
            return testCase;
        }

        JsonNode testNode = testsNode.get(0);
        JsonNode resultsNode = testNode.get("results");

        if (resultsNode == null || !resultsNode.isArray() || resultsNode.size() == 0) {
            testCase.setStatus(TestStatus.SKIPPED);
            testCase.setDurationMs(0);
            return testCase;
        }

        // Take the last result (final retry)
        JsonNode lastResult = resultsNode.get(resultsNode.size() - 1);

        String statusString = lastResult.get("status").asText();
        testCase.setStatus(mapStatus(statusString));
        testCase.setDurationMs(lastResult.get("duration").asLong(0));

        // Extract error information if present
        JsonNode errorNode = lastResult.get("error");
        if (errorNode != null) {
            testCase.setErrorMessage(errorNode.get("message").asText());
            testCase.setStackTrace(errorNode.get("stack").asText());
        }

        return testCase;
    }

    private TestStatus mapStatus(String status) {
        return switch (status.toLowerCase()) {
            case "passed" -> TestStatus.PASSED;
            case "failed" -> TestStatus.FAILED;
            case "skipped" -> TestStatus.SKIPPED;
            default -> TestStatus.SKIPPED;
        };
    }
}
