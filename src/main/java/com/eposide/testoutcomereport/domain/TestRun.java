package com.eposide.testoutcomereport.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Document(collection = "test_runs")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TestRun {
    @Id
    private String id;
    private String project;
    private String branch;
    private String commitId;
    private String environment;
    private String source; // e.g., jenkins, github-actions
    private String framework; // e.g., playwright, junit
    private LocalDateTime timestamp;
    private List<TestSuite> suites;
    private TestSummary summary;
    private Map<String, Object> metadata;
}
