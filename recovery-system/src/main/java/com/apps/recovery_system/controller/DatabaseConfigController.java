package com.apps.recovery_system.controller;

import com.apps.recovery_system.model.DatabaseConfig;
import com.apps.recovery_system.service.DatabaseConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/database-configs")
public class DatabaseConfigController {

    @Autowired
    private DatabaseConfigService service;

    @GetMapping
    public List<DatabaseConfig> getAllConfigs() {
        return service.getAllConfigs();
    }

    @GetMapping("/{id}")
    public ResponseEntity<DatabaseConfig> getConfigById(@PathVariable String id) {
        return service.getConfigById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public DatabaseConfig addConfig(@RequestBody DatabaseConfig config) {
        return service.addConfig(config);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DatabaseConfig> updateConfig(@PathVariable String id, @RequestBody DatabaseConfig config) {
        return service.getConfigById(id)
                .map(existingConfig -> {
                    config.setId(id);
                    return ResponseEntity.ok(service.updateConfig(config));
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteConfig(@PathVariable String id) {
        return service.getConfigById(id)
                .map(existingConfig -> {
                    service.deleteConfig(id);
                    return ResponseEntity.ok().<Void>build();
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
