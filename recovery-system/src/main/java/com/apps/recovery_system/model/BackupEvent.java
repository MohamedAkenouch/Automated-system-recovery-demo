package com.apps.recovery_system.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BackupEvent {

    private String databaseName;
    private long timestamp;
}