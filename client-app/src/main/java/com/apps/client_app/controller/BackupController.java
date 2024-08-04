package com.apps.client_app.controller;

import com.apps.client_app.task.DatabaseBackupTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/backup")
public class BackupController {

    @Autowired
    private DatabaseBackupTask databaseBackupTask;

    @GetMapping
    public String backupDatabase() {
        databaseBackupTask.backupDatabase();
        return "Backup successful";
    }

}

