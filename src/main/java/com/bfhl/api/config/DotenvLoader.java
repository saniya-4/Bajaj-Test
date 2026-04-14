package com.bfhl.api.config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public final class DotenvLoader {

    private DotenvLoader() {}

    public static void loadIfPresent() {
        // Spring Boot does not load .env by default. We load it to preserve the original Node behavior.
        Path envPath = findDotenvPath();
        if (envPath == null) {
            return;
        }

        List<String> lines;
        try {
            lines = Files.readAllLines(envPath);
        } catch (IOException e) {
            return;
        }

        for (String line : lines) {
            if (line == null) continue;
            String trimmed = line.trim();
            if (trimmed.isEmpty() || trimmed.startsWith("#")) continue;

            int eq = trimmed.indexOf('=');
            if (eq <= 0) continue;

            String key = trimmed.substring(0, eq).trim();
            String value = trimmed.substring(eq + 1).trim();

            value = stripOptionalQuotes(value);

            // Only set if not already provided via OS env / JVM args.
            if (System.getenv(key) == null && System.getProperty(key) == null) {
                System.setProperty(key, value);
            }
        }
    }

    private static Path findDotenvPath() {
        // Common cases:
        // - running from bfhl-api directory => .env in user.dir
        // - running from workspace root => bfhl-api/.env exists
        Path cwd = Path.of(System.getProperty("user.dir", ".")).toAbsolutePath().normalize();
        Path direct = cwd.resolve(".env");
        if (Files.exists(direct)) return direct;

        Path nested = cwd.resolve("bfhl-api").resolve(".env");
        if (Files.exists(nested)) return nested;

        return null;
    }

    private static String stripOptionalQuotes(String value) {
        if (value == null || value.length() < 2) return value;
        if ((value.startsWith("\"") && value.endsWith("\"")) || (value.startsWith("'") && value.endsWith("'"))) {
            return value.substring(1, value.length() - 1);
        }
        return value;
    }
}

