package com.example.codeChallenge.excercise.exam1.portability.me;





import java.io.IOException;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class BackupService {
    private final BackupConfiguration backupConfiguration;

    public BackupService(BackupConfiguration backupConfiguration) {
        this.backupConfiguration = backupConfiguration;
    }

    public void backup() throws Exception {
        try {
            try {
                Files.copy(
                        this.backupConfiguration.getSourceFilePath(),
                        this.backupConfiguration.getTempFilePath(),
                        StandardCopyOption.REPLACE_EXISTING
                );
            } catch (IOException e) {
                throw new RuntimeException("An exception occurred in copy file to temp.", e);
            }


            try {
                Files.move(
                        this.backupConfiguration.getTempFilePath(),
                        this.backupConfiguration.getTargetFilePath(),
                        StandardCopyOption.ATOMIC_MOVE
                );
            } catch (AtomicMoveNotSupportedException e) {
                Files.move(
                        this.backupConfiguration.getTempFilePath(),
                        this.backupConfiguration.getTargetFilePath(),
                        StandardCopyOption.REPLACE_EXISTING
                );
            }
        } catch (RuntimeException e) {
            Files.delete(this.backupConfiguration.getTempFilePath());
            throw new RuntimeException("An exception occurred in backup method.", e);
        }

    }
}