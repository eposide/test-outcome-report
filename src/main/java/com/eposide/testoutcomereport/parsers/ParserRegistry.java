package com.eposide.testoutcomereport.parsers;

import com.eposide.testoutcomereport.domain.TestRun;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class ParserRegistry {
    private final List<TestResultParser> parsers;

    public ParserRegistry(List<TestResultParser> parsers) {
        this.parsers = parsers;
    }

    public TestResultParser resolve(ParserContext context) {
        return parsers.stream()
                .filter(parser -> parser.supports(context))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "No parser found for framework: " + context.getFramework()
                ));
    }
    public TestRun parseTestResults(String input, ParserContext context) throws Exception {
        TestResultParser parser = resolve(context);
        return parser.parse(input, context);
    }

    public List<String> getSupportedFrameworks() {
        return parsers.stream()
                .map(TestResultParser::getFrameworkName)
                .toList();
    }

}
