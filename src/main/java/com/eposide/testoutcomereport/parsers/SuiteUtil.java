package com.eposide.testoutcomereport.parsers;

import com.eposide.testoutcomereport.domain.TestCase;
import com.eposide.testoutcomereport.domain.TestSuite;
import com.eposide.testoutcomereport.domain.TestSummary;

import java.util.List;

public class SuiteUtil {

    private SuiteUtil() {
        // private constructor to prevent instantiation
    }

    public static TestSummary getSummary(List<TestSuite> suites) {
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
}
