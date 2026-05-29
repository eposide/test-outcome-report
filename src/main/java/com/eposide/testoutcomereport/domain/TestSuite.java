package com.eposide.testoutcomereport.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TestSuite {
    private String name;
    private List<TestCase> testCases;
    private List<TestSuite> suites;
}
