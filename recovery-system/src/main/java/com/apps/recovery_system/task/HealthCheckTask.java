package com.apps.recovery_system.task;

import com.apps.recovery_system.model.DatabaseConfig;
import com.apps.recovery_system.model.DatabaseConfigPayload;
import com.apps.recovery_system.repo.DatabaseConfigRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.Status;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;


import java.net.URI;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

@Component
public class HealthCheckTask {

    @Value("${jenkins.process.api.url}")
    private String apiUrl;
    @Value("${jenkins.auth}")
    private String auth;
    @Value("${jenkins.crumb}")
    private String crumb;

    @Autowired
    private DatabaseConfigRepository databaseConfigRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Scheduled(fixedRate = 900000) // Run every 15 minutes (900000 milliseconds)
    public void performHealthCheck() {
        List<DatabaseConfig> databases = databaseConfigRepository.findAll();
        for (DatabaseConfig config : databases) {
            Health health = checkDatabaseHealth(config);
            if (health.getStatus().getCode().equals(Status.DOWN)) {
                triggerJenkinsPipeline(config);
            }
        }
    }

    private Health checkDatabaseHealth(DatabaseConfig config) {
        try (Connection connection = DriverManager.getConnection(config.getUrl(), config.getUser(), config.getPassword())) {
            if (connection.isValid(2)) {
                return Health.up().withDetail("database", "Database is up and running").build();
            } else {
                return Health.down().withDetail("database", "Database is not reachable").build();
            }
        } catch (SQLException e) {
            return Health.down(e).withDetail("database", "Database is not reachable").build();
        }
    }

    private void triggerJenkinsPipeline(DatabaseConfig config) {

        try {
            URI uri = new URI(config.getUrl().replace("jdbc:", ""));
            DatabaseConfigPayload databaseConfigPayload = new DatabaseConfigPayload(config.getEngine(),uri.getPath().substring(1),config.getUser(),config.getPassword(),uri.getPort());
            String payload = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(databaseConfigPayload);

            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Jenkins-Crumb", this.crumb);
            headers.setBasicAuth(this.auth);

            HttpEntity<String> requestEntity = new HttpEntity<>(payload, headers);
            restTemplate.postForObject(this.apiUrl, requestEntity, String.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String createPayload(DatabaseConfigPayload config) throws Exception {
        return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(config);
    }
}
