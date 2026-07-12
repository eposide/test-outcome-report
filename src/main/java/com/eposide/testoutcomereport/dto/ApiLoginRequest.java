package com.eposide.testoutcomereport.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiLoginRequest {
    private String username;
    private String password;
}
