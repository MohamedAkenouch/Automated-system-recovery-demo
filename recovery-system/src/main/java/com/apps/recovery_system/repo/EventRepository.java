package com.apps.recovery_system.repo;

import com.apps.recovery_system.model.Event;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface EventRepository extends MongoRepository<Event, String> {
}
