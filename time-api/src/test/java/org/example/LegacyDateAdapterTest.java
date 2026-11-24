package org.example;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.GregorianCalendar;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LegacyDateAdapterTest {

    @Test
    void shouldRoundTripDateAndInstant() {
        Instant originalInstant = Instant.parse("2024-03-30T22:30:00.123Z");

        Date legacyDate = LegacyDateAdapter.toDate(originalInstant);
        Instant restoredInstant = LegacyDateAdapter.toInstant(legacyDate);

        assertEquals(originalInstant, restoredInstant);
        assertEquals(originalInstant.toEpochMilli(), legacyDate.getTime());

        Date roundTripDate = LegacyDateAdapter.toDate(restoredInstant);
        assertEquals(legacyDate, roundTripDate);
    }

    @Test
    void shouldRoundTripGregorianCalendarAndZonedDateTimePreservingZone() {
        ZoneId berlin = ZoneId.of("Europe/Berlin");
        ZonedDateTime original = ZonedDateTime.of(2024, 3, 31, 3, 30, 0, 0, berlin);

        GregorianCalendar calendar = LegacyDateAdapter.toGregorianCalendar(original);
        ZonedDateTime restored = LegacyDateAdapter.toZonedDateTime(calendar);

        assertTrue(original.isEqual(restored));
        assertEquals(original.toInstant(), restored.toInstant());
        assertEquals(berlin, restored.getZone());
        assertEquals(berlin, calendar.getTimeZone().toZoneId());
    }

    @Test
    void shouldRejectNullArguments() {
        assertThrows(NullPointerException.class, () -> LegacyDateAdapter.toInstant(null));
        assertThrows(NullPointerException.class, () -> LegacyDateAdapter.toDate(null));
        assertThrows(NullPointerException.class, () -> LegacyDateAdapter.toZonedDateTime(null));
        assertThrows(NullPointerException.class, () -> LegacyDateAdapter.toGregorianCalendar(null));
    }
}
