package org.example;

import java.time.Instant;
import java.util.Objects;

public final class IncidentParser {

    private static final int EXPECTED_FIELD_COUNT = 4;

    private IncidentParser() {
    }

    public static Incident parse(String line) {
        Objects.requireNonNull(line, "Input line must not be null");

        String[] fields = line.split(";", -1);
        if (fields.length != EXPECTED_FIELD_COUNT) {
            throw new IllegalArgumentException(
                    "Expected " + EXPECTED_FIELD_COUNT + " fields separated by ';', got " + fields.length);
        }

        String id = fields[0];
        String service = fields[1];
        String startedAtRaw = fields[2];
        String resolvedAtRaw = fields[3];

        if (id.isBlank()) {
            throw new IllegalArgumentException("Incident id must not be empty");
        }
        if (service.isBlank()) {
            throw new IllegalArgumentException("Service name must not be empty");
        }
        if (startedAtRaw.isBlank()) {
            throw new IllegalArgumentException("startedAt timestamp must not be empty");
        }
        if (resolvedAtRaw.isBlank()) {
            throw new IllegalArgumentException("resolvedAt timestamp must not be empty");
        }

        Instant startedAt = Instant.parse(startedAtRaw);
        Instant resolvedAt = Instant.parse(resolvedAtRaw);

        return new Incident(id, service, startedAt, resolvedAt);
    }
}
