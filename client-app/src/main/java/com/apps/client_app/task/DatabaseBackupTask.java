package com.apps.client_app.task;

import com.apps.client_app.model.BackupEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Component
public class DatabaseBackupTask {

    @Value("${spring.datasource.url}")
    private String dbUrl;

    @Value("${spring.datasource.username}")
    private String dbUser;

    @Value("${spring.datasource.password}")
    private String dbPassword;

    @Value("${backup.local.directory}")
    private String localBackupDirectory;

    @Value("${backup.remote.directory}")
    private String remoteBackupDirectory;

    @Value("${destination.type}")
    private String destinationType;

    @Value("${destination.address}")
    private String destinationAddress;

    @Value("${destination.user}")
    private String destinationUser;

    @Value("${destination.key.path}")
    private String destinationKeyPath;

    @Autowired
    private KafkaTemplate<String, BackupEvent> kafkaTemplate;

    private static final String TOPIC = "backup-events";
    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");

    @Scheduled(cron = "0 0 2 * * ?") // Schedule to run daily at 2 AM
    public void backupDatabase() {
        long time = LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        String timestamp = dateTimeFormatter.format(LocalDateTime.now());
        String localBackupFileName = Paths.get(localBackupDirectory, "backup_" + timestamp + ".sql").toString();

        String[] urlParts = dbUrl.split("://")[1].split(":");
        String host = urlParts[0];
        String[] portAndDb = urlParts[1].split("/");
        String port = portAndDb[0];
        String dbName = portAndDb[1];

        String backupCommand = String.format(
                "pg_dump -h %s -p %s -U %s -d %s -F c -f %s",
                host, port, dbUser, dbName, localBackupFileName
        );

        ProcessBuilder processBuilder = new ProcessBuilder("sh", "-c", backupCommand);
        processBuilder.environment().put("PGPASSWORD", dbPassword);

        try {
            Process process = processBuilder.start();
            int processComplete = process.waitFor();

            if (processComplete == 0) {
                System.out.println("Backup successful: " + localBackupFileName);

                transferBackup(localBackupFileName, dbName);
                BackupEvent backupEvent = new BackupEvent(dbName, time);
                kafkaTemplate.send(TOPIC, backupEvent);
            } else {
                System.err.println("Backup failed.");
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void transferBackup(String localBackupFileName, String dbName) {
        String remoteBackupPath = remoteBackupDirectory.replace("${db.name}", dbName);
        String checkDirCommand;
        String createDirCommand;
        String transferCommand;

        if ("container".equals(destinationType)) {
            checkDirCommand = String.format("docker exec %s test -d %s", destinationAddress, remoteBackupPath);
            createDirCommand = String.format("docker exec %s mkdir -p %s", destinationAddress, remoteBackupPath);
            transferCommand = String.format("docker cp %s %s:%s", localBackupFileName, destinationAddress, remoteBackupPath);
        } else if ("server".equals(destinationType)) {
            checkDirCommand = String.format("ssh -i %s %s@%s 'test -d %s'", destinationKeyPath, destinationUser, destinationAddress, remoteBackupPath);
            createDirCommand = String.format("ssh -i %s %s@%s 'mkdir -p %s'", destinationKeyPath, destinationUser, destinationAddress, remoteBackupPath);
            transferCommand = String.format("scp -i %s %s %s@%s:%s", destinationKeyPath, localBackupFileName, destinationUser, destinationAddress, remoteBackupPath);
        } else {
            throw new IllegalArgumentException("Invalid destination type: " + destinationType);
        }

        try {

            Process checkDirProcess = Runtime.getRuntime().exec(checkDirCommand);
            int checkDirProcessComplete = checkDirProcess.waitFor();

            if (checkDirProcessComplete != 0) {
                Process createDirProcess = Runtime.getRuntime().exec(createDirCommand);
                int createDirProcessComplete = createDirProcess.waitFor();

                if (createDirProcessComplete == 0) {
                    System.out.println("Remote directory created: " + remoteBackupPath);
                } else {
                    System.err.println("Failed to create remote directory.");
                    return;
                }
            }

            Process transferProcess = Runtime.getRuntime().exec(transferCommand);
            int transferProcessComplete = transferProcess.waitFor();

            if (transferProcessComplete == 0) {
                System.out.println("Backup transferred to " + destinationType + ": " + remoteBackupPath);
            } else {
                System.err.println("Failed to transfer backup to " + destinationType + ".");
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

}