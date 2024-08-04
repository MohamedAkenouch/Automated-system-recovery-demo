package com.apps.recovery_system.service;

public class SqlQueryGeneratorFactory {

    public static SqlQueryGenerator getSqlQueryGenerator(String databaseType) {
        switch (databaseType.toLowerCase()) {
            case "postgresql":
                return new PostgresSqlQueryGenerator();
            case "mysql":
                return new MySqlSqlQueryGenerator();
            default:
                throw new IllegalArgumentException("Unsupported database type: " + databaseType);
        }
    }
}