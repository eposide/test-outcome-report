package com.eposide.testoutcomereport.parsers;

import com.eposide.testoutcomereport.domain.TestRun;

public interface TestResultParser {
    boolean supports(ParserContext context);
    String getFrameworkName();
    TestRun parse(String input, ParserContext context) throws Exception;
}
