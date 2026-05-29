package com.eposide.testoutcomereport.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "test_projects")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TestProject {

    private String projectName;
    private String projectDescription;

}
