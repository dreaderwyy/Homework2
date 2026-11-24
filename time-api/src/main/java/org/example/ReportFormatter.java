package org.example;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Objects;

public final class ReportFormatter {

    private static final DateTimeFormatter WINDOW_FORMATTER =
            DateTimeFormatter.ofPattern("dd.MM.uuuu HH:mm XXX (VV)");
    // Space + en dash (U+2013) + space: " – "
    private static final String SEPARATOR = " \u2013 ";

    private ReportFormatter() {
    }

    public static String formatWindow(MaintenanceWindow window, ZoneId zone, Locale locale) {
        Objects.requireNonNull(window, "window must not be null");
        Objects.requireNonNull(zone, "zone must not be null");
        Objects.requireNonNull(locale, "locale must not be null");

        DateTimeFormatter formatter = WINDOW_FORMATTER
                .withLocale(locale)
                .withZone(zone);

        return formatter.format(window.start()) + SEPARATOR + formatter.format(window.end());
    }
}
