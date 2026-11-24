package org.example;

import java.time.Duration;
import java.time.Instant;
import java.util.Objects;

public record Incident(
        String id,
        String service,
        Instant startedAt,
        Instant resolvedAt) {

    public Incident {
        Objects.requireNonNull(id, "Incident id must not be null");
        Objects.requireNonNull(service, "Service name must not be null");
        Objects.requireNonNull(startedAt, "startedAt must not be null");
        Objects.requireNonNull(resolvedAt, "resolvedAt must not be null");
        if (id.isBlank()) {
            throw new IllegalArgumentException("Incident id must not be empty");
        }
        if (service.isBlank()) {
            throw new IllegalArgumentException("Service name must not be empty");
        }
        if (resolvedAt.isBefore(startedAt)) {
            throw new IllegalArgumentException(
                    "resolvedAt (" + resolvedAt + ") cannot precede startedAt (" + startedAt + ")");
        }
    }

    public Duration duration() {
        return Duration.between(startedAt, resolvedAt);
    }
}
