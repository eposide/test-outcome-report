package com.eposide.testoutcomereport.parsers;

import com.eposide.testoutcomereport.domain.ParserFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ParserContext {
    private String organizationId;
    private String framework;
    private ParserFormat format;
    private String fileName;
    private String contentType;
    private String project;
    private String branch;
    private String commitId;
    private String environment;
    private String source;
}
