package org.example;

import java.time.Duration;
import java.time.Instant;
import java.util.Objects;

public record MaintenanceWindow(Instant start, Instant end) {

    public MaintenanceWindow {
        Objects.requireNonNull(start, "Window start must not be null");
        Objects.requireNonNull(end, "Window end must not be null");
        if (!end.isAfter(start)) {
            throw new IllegalArgumentException(
                    "Window end (" + end + ") must be after start (" + start + ")");
        }
    }

    public Duration duration() {
        return Duration.between(start, end);
    }
}
