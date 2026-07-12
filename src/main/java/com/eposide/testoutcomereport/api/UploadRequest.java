package com.eposide.testoutcomereport.api;

import com.eposide.testoutcomereport.domain.ParserFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UploadRequest {
    private String organizationId;
    private String framework;
    private String format;
    private String project;
    private String branch;
    private String commitId;
    private String environment;
    private String source;
}
