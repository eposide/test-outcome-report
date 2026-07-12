package com.eposide.testoutcomereport.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Set;

@Document(collection = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    private String id;

    private String username;

    private String passwordHash;

    private String organizationId;

    private Set<UserRoles> roles;
}
