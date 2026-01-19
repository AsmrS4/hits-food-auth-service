package com.example.user_service.utils;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.HashMap;
import java.util.Scanner;

@Component
@Slf4j
public class FeatureFlagsManager {
    private final HashMap<String, Boolean> flags = new HashMap<String, Boolean>();
    private final File envFile = findEnvFile();
    private long lastModified = 0;

    @PostConstruct
    public void start() {
        reload();
    }

    private File findEnvFile() {
        File currentDir = new File(".");

        for (int i = 0; i < 4; i++) {
            File envFile = new File(currentDir, ".env");
            if (envFile.exists()) return envFile;

            currentDir = currentDir.getParentFile();
            if (currentDir == null) break;
        }
        return new File(".env");
    }

    @Scheduled(fixedRate = 5000)
    public void reload() {
        synchronized (this) {
            try {
                if (!envFile.exists()) return;

                long modified = envFile.lastModified();
                if (modified <= lastModified) return;

                lastModified = modified;
                flags.clear();

                try (Scanner scanner = new Scanner(envFile)) {
                    while (scanner.hasNextLine()) {
                        String line = scanner.nextLine().trim();
                        if (line.contains("=") && !line.startsWith("#")) {
                            String[] parts = line.split("=", 2);
                            String key = parts[0].trim();
                            String value = parts[1].trim().replace("\"", "");
                            if ("true".equals(value)) {
                                setFlag(key, true);
                            } else if ("false".equals(value)) {
                                setFlag(key, false);
                            }
                        }
                    }
                }
                log.info("Reloaded {} flags from {}", flags.size(), envFile.getAbsolutePath());
            } catch (Exception e) {
                log.error("Error reloading flags", e);
            }
        }
    }

    private void setFlag(String key, Boolean value) {
        flags.put(key, value);
    }

    public boolean isEnabled(String key) {
        Boolean flagValue = flags.get(key);
        return flagValue !=null ? flagValue : false;
    }
}
