package com.apps.recovery_system.service;

import com.apps.recovery_system.model.DebeziumEvent;
import com.apps.recovery_system.model.Event;
import com.apps.recovery_system.repo.EventRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class EventProcessingService {

    @Autowired
    private EventRepository eventRepository;

    private ObjectMapper objectMapper = new ObjectMapper();

    @KafkaListener(topicPattern = "client.*", groupId = "recovery-group")
    public void consume(String message) {
        try {
            DebeziumEvent event = objectMapper.readValue(message, DebeziumEvent.class);
            String databaseType = event.getSource().getConnector();
            SqlQueryGenerator sqlQueryGenerator = SqlQueryGeneratorFactory.getSqlQueryGenerator(databaseType);
            Event storedEvent = convertToStoredEvent(event, sqlQueryGenerator, databaseType);
            eventRepository.save(storedEvent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Event convertToStoredEvent(DebeziumEvent debeziumEvent, SqlQueryGenerator sqlQueryGenerator, String databaseType) {
        Event event = new Event();
        event.setDatabaseType(databaseType);
        event.setDatabase(debeziumEvent.getSource().getDb());
        event.setSchema(debeziumEvent.getSource().getSchema());
        event.setTable(debeziumEvent.getSource().getTable());
        event.setOperation(debeziumEvent.getOp());
        event.setTimestamp(debeziumEvent.getTs_ms());

        String sqlQuery;
        switch (debeziumEvent.getOp()) {
            case "c":
                sqlQuery = sqlQueryGenerator.generateInsertQuery(debeziumEvent.getAfter(), debeziumEvent.getSource());
                break;
            case "u":
                sqlQuery = sqlQueryGenerator.generateUpdateQuery(debeziumEvent.getAfter(), debeziumEvent.getSource());
                break;
            case "d":
                sqlQuery = sqlQueryGenerator.generateDeleteQuery(debeziumEvent.getBefore(), debeziumEvent.getSource());
                break;
            default:
                sqlQuery = "";
                break;
        }
        event.setSqlQuery(sqlQuery);
        return event;
    }
}
