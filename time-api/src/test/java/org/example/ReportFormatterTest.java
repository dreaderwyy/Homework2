package org.example;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.ZoneId;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ReportFormatterTest {

    @Test
    void shouldFormatMaintenanceWindowExactly() {
        MaintenanceWindow window = new MaintenanceWindow(
                Instant.parse("2024-03-31T01:00:00Z"),
                Instant.parse("2024-03-31T02:00:00Z"));

        String formatted = ReportFormatter.formatWindow(
                window,
                ZoneId.of("Europe/Berlin"),
                Locale.forLanguageTag("ru-RU"));

        assertEquals(
                "31.03.2024 03:00 +02:00 (Europe/Berlin) \u2013 31.03.2024 04:00 +02:00 (Europe/Berlin)",
                formatted);
    }

    @Test
    void shouldRejectNullArguments() {
        MaintenanceWindow window = new MaintenanceWindow(
                Instant.parse("2024-03-31T01:00:00Z"),
                Instant.parse("2024-03-31T02:00:00Z"));

        assertThrows(NullPointerException.class,
                () -> ReportFormatter.formatWindow(null, ZoneId.of("Europe/Berlin"), Locale.US));
        assertThrows(NullPointerException.class,
                () -> ReportFormatter.formatWindow(window, null, Locale.US));
        assertThrows(NullPointerException.class,
                () -> ReportFormatter.formatWindow(window, ZoneId.of("Europe/Berlin"), null));
    }
}
