package com.eposide.testoutcomereport.exception;

public class TestOutcomeException extends RuntimeException {

    public TestOutcomeException(String message) {
        super(message);
    }
    public TestOutcomeException(String message, Throwable cause) {
        super(message, cause);
    }
    public TestOutcomeException(Throwable cause) {
        super(cause);
    }

}
