package com.eposide.testoutcomereport.api;

import com.eposide.testoutcomereport.domain.ParserFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UploadRequest {
    @NotBlank(message = "Organization ID is required")
    private String organizationId;

    @NotBlank(message = "Framework is required")
    @Pattern(regexp = "^(playwright-json|playwright-xml|junit-xml|robot)$", 
            message = "Framework must be one of: playwright-json, playwright-xml, junit-xml, robot")
    private String framework;

    private String format;

    @NotBlank(message = "Project name is required")
    @Size(min = 1, max = 255, message = "Project name must be between 1 and 255 characters")
    @Pattern(regexp = "^[a-zA-Z0-9_\\-. ]+$", message = "Project name contains invalid characters")
    private String project;

    @Size(max = 100, message = "Branch name must not exceed 100 characters")
    @Pattern(regexp = "^[a-zA-Z0-9_\\-/.]*$", message = "Branch name contains invalid characters")
    private String branch;

    @Size(max = 100, message = "Commit ID must not exceed 100 characters")
    private String commitId;

    @Size(max = 100, message = "Environment must not exceed 100 characters")
    @Pattern(regexp = "^[a-zA-Z0-9_\\-]*$", message = "Environment contains invalid characters")
    private String environment;

    @Size(max = 100, message = "Source must not exceed 100 characters")
    @Pattern(regexp = "^[a-zA-Z0-9_\\-]*$", message = "Source contains invalid characters")
    private String source;
}
