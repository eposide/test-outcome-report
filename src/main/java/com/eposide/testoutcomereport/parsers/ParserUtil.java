package com.eposide.testoutcomereport.parsers;

import com.eposide.testoutcomereport.domain.TestCase;
import com.eposide.testoutcomereport.domain.TestRun;
import com.eposide.testoutcomereport.domain.TestSuite;
import com.eposide.testoutcomereport.domain.TestSummary;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;

public class ParserUtil {

    private ParserUtil() {
        // private constructor to prevent instantiation
    }

    private static TestSummary getSummary(List<TestSuite> suites) {
        TestSummary summary = new TestSummary();
        int total = 0;
        int passed = 0;
        int failed = 0;
        int skipped = 0;
        long durationMs = 0;

        for (TestSuite suite : suites) {
            for (TestCase testCase : suite.getTestCases()) {
                total++;
                switch (testCase.getStatus()) {
                    case PASSED -> passed++;
                    case FAILED -> failed++;
                    case SKIPPED -> skipped++;
                }
                durationMs += testCase.getDurationMs();
            }
        }
        summary.setTotalTests(total);
        summary.setPassedTests(passed);
        summary.setFailedTests(failed);
        summary.setSkippedTests(skipped);
        // calculate pass rate as a percentage
        summary.setPassRate((total > 0 ? (double) passed / (double) total : 0.0) * 100.0);

        summary.setTotalDurationMs(durationMs);

        return summary;
    }

    /**
     * Builds a TestRun object from the given list of TestSuites and ParserContext.
     * This method populates the TestRun with metadata from the context and calculates the summary.
     * @param suites  the list of TestSuites to include in the TestRun
     * @param context the ParserContext containing metadata about the test run
     * @return a populated TestRun object with no assigned framework
     */

    public static TestRun buildTestRun(List<TestSuite> suites, ParserContext context) {

        TestRun testRun = new TestRun();
        testRun.setId(UUID.randomUUID().toString());
        testRun.setOrganizationId(context.getOrganizationId());
        testRun.setProject(context.getProject() != null ? context.getProject() : "unknown");
        testRun.setBranch(context.getBranch());
        testRun.setCommitId(context.getCommitId());
        testRun.setEnvironment(context.getEnvironment());
        testRun.setSource(context.getSource());
        testRun.setTimestamp(LocalDateTime.now(ZoneId.systemDefault()));
        testRun.setSuites(suites);

        testRun.setSummary(getSummary(suites));

        return testRun;
    }
}
