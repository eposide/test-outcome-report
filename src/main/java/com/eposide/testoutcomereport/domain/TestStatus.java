package com.eposide.testoutcomereport.domain;

public enum TestStatus {
    PASSED("PASSED"),
    FAILED("FAILED"),
    SKIPPED("SKIPPED");

    private final String value;

    TestStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static TestStatus fromString(String value) {
        for (TestStatus status : TestStatus.values()) {
            if (status.value.equalsIgnoreCase(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown test status: " + value);
    }
}
