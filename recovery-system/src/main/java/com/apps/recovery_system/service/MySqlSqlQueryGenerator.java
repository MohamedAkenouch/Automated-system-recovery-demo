package com.apps.recovery_system.service;

import com.apps.recovery_system.model.DebeziumEvent;

public class MySqlSqlQueryGenerator implements SqlQueryGenerator {

    @Override
    public String generateInsertQuery(DebeziumEvent.Data data, DebeziumEvent.Source source) {
        return String.format("INSERT INTO `%s`.`%s` (id, name, email) VALUES (%s, '%s', '%s');",
                source.getSchema(), source.getTable(), data.getId(), data.getName(), data.getEmail());
    }

    @Override
    public String generateUpdateQuery(DebeziumEvent.Data data, DebeziumEvent.Source source) {
        return String.format("UPDATE `%s`.`%s` SET name = '%s', email = '%s' WHERE id = %s;",
                source.getSchema(), source.getTable(), data.getName(), data.getEmail(), data.getId());
    }

    @Override
    public String generateDeleteQuery(DebeziumEvent.Data data, DebeziumEvent.Source source) {
        return String.format("DELETE FROM `%s`.`%s` WHERE id = %s;",
                source.getSchema(), source.getTable(), data.getId());
    }
}
