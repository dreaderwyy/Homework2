package org.example;

import java.time.Duration;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.TreeMap;
import java.util.stream.Collectors;

public final class IncidentAnalytics {

    private static final Comparator<Incident> BY_START_THEN_ID = Comparator
            .comparing(Incident::startedAt)
            .thenComparing(Incident::id);

    private static final Comparator<Incident> LONGEST_FIRST = Comparator
            .comparing(Incident::duration, Comparator.reverseOrder())
            .thenComparing(Incident::startedAt)
            .thenComparing(Incident::id);

    private IncidentAnalytics() {
    }

    public static Duration totalDowntime(List<Incident> incidents) {
        validateIncidents(incidents);
        return sumDurations(incidents);
    }

    public static Duration averageDowntime(List<Incident> incidents) {
        validateIncidents(incidents);
        if (incidents.isEmpty()) {
            return Duration.ZERO;
        }
        return sumDurations(incidents).dividedBy(incidents.size());
    }

    public static Optional<Incident> longestIncident(List<Incident> incidents) {
        validateIncidents(incidents);
        return incidents.stream().min(LONGEST_FIRST);
    }

    public static Map<LocalDate, List<Incident>> groupByStartDate(
            List<Incident> incidents, ZoneId zone) {
        validateIncidents(incidents);
        Objects.requireNonNull(zone, "zone must not be null");

        return incidents.stream()
                .sorted(BY_START_THEN_ID)
                .collect(Collectors.groupingBy(
                        incident -> incident.startedAt().atZone(zone).toLocalDate(),
                        TreeMap::new,
                        Collectors.toUnmodifiableList()
                ));
    }

    private static Duration sumDurations(List<Incident> incidents) {
        return incidents.stream()
                .map(Incident::duration)
                .reduce(Duration.ZERO, Duration::plus);
    }

    private static void validateIncidents(List<Incident> incidents) {
        Objects.requireNonNull(incidents, "incidents list must not be null");
        for (Incident incident : incidents) {
            Objects.requireNonNull(incident, "Incident element must not be null");
        }
    }
}
