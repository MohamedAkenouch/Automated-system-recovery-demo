package com.apps.recovery_system.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "events")
@Data
public class Event {

    @Id
    private String id;
    private String databaseType;
    private String database;
    private String schema;
    private String table;
    private String operation;
    private String sqlQuery;
    private long timestamp;

}
