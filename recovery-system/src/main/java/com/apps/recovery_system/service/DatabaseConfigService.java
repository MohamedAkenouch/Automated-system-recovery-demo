package com.apps.recovery_system.service;

import com.apps.recovery_system.model.DatabaseConfig;
import com.apps.recovery_system.repo.DatabaseConfigRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DatabaseConfigService {

    @Autowired
    private DatabaseConfigRepository repository;

    public List<DatabaseConfig> getAllConfigs() {
        return repository.findAll();
    }

    public Optional<DatabaseConfig> getConfigById(String id) {
        return repository.findById(id);
    }

    public DatabaseConfig addConfig(DatabaseConfig config) {
        return repository.save(config);
    }

    public DatabaseConfig updateConfig(DatabaseConfig config) {
        return repository.save(config);
    }

    public void deleteConfig(String id) {
        repository.deleteById(id);
    }
}
