package org.example;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class IncidentAnalyticsTest {

    @Test
    void shouldHandleEmptyList() {
        List<Incident> empty = List.of();

        assertEquals(Duration.ZERO, IncidentAnalytics.totalDowntime(empty));
        assertEquals(Duration.ZERO, IncidentAnalytics.averageDowntime(empty));
        assertEquals(Optional.empty(), IncidentAnalytics.longestIncident(empty));
        assertTrue(IncidentAnalytics.groupByStartDate(empty, ZoneId.of("Europe/Berlin")).isEmpty());
    }

    @Test
    void shouldCalculateTotalAndAverageWithRoundingDown() {
        Instant base = Instant.parse("2024-03-30T10:00:00Z");
        // Total = 2 seconds across 3 incidents -> 0.66666666666... s -> rounded down to 666_666_666 ns
        List<Incident> incidents = List.of(
                new Incident("INC-1", "billing", base, base.plusSeconds(1)),
                new Incident("INC-2", "billing", base, base.plusSeconds(1)),
                new Incident("INC-3", "billing", base, base)
        );

        assertEquals(Duration.ofSeconds(2), IncidentAnalytics.totalDowntime(incidents));
        assertEquals(Duration.ofNanos(666_666_666L), IncidentAnalytics.averageDowntime(incidents));
    }

    @Test
    void shouldComputeAverageWithoutLongNanosecondOverflow() {
        // 3 incidents of 200 years each -> sum is ~600 years, which exceeds Long.MAX_VALUE nanoseconds (~292 years)
        Duration twoHundredYears = Duration.ofDays(200L * 365L);
        Instant start = Instant.parse("2000-01-01T00:00:00Z");
        List<Incident> incidents = List.of(
                new Incident("INC-1", "core", start, start.plus(twoHundredYears)),
                new Incident("INC-2", "core", start, start.plus(twoHundredYears).plusNanos(1)),
                new Incident("INC-3", "core", start, start.plus(twoHundredYears).plusNanos(1))
        );

        // Sum = 3 * 200y + 2ns; divided by 3 with floor rounding = 200y + 0ns
        assertEquals(twoHundredYears, IncidentAnalytics.averageDowntime(incidents));
    }

    @Test
    void shouldSelectLongestIncidentWithTieBreakers() {
        Instant t1 = Instant.parse("2024-03-30T10:00:00Z");
        Instant t2 = Instant.parse("2024-03-30T11:00:00Z");

        Incident shortInc = new Incident("INC-0", "api", t1, t1.plus(Duration.ofMinutes(10)));
        Incident laterStart = new Incident("INC-1", "api", t2, t2.plus(Duration.ofMinutes(30)));
        Incident earlierStartLargerId = new Incident("INC-B", "api", t1, t1.plus(Duration.ofMinutes(30)));
        Incident earlierStartSmallerId = new Incident("INC-A", "api", t1, t1.plus(Duration.ofMinutes(30)));

        List<Incident> mutableInput = new ArrayList<>(
                List.of(shortInc, laterStart, earlierStartLargerId, earlierStartSmallerId));
        List<Incident> snapshot = List.copyOf(mutableInput);

        Optional<Incident> longest = IncidentAnalytics.longestIncident(mutableInput);

        assertEquals(Optional.of(earlierStartSmallerId), longest);
        assertEquals(snapshot, mutableInput);
    }

    @Test
    void shouldGroupByStartDateInEuropeBerlinWhereLocalDateDiffersFromUtc() {
        ZoneId berlin = ZoneId.of("Europe/Berlin");

        // In UTC both INC-1 and INC-2 start on 2024-03-30;
        // in Europe/Berlin (+01:00): INC-1 is 2024-03-30 23:30, INC-2 is 2024-03-31 00:30
        Incident inc1 = IncidentParser.parse("INC-1;billing;2024-03-30T22:30:00Z;2024-03-31T01:00:00Z");
        Incident inc2 = IncidentParser.parse("INC-2;gateway;2024-03-30T23:30:00Z;2024-03-31T00:15:00Z");
        Incident inc3 = IncidentParser.parse("INC-0;gateway;2024-03-30T23:30:00Z;2024-03-31T00:10:00Z");
        Incident inc4 = IncidentParser.parse("INC-3;auth;2024-03-30T23:45:00Z;2024-03-31T00:05:00Z");

        List<Incident> mutableInput = new ArrayList<>(List.of(inc4, inc2, inc1, inc3));
        List<Incident> snapshot = List.copyOf(mutableInput);

        Map<LocalDate, List<Incident>> grouped = IncidentAnalytics.groupByStartDate(mutableInput, berlin);

        assertEquals(snapshot, mutableInput);
        assertEquals(
                List.of(LocalDate.of(2024, 3, 30), LocalDate.of(2024, 3, 31)),
                List.copyOf(grouped.keySet()));
        assertEquals(List.of(inc1), grouped.get(LocalDate.of(2024, 3, 30)));
        assertEquals(List.of(inc3, inc2, inc4), grouped.get(LocalDate.of(2024, 3, 31)));
    }

    @Test
    void shouldRejectNullArguments() {
        assertThrows(NullPointerException.class, () -> IncidentAnalytics.totalDowntime(null));
        assertThrows(NullPointerException.class, () -> IncidentAnalytics.averageDowntime(null));
        assertThrows(NullPointerException.class, () -> IncidentAnalytics.longestIncident(null));
        assertThrows(NullPointerException.class, () -> IncidentAnalytics.groupByStartDate(null, ZoneId.of("UTC")));
        assertThrows(NullPointerException.class, () -> IncidentAnalytics.groupByStartDate(List.of(), null));

        List<Incident> withNullElement = new ArrayList<>();
        withNullElement.add(null);
        assertThrows(NullPointerException.class, () -> IncidentAnalytics.totalDowntime(withNullElement));
    }
}
