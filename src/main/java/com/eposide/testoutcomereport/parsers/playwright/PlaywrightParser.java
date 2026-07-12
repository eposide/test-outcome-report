package com.eposide.testoutcomereport.parsers.playwright;

import com.eposide.testoutcomereport.domain.ParserFormat;
import com.eposide.testoutcomereport.domain.TestRun;
import com.eposide.testoutcomereport.domain.TestSuite;
import com.eposide.testoutcomereport.parsers.ParserContext;
import com.eposide.testoutcomereport.parsers.ParserUtil;
import com.eposide.testoutcomereport.parsers.TestResultParser;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class PlaywrightParser implements TestResultParser {

    private static final String FRAMEWORK_NAME = "playwright";
    private static final List<ParserFormat> SUPPORTED_FORMATS = new ArrayList<>(List.of(ParserFormat.XML, ParserFormat.JSON));
    @Override
    public String getFrameworkName() {
        return FRAMEWORK_NAME;
    }

    @Override
    public List<ParserFormat> getFormats() {
        return SUPPORTED_FORMATS;
    }

    @Override
    public boolean supports(ParserContext context) {

        if (context == null) {
            return false;
        }
        if (context.getFramework() == null) {
            return false;
        }
        if (context.getFormat() == null) {
            return false;
        }

        return context.getFramework().equals(FRAMEWORK_NAME) && SUPPORTED_FORMATS.contains(context.getFormat());

    }

    @Override
    public TestRun parse(String input, ParserContext context) throws Exception {
        List<TestSuite> suites = getSuites(input, context);
        TestRun testRun = ParserUtil.buildTestRun(suites, context);
        testRun.setFramework(FRAMEWORK_NAME);
        return testRun;
    }

    private List<TestSuite> getSuites(String input, ParserContext context) throws IOException {

        return switch (context.getFormat()) {
            case JSON -> new PlaywrightJsonReader().readSuites(input);
            case XML -> new PlaywrightXmlReader().readSuites(input);
            default -> throw new IllegalArgumentException("Unsupported format: " + context.getFormat());
        };
    }
}
