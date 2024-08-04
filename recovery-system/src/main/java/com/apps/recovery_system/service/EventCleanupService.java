package com.apps.recovery_system.service;

import com.apps.recovery_system.model.BackupEvent;
import com.apps.recovery_system.model.Event;
import com.apps.recovery_system.repo.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

@Service
public class EventCleanupService {

    @Autowired
    private MongoTemplate mongoTemplate;

    private static final String TOPIC = "backup-events";

    @KafkaListener(topics = TOPIC, groupId = "recovery-group")
    public void listen(BackupEvent event) {
        long timestamp = event.getTimestamp();
        deleteOldEvents(event.getDatabaseName(), timestamp);
    }

    private void deleteOldEvents(String databaseName, long timestamp) {
        mongoTemplate.remove(
                query(where("databaseName").is(databaseName)
                        .and("eventTime").lt(timestamp)),
                Event.class
        );
    }
}
