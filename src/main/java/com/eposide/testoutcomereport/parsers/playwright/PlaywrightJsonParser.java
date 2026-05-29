package com.eposide.testoutcomereport.parsers.playwright;

import com.eposide.testoutcomereport.domain.*;
import com.eposide.testoutcomereport.parsers.ParserContext;
import com.eposide.testoutcomereport.parsers.SuiteUtil;
import com.eposide.testoutcomereport.parsers.TestResultParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Component
public class PlaywrightJsonParser implements TestResultParser {
    private static final String FRAMEWORK_NAME = "playwright-json";
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String getFrameworkName() {
        return FRAMEWORK_NAME;
    }

    @Override
    public boolean supports(ParserContext context) {
        return FRAMEWORK_NAME.equalsIgnoreCase(context.getFramework());
    }

    @Override
    public TestRun parse(String payload, ParserContext context) throws Exception {
        JsonNode rootNode = null;

        rootNode = objectMapper.readTree(payload.getBytes());

        List<TestSuite> suites = extractSuites(rootNode.get("suites"));

        TestRun testRun = new TestRun();
        testRun.setId(UUID.randomUUID().toString());
        testRun.setProject(context.getProject() != null ? context.getProject() : "unknown");
        testRun.setBranch(context.getBranch());
        testRun.setCommitId(context.getCommitId());
        testRun.setEnvironment(context.getEnvironment());
        testRun.setSource(context.getSource());
        testRun.setFramework("playwright");
        testRun.setTimestamp(LocalDateTime.now());
        testRun.setSuites(suites);

        testRun.setSummary(SuiteUtil.getSummary(suites));
        return testRun;
    }


    private List<TestSuite> extractSuites(JsonNode suitesNode) {
        List<TestSuite> result = new ArrayList<>();

        if (suitesNode == null || suitesNode.isMissingNode()) {
            return result;
        }

        for (JsonNode suiteNode : suitesNode) {
            walkSuite(suiteNode, result, null);
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
