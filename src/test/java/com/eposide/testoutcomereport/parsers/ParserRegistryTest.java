package com.eposide.testoutcomereport.parsers;

import com.eposide.testoutcomereport.domain.TestRun;
import com.eposide.testoutcomereport.domain.TestStatus;
import com.eposide.testoutcomereport.parsers.playwright.PlaywrightJsonParser;
import com.eposide.testoutcomereport.parsers.playwright.PlaywrightXmlParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ParserRegistryTest {

    private ParserRegistry parserRegistry;
    private PlaywrightJsonParser playwrightJsonParser;
    private PlaywrightXmlParser playwrightXmlParser;

    @BeforeEach
    void setUp() {
        playwrightJsonParser = new PlaywrightJsonParser();
        playwrightXmlParser = new PlaywrightXmlParser();
        parserRegistry = new ParserRegistry(java.util.List.of(playwrightJsonParser, playwrightXmlParser));
    }

    @Test
    void testResolvePlaywrightJsonParser() {
        ParserContext context = new ParserContext();
        context.setFramework("playwright-json");

        TestResultParser parser = parserRegistry.resolve(context);
        assertNotNull(parser);
        assertTrue(parser instanceof PlaywrightJsonParser);
    }

    @Test
    void testResolvePlaywrightXmlParser() {
        ParserContext context = new ParserContext();
        context.setFramework("playwright-xml");

        TestResultParser parser = parserRegistry.resolve(context);
        assertNotNull(parser);
        assertTrue(parser instanceof PlaywrightXmlParser);
    }

    @Test
    void testResolveMissingParserThrowsException() {
        ParserContext context = new ParserContext();
        context.setFramework("unknown-framework");

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> parserRegistry.resolve(context)
        );

        assertTrue(exception.getMessage().contains("No parser found"));
    }

    @Test
    void testParsePlaywrightJson() throws Exception {
        String playwrightJson = """
                {
                    "suites": [
                        {
                            "title": "Test Suite",
                            "specs": [
                                {
                                    "title": "Test Case",
                                    "file": "test.spec.ts",
                                    "tests": [
                                        {
                                            "results": [
                                                {
                                                    "status": "passed",
                                                    "duration": 1000
                                                }
                                            ]
                                        }
                                    ]
                                }
                            ]
                        }
                    ]
                }
                """;

        ParserContext context = new ParserContext();
        context.setFramework("playwright-json");
        context.setProject("test-project");
        context.setBranch("main");

        TestRun testRun = parserRegistry.parseTestResults(playwrightJson, context);

        assertNotNull(testRun);
        assertEquals("test-project", testRun.getProject());
        assertEquals("main", testRun.getBranch());
        assertEquals("playwright", testRun.getFramework());
        assertNotNull(testRun.getSuites());
        assertEquals(1, testRun.getSuites().size());
    }

    @Test
    void testParsePlaywrightXml() throws Exception {
        String playwrightXml = """
                <?xml version="1.0" encoding="UTF-8"?>
                <testsuites name="Playwright Test Results" tests="10" failures="2" skipped="1" time="17.350">
                  <testsuite name="Authentication Tests" tests="3" failures="0" skipped="1" time="6.100" timestamp="2026-04-26T10:15:30Z">
                    <testcase name="should log in with valid credentials" classname="tests.auth" time="2.500" file="tests/auth.spec.ts"/>
                    <testcase name="should fail with invalid password" classname="tests.auth" time="1.800" file="tests/auth.spec.ts"/>
                    <testcase name="should display remember me option" classname="tests.auth" time="0" file="tests/auth.spec.ts">
                      <skipped/>
                    </testcase>
                  </testsuite>
                  <testsuite name="Dashboard" tests="5" failures="1" skipped="0" time="8.850" timestamp="2026-04-26T10:15:37Z">
                    <testsuite name="User Profile" tests="2" failures="1" skipped="0" time="4.700" timestamp="2026-04-26T10:15:37Z">
                      <testcase name="should display user profile" classname="tests.dashboard.UserProfile" time="1.200" file="tests/dashboard.spec.ts"/>
                      <testcase name="should update profile picture" classname="tests.dashboard.UserProfile" time="3.500" file="tests/dashboard.spec.ts">
                        <failure message="Expected element to be visible">Error: Expected element to be visible
                  at test/dashboard.spec.ts:75:20
                  at processTicksAndRejections (internal/timers.js:1:1)</failure>
                      </testcase>
                    </testsuite>
                    <testsuite name="Settings" tests="1" failures="0" skipped="0" time="2.100" timestamp="2026-04-26T10:15:42Z">
                      <testcase name="should save preferences" classname="tests.dashboard.Settings" time="2.100" file="tests/dashboard.spec.ts"/>
                    </testsuite>
                  </testsuite>
                  <testsuite name="Search Functionality" tests="1" failures="1" skipped="0" time="5.600" timestamp="2026-04-26T10:15:46Z">
                    <testcase name="should search for products" classname="tests.search" time="5.600" file="tests/search.spec.ts">
                      <failure message="Timeout waiting for search results">TimeoutError: Timeout waiting for search results
                  at test/search.spec.ts:25:15
                  at processTicksAndRejections (internal/timers.js:1:1)</failure>
                    </testcase>
                  </testsuite>
                </testsuites>
                """;

        ParserContext context = new ParserContext();
        context.setFramework("playwright-xml");
        context.setProject("test-project");
        context.setBranch("main");

        TestRun testRun = parserRegistry.parseTestResults(playwrightXml, context);

        assertNotNull(testRun);
        assertEquals("test-project", testRun.getProject());
        assertEquals("main", testRun.getBranch());
        assertEquals("playwright", testRun.getFramework());
        assertNotNull(testRun.getSuites());
        assertEquals(4, testRun.getSuites().size()); // Authentication Tests, User Profile, Settings, Search Functionality

        // Check specific suites
        assertEquals("Authentication Tests", testRun.getSuites().get(0).getName());
        assertEquals(3, testRun.getSuites().get(0).getTestCases().size());

        assertEquals("Dashboard > User Profile", testRun.getSuites().get(1).getName());
        assertEquals(2, testRun.getSuites().get(1).getTestCases().size());

        assertEquals("Dashboard > Settings", testRun.getSuites().get(2).getName());
        assertEquals(1, testRun.getSuites().get(2).getTestCases().size());

        assertEquals("Search Functionality", testRun.getSuites().get(3).getName());
        assertEquals(1, testRun.getSuites().get(3).getTestCases().size());

        // Check a failed test case
        var failedTest = testRun.getSuites().get(1).getTestCases().get(1);
        assertEquals("should update profile picture", failedTest.getName());
        assertEquals(TestStatus.FAILED, failedTest.getStatus());
        assertEquals("Expected element to be visible", failedTest.getErrorMessage());
    }
}
