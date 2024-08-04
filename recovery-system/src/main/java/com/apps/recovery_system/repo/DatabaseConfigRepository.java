package com.apps.recovery_system.repo;

import com.apps.recovery_system.model.DatabaseConfig;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface DatabaseConfigRepository extends MongoRepository<DatabaseConfig, String> {
    List<DatabaseConfig> findAll();
}
