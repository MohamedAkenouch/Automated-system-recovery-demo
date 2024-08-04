package com.apps.recovery_system.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class DatabaseConfigPayload {
    private String DB_ENGINE;
    private String DB_NAME;
    private String DB_USER;
    private String DB_PASSWORD;
    private int DB_PORT;
}
