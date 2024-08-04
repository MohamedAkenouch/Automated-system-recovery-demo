package com.apps.recovery_system.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "databases")
@Getter
@Setter
public class DatabaseConfig {

    @Id
    private String id;
    private String url;
    private String user;
    private String password;
    private String engine;
}
