package com.eposide.testoutcomereport.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "organizations")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Organization {
    private String id;
    private String name;
    private String description;
    private String contactName;
    private String contactEmail;
    private String contactPhone;
    private OrganizationStatus status;
}
