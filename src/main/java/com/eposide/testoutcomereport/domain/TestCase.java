package com.eposide.testoutcomereport.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TestCase {
    private String name;
    private TestStatus status;
    private long durationMs;
    private String errorMessage;
    private String stackTrace;
    private String filePath;
    private String browser;
    private Map<String, Object> metadata;
}
