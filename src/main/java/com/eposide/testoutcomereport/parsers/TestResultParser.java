package com.eposide.testoutcomereport.parsers;

import com.eposide.testoutcomereport.domain.ParserFormat;
import com.eposide.testoutcomereport.domain.TestRun;

import java.util.List;

public interface TestResultParser {
    boolean supports(ParserContext context);
    String getFrameworkName();
    List<ParserFormat> getFormats();
    TestRun parse(String input, ParserContext context) throws Exception;
}
