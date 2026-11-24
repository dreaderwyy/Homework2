package org.example;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;
import java.time.format.DateTimeParseException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class IncidentParserTest {

    @Test
    void shouldParseValidLineAndCompute150MinuteDuration() {
        Incident incident = IncidentParser.parse(
                "INC-1;billing;2024-03-30T22:30:00Z;2024-03-31T01:00:00Z");

        assertEquals("INC-1", incident.id());
        assertEquals("billing", incident.service());
        assertEquals(Instant.parse("2024-03-30T22:30:00Z"), incident.startedAt());
        assertEquals(Instant.parse("2024-03-31T01:00:00Z"), incident.resolvedAt());
        assertEquals(Duration.ofMinutes(150), incident.duration());
    }

    @Test
    void shouldAllowZeroDurationWhenStartEqualsResolved() {
        Incident incident = IncidentParser.parse(
                "INC-0;auth;2024-03-30T22:30:00Z;2024-03-30T22:30:00Z");

        assertEquals(Duration.ZERO, incident.duration());
    }

    @Test
    void shouldRejectInvalidTimestamp() {
        assertThrows(DateTimeParseException.class, () -> IncidentParser.parse(
                "INC-1;billing;2024-03-30T22:30:00;2024-03-31T01:00:00Z"));
        assertThrows(DateTimeParseException.class, () -> IncidentParser.parse(
                "INC-1;billing;2024-03-30T22:30:00Z;not-a-timestamp"));
        assertThrows(DateTimeParseException.class, () -> IncidentParser.parse(
                "INC-1;billing;2024-03-30;2024-03-31T01:00:00Z"));
    }

    @Test
    void shouldRejectEmptyIdAndService() {
        assertThrows(IllegalArgumentException.class, () -> IncidentParser.parse(
                ";billing;2024-03-30T22:30:00Z;2024-03-31T01:00:00Z"));
        assertThrows(IllegalArgumentException.class, () -> IncidentParser.parse(
                "   ;billing;2024-03-30T22:30:00Z;2024-03-31T01:00:00Z"));
        assertThrows(IllegalArgumentException.class, () -> IncidentParser.parse(
                "INC-1; ;2024-03-30T22:30:00Z;2024-03-31T01:00:00Z"));
    }

    @Test
    void shouldRejectResolvedBeforeStarted() {
        assertThrows(IllegalArgumentException.class, () -> IncidentParser.parse(
                "INC-1;billing;2024-03-31T01:00:00Z;2024-03-30T22:30:00Z"));
        assertThrows(IllegalArgumentException.class, () -> new Incident(
                "INC-1",
                "billing",
                Instant.parse("2024-03-31T01:00:00Z"),
                Instant.parse("2024-03-30T22:30:00Z")));
    }

    @Test
    void shouldRejectWrongFieldCountAndNullLine() {
        assertThrows(IllegalArgumentException.class, () -> IncidentParser.parse(
                "INC-1;billing;2024-03-30T22:30:00Z"));
        assertThrows(IllegalArgumentException.class, () -> IncidentParser.parse(
                "INC-1;billing;2024-03-30T22:30:00Z;2024-03-31T01:00:00Z;extra"));
        assertThrows(IllegalArgumentException.class, () -> IncidentParser.parse(
                "INC-1;billing;2024-03-30T22:30:00Z;2024-03-31T01:00:00Z;"));
        assertThrows(NullPointerException.class, () -> IncidentParser.parse(null));
    }
}
