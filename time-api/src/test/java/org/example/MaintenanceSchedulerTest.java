package org.example;

import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.DateTimeException;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MaintenanceSchedulerTest {

    private static final ZoneId BERLIN = ZoneId.of("Europe/Berlin");

    @Test
    void shouldRejectNonExistentLocalTimeInDstGap() {
        Clock clock = Clock.fixed(Instant.parse("2024-03-30T00:00:00Z"), ZoneOffset.UTC);
        MaintenanceScheduler scheduler = new MaintenanceScheduler(clock);

        assertThrows(DateTimeException.class, () -> scheduler.nextWindow(
                LocalDate.of(2024, 3, 31),
                LocalTime.of(2, 30),
                Duration.ofMinutes(30),
                BERLIN,
                OverlapPolicy.EARLIER));
    }

    @Test
    void shouldResolveDstOverlapAccordingToPolicy() {
        Clock clock = Clock.fixed(Instant.parse("2024-10-27T00:00:00Z"), ZoneOffset.UTC);
        MaintenanceScheduler scheduler = new MaintenanceScheduler(clock);

        LocalDate overlapDate = LocalDate.of(2024, 10, 27);
        LocalTime overlapTime = LocalTime.of(2, 30);
        Duration duration = Duration.ofMinutes(30);

        MaintenanceWindow earlier = scheduler.nextWindow(
                overlapDate, overlapTime, duration, BERLIN, OverlapPolicy.EARLIER);
        MaintenanceWindow later = scheduler.nextWindow(
                overlapDate, overlapTime, duration, BERLIN, OverlapPolicy.LATER);

        ZonedDateTime earlierZoned = earlier.start().atZone(BERLIN);
        ZonedDateTime laterZoned = later.start().atZone(BERLIN);

        assertEquals(ZoneOffset.ofHours(2), earlierZoned.getOffset());
        assertEquals(ZoneOffset.ofHours(1), laterZoned.getOffset());
        assertEquals(Instant.parse("2024-10-27T00:30:00Z"), earlier.start());
        assertEquals(Instant.parse("2024-10-27T01:30:00Z"), later.start());
        assertEquals(Duration.ofHours(1), Duration.between(earlier.start(), later.start()));
        assertEquals(duration, earlier.duration());
        assertEquals(duration, later.duration());
    }

    @Test
    void shouldScheduleDailyWindowsAcrossSpringForwardWith23HourDifferenceAndRepeatDeterministically() {
        Clock fixedClock = Clock.fixed(Instant.parse("2024-03-30T00:00:00Z"), ZoneOffset.UTC);
        MaintenanceScheduler scheduler = new MaintenanceScheduler(fixedClock);

        List<MaintenanceWindow> firstCall = scheduler.upcomingWindows(
                2, LocalTime.of(3, 30), Duration.ofMinutes(30), BERLIN, OverlapPolicy.EARLIER);
        List<MaintenanceWindow> secondCall = scheduler.upcomingWindows(
                2, LocalTime.of(3, 30), Duration.ofMinutes(30), BERLIN, OverlapPolicy.EARLIER);

        assertEquals(2, firstCall.size());
        assertEquals(firstCall, secondCall);

        ZonedDateTime firstStart = firstCall.get(0).start().atZone(BERLIN);
        ZonedDateTime secondStart = firstCall.get(1).start().atZone(BERLIN);

        assertEquals(LocalDate.of(2024, 3, 30), firstStart.toLocalDate());
        assertEquals(LocalDate.of(2024, 3, 31), secondStart.toLocalDate());
        assertEquals(LocalTime.of(3, 30), firstStart.toLocalTime());
        assertEquals(LocalTime.of(3, 30), secondStart.toLocalTime());
        assertEquals(Duration.ofHours(23), Duration.between(firstCall.get(0).start(), firstCall.get(1).start()));
        assertEquals(Duration.ofMinutes(30), firstCall.get(0).duration());
        assertEquals(Duration.ofMinutes(30), firstCall.get(1).duration());
    }

    @Test
    void shouldScheduleDailyWindowsAcrossFallBackWith25HourDifference() {
        Clock fixedClock = Clock.fixed(Instant.parse("2024-10-26T00:00:00Z"), ZoneOffset.UTC);
        MaintenanceScheduler scheduler = new MaintenanceScheduler(fixedClock);

        List<MaintenanceWindow> windows = scheduler.upcomingWindows(
                2, LocalTime.of(3, 30), Duration.ofMinutes(30), BERLIN, OverlapPolicy.EARLIER);

        assertEquals(2, windows.size());
        assertEquals(Duration.ofHours(25), Duration.between(windows.get(0).start(), windows.get(1).start()));
    }

    @Test
    void shouldHandleZeroDaysAndRejectInvalidParameters() {
        Clock fixedClock = Clock.fixed(Instant.parse("2024-03-30T00:00:00Z"), ZoneOffset.UTC);
        MaintenanceScheduler scheduler = new MaintenanceScheduler(fixedClock);

        assertTrue(scheduler.upcomingWindows(
                0, LocalTime.of(3, 30), Duration.ofMinutes(30), BERLIN, OverlapPolicy.EARLIER).isEmpty());

        assertThrows(IllegalArgumentException.class, () -> scheduler.upcomingWindows(
                -1, LocalTime.of(3, 30), Duration.ofMinutes(30), BERLIN, OverlapPolicy.EARLIER));
        assertThrows(IllegalArgumentException.class, () -> scheduler.nextWindow(
                LocalDate.of(2024, 3, 30), LocalTime.of(3, 30), Duration.ZERO, BERLIN, OverlapPolicy.EARLIER));
        assertThrows(IllegalArgumentException.class, () -> scheduler.nextWindow(
                LocalDate.of(2024, 3, 30), LocalTime.of(3, 30), Duration.ofMinutes(-5), BERLIN, OverlapPolicy.EARLIER));
        assertThrows(NullPointerException.class, () -> new MaintenanceScheduler(null));
    }
}
