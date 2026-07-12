package com.eposide.testoutcomereport.parsers.robot;

import com.eposide.testoutcomereport.domain.*;
import com.eposide.testoutcomereport.parsers.ParserContext;
import com.eposide.testoutcomereport.parsers.ParserUtil;
import com.eposide.testoutcomereport.parsers.TestResultParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class RobotParser implements TestResultParser {

    private final XmlMapper xmlMapper = new XmlMapper();
    private static final String FRAMEWORK_NAME = "robot";
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
        JsonNode rootNode = xmlMapper.readTree(payload.getBytes());

        List<TestSuite> suites = extractTestSuites(rootNode);

        TestRun testRun = ParserUtil.buildTestRun(suites, context);
        testRun.setFramework(FRAMEWORK_NAME);

        return testRun;
    }

    private List<TestSuite> extractTestSuites(JsonNode rootNode) {
        List<TestSuite> result = new ArrayList<>();

        if (rootNode == null || rootNode.isMissingNode()) {
            return result;
        }

        // Get the suite element (root suite in robot-output.xml structure)
        JsonNode suiteNode = rootNode.get("suite");
        if (suiteNode != null) {
            walkSuite(suiteNode, result, null);
        }

        return result;
    }

    /**
     * Recursive traversal for nested suites
     */
    private void walkSuite(JsonNode suiteNode, List<TestSuite> result, String parentName) {
        String title = suiteNode.get("name").asText();
        String suiteName = parentName != null ? parentName + " > " + title : title;

        List<TestCase> testCases = new ArrayList<>();
        
        // Handle testcases
        JsonNode testCasesNode = suiteNode.get("test");
        if (testCasesNode != null) {
            if (testCasesNode.isArray()) {
                for (JsonNode testCaseNode : testCasesNode) {
                    testCases.add(extractTestCase(testCaseNode));
                }
            } else {
                // Single testcase
                testCases.add(extractTestCase(testCasesNode));
            }
        }

        // Check for nested suites
        JsonNode nestedSuitesNode = suiteNode.get("suite");
        List<JsonNode> nestedSuites = new ArrayList<>();
        if (nestedSuitesNode != null) {
            if (nestedSuitesNode.isArray()) {
                nestedSuitesNode.forEach(nestedSuites::add);
            } else {
                nestedSuites.add(nestedSuitesNode);
            }
        }

        // If this suite has test cases, add it to results
        if (!testCases.isEmpty()) {
            TestSuite suite = new TestSuite();
            suite.setName(suiteName);
            suite.setTestCases(testCases);
            result.add(suite);
        }

        // Always recurse into nested suites
        for (JsonNode childSuiteNode : nestedSuites) {
            walkSuite(childSuiteNode, result, suiteName);
        }
    }

    private TestCase extractTestCase(JsonNode testCaseNode) {
        TestCase testCase = new TestCase();
        testCase.setName(testCaseNode.get("name").asText());

        JsonNode fileNode = testCaseNode.get("file");
        if (fileNode != null) {
            testCase.setFilePath(fileNode.asText());
        }

        JsonNode statusNode = testCaseNode.get("status");

        if (statusNode != null) {
            long time = statusNode.get("elapsed").asLong();
            testCase.setDurationMs(time);

            String status = statusNode.get("status").asText();

            if (status.equals("FAIL")) {
                testCase.setStatus(TestStatus.FAILED);
                // Extract failure message from msg elements within the test case
                JsonNode msgNode = testCaseNode.get("msg");
                String failureMessage = "";
                if (msgNode != null) {
                    if (msgNode.isArray()) {
                        // Get the last message which typically contains the error
                        for (JsonNode msg : msgNode) {
                            failureMessage = msg.asText();
                        }
                    } else {
                        failureMessage = msgNode.asText();
                    }
                }
                testCase.setErrorMessage(failureMessage.trim());

            } else if (status.equals("SKIP")) {
                testCase.setStatus(TestStatus.SKIPPED);
            } else  {
                testCase.setStatus(TestStatus.PASSED);
            }

        }
        return testCase;
    }


}
