package org.example;

import java.time.Clock;
import java.time.DateTimeException;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class MaintenanceScheduler {

    private final Clock clock;

    public MaintenanceScheduler(Clock clock) {
        this.clock = Objects.requireNonNull(clock, "clock must not be null");
    }

    public MaintenanceWindow nextWindow(
            LocalDate date, LocalTime localStart, Duration duration,
            ZoneId zone, OverlapPolicy policy) {
        Objects.requireNonNull(date, "date must not be null");
        Objects.requireNonNull(localStart, "localStart must not be null");
        validateDuration(duration);
        Objects.requireNonNull(zone, "zone must not be null");
        Objects.requireNonNull(policy, "policy must not be null");

        LocalDateTime localDateTime = LocalDateTime.of(date, localStart);
        List<ZoneOffset> validOffsets = zone.getRules().getValidOffsets(localDateTime);
        if (validOffsets.isEmpty()) {
            throw new DateTimeException(
                    "Local date-time " + localDateTime + " does not exist in zone " + zone + " (DST gap)");
        }

        ZonedDateTime zonedStart = localDateTime.atZone(zone);
        if (validOffsets.size() > 1) {
            zonedStart = switch (policy) {
                case EARLIER -> zonedStart.withEarlierOffsetAtOverlap();
                case LATER -> zonedStart.withLaterOffsetAtOverlap();
            };
        }

        Instant start = zonedStart.toInstant();
        Instant end = start.plus(duration);
        return new MaintenanceWindow(start, end);
    }

    public List<MaintenanceWindow> upcomingWindows(
            int days, LocalTime localStart, Duration duration,
            ZoneId zone, OverlapPolicy policy) {
        if (days < 0) {
            throw new IllegalArgumentException("days must not be negative: " + days);
        }
        Objects.requireNonNull(localStart, "localStart must not be null");
        validateDuration(duration);
        Objects.requireNonNull(zone, "zone must not be null");
        Objects.requireNonNull(policy, "policy must not be null");

        if (days == 0) {
            return List.of();
        }

        LocalDate startDate = LocalDate.now(clock.withZone(zone));
        List<MaintenanceWindow> windows = new ArrayList<>(days);
        for (int i = 0; i < days; i++) {
            LocalDate currentDate = startDate.plusDays(i);
            windows.add(nextWindow(currentDate, localStart, duration, zone, policy));
        }
        return List.copyOf(windows);
    }

    private static void validateDuration(Duration duration) {
        Objects.requireNonNull(duration, "duration must not be null");
        if (duration.isZero() || duration.isNegative()) {
            throw new IllegalArgumentException("duration must be positive: " + duration);
        }
    }
}
