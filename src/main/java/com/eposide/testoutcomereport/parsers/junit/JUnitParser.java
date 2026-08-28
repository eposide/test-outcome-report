package com.eposide.testoutcomereport.parsers.junit;

import com.eposide.testoutcomereport.domain.*;
import com.eposide.testoutcomereport.parsers.ParserContext;
import com.eposide.testoutcomereport.parsers.ParserUtil;
import com.eposide.testoutcomereport.parsers.TestResultParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class JUnitParser implements TestResultParser {

    private final XmlMapper xmlMapper;

    public JUnitParser() {
        this.xmlMapper = new XmlMapper();
    }

    private static final String FRAMEWORK_NAME = "junit";
    private static final List<ParserFormat> SUPPORTED_FORMATS = new ArrayList<>(List.of(ParserFormat.XML));
    @Override
    public String getFrameworkName() {
        return FRAMEWORK_NAME;
    }

    @Override
    public List<ParserFormat> getFormats() {
        return SUPPORTED_FORMATS;
    }

    @Override
    public boolean supports(ParserContext context) {

        if (context == null) {
            return false;
        }
        if (context.getFramework() == null) {
            return false;
        }
        if (context.getFormat() == null) {
            return false;
        }

        return context.getFramework().equals(FRAMEWORK_NAME) && SUPPORTED_FORMATS.contains(context.getFormat());

    }

    @Override
    public TestRun parse(String payload, ParserContext context) throws Exception {

        //Only supports XML no need to check format for now
        JsonNode rootNode = xmlMapper.readTree(payload.getBytes());

        List<TestSuite> suites = extractTestSuites(rootNode);

        TestRun testRun = ParserUtil.buildTestRun(suites, context);
        testRun.setFramework("junit");

        return testRun;
    }

    private List<TestSuite> extractTestSuites(JsonNode testSuitesNode) {
        List<TestSuite> result = new ArrayList<>();

        if (testSuitesNode == null || testSuitesNode.isMissingNode()) {
            return result;
        }

        JsonNode suitesArray = testSuitesNode.get("testsuite");
        if (suitesArray != null) {
            if (suitesArray.isArray()) {
                for (JsonNode suiteNode : suitesArray) {
                    walkSuite(suiteNode, result, null);
                }
            } else {
                // Single testsuite
                walkSuite(suitesArray, result, null);
            }
        } else {
            // Handle case where root is a single testsuite
            if (testSuitesNode.has("name") && testSuitesNode.has("testcase")) {
                walkSuite(testSuitesNode, result, null);
            }
        }

        return result;
    }

    /**
     * Recursive traversal for nested suites
     */
    private void walkSuite(JsonNode suiteNode, List<TestSuite> result, String parentName) {
        String title = suiteNode.get("name").asText();
        String suiteName = parentName != null ? parentName + " > " + title : title;

        // Handle testcases
        JsonNode testCasesNode = suiteNode.get("testcase");
        if (testCasesNode != null) {
            List<TestCase> testCases = new ArrayList<>();
            if (testCasesNode.isArray()) {
                for (JsonNode testCaseNode : testCasesNode) {
                    testCases.add(extractTestCase(testCaseNode));
                }
            } else {
                // Single testcase
                testCases.add(extractTestCase(testCasesNode));
            }

            TestSuite suite = new TestSuite();
            suite.setName(suiteName);
            suite.setTestCases(testCases);
            result.add(suite);
        }

        // Recurse into nested suites
        JsonNode nestedSuitesNode = suiteNode.get("testsuite");
        if (nestedSuitesNode != null) {
            if (nestedSuitesNode.isArray()) {
                for (JsonNode childSuiteNode : nestedSuitesNode) {
                    walkSuite(childSuiteNode, result, suiteName);
                }
            } else {
                // Single nested suite
                walkSuite(nestedSuitesNode, result, suiteName);
            }
        }
    }

    private TestCase extractTestCase(JsonNode testCaseNode) {
        TestCase testCase = new TestCase();
        testCase.setName(testCaseNode.get("name").asText());

        JsonNode fileNode = testCaseNode.get("file");
        if (fileNode != null) {
            testCase.setFilePath(fileNode.asText());
        }

        double time = testCaseNode.get("time").asDouble(0);
        testCase.setDurationMs((long) (time * 1000));

        if (testCaseNode.has("failure")) {
            testCase.setStatus(TestStatus.FAILED);
            JsonNode failureNode = testCaseNode.get("failure");
            JsonNode messageNode = failureNode.get("message");
            if (messageNode != null) {
                testCase.setErrorMessage(messageNode.asText());
            }
            JsonNode textNode = failureNode.get("#text");
            if (textNode != null) {
                testCase.setStackTrace(textNode.asText());
            }
        } else if (testCaseNode.has("skipped")) {
            testCase.setStatus(TestStatus.SKIPPED);
        } else {
            testCase.setStatus(TestStatus.PASSED);
        }

        return testCase;
    }
}
