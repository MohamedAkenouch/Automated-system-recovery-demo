package com.apps.recovery_system.model;

import lombok.Data;

@Data
public class DebeziumEvent {

    private Data before;
    private Data after;
    private Source source;
    private String op;
    private long ts_ms;
    private Transaction transaction;

    @lombok.Data
    public static class Data {
        private String id;
        private String name;
        private String email;
    }

    @lombok.Data
    public static class Source {
        private String version;
        private String connector;
        private String name;
        private long ts_ms;
        private boolean snapshot;
        private String db;
        private String schema;
        private String table;
        private long txId;
    }

    @lombok.Data
    public static class Transaction {
        private long id;
        private String status;
    }
}
