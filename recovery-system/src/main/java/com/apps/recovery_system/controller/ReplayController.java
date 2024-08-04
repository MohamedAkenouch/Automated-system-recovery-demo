package com.apps.recovery_system.controller;

import com.apps.recovery_system.helper.UrlParser;
import com.apps.recovery_system.model.Event;
import com.apps.recovery_system.model.ReplayRequest;
import com.apps.recovery_system.service.DatabaseService;
import com.apps.recovery_system.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.sql.Connection;
import java.sql.Statement;
import java.util.List;

@RestController
@RequestMapping("/api/replay")
public class ReplayController {

    private final EventService eventService;
    private final DatabaseService databaseService;

    @Autowired
    public ReplayController(EventService eventService, DatabaseService databaseService) {
        this.eventService = eventService;
        this.databaseService = databaseService;
    }

    @PostMapping
    public String replayEvents(@RequestBody ReplayRequest request) {
        List<Event> events = eventService.getAllEvents();

        try {
            String jdbcUrl = UrlParser.parseUrlToJDBC(request.getDatabaseUrl());
            Connection connection = databaseService.getConnection(jdbcUrl, request.getUsername(), request.getPassword());
            try (Statement statement = connection.createStatement()) {
                for (Event event : events) {
                    statement.execute(event.getSqlQuery());
                }
            }
            return "Events replayed successfully";
        } catch (Exception e) {
            // Log the exception
            e.printStackTrace();
            return "Error replaying events: " + e.getMessage();
        }
    }
}