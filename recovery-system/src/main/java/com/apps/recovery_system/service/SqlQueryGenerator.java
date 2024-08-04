package com.apps.recovery_system.service;

import com.apps.recovery_system.model.DebeziumEvent;

public interface SqlQueryGenerator {
    String generateInsertQuery(DebeziumEvent.Data data, DebeziumEvent.Source source);
    String generateUpdateQuery(DebeziumEvent.Data data, DebeziumEvent.Source source);
    String generateDeleteQuery(DebeziumEvent.Data data, DebeziumEvent.Source source);
}