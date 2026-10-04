package com.example.code.challenge.finaly.portability;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class BackupService {

    public void backup() throws Exception {

        String source = "C:\\Data\\users.db";

        String target = "D:\\Backup\\users.bak";

        Files.copy(
                Paths.get(source),
                Paths.get(target),
                StandardCopyOption.REPLACE_EXISTING
        );

    }

}