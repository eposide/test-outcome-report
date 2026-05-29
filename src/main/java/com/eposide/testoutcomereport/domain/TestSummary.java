package com.eposide.testoutcomereport.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TestSummary {
    private long totalTests;
    private long passedTests;
    private long failedTests;
    private long skippedTests;
    private long totalDurationMs;
    private double passRate;
}
