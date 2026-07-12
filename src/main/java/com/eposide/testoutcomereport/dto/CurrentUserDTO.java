package com.eposide.testoutcomereport.dto;

import com.eposide.testoutcomereport.domain.UserRoles;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CurrentUserDTO {

    private String username;
    private String organizationName;
    private Set<UserRoles> roles;
}
