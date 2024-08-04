package com.apps.recovery_system.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ReplayRequest {

    private String databaseUrl;
    private String username;
    private String password;

}