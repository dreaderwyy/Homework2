package org.example;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Locale;

public class Main {

    public static void main(String[] args) {
        Incident inc1 = IncidentParser.parse("INC-1;billing;2024-03-30T22:30:00Z;2024-03-31T01:00:00Z");
        Incident inc2 = IncidentParser.parse("INC-2;gateway;2024-03-30T23:30:00Z;2024-03-31T00:15:00Z");
        List<Incident> incidents = List.of(inc1, inc2);

        ZoneId berlin = ZoneId.of("Europe/Berlin");
        System.out.println("Total downtime: " + IncidentAnalytics.totalDowntime(incidents));
        System.out.println("Average downtime: " + IncidentAnalytics.averageDowntime(incidents));
        System.out.println("Longest incident: " + IncidentAnalytics.longestIncident(incidents));
        System.out.println("Grouped by Berlin date: " + IncidentAnalytics.groupByStartDate(incidents, berlin));

        Clock clock = Clock.fixed(Instant.parse("2024-03-30T00:00:00Z"), ZoneOffset.UTC);
        MaintenanceScheduler scheduler = new MaintenanceScheduler(clock);
        List<MaintenanceWindow> windows = scheduler.upcomingWindows(
                2, LocalTime.of(3, 30), Duration.ofMinutes(30), berlin, OverlapPolicy.EARLIER);

        for (MaintenanceWindow window : windows) {
            System.out.println(ReportFormatter.formatWindow(window, berlin, Locale.forLanguageTag("ru-RU")));
        }
    }
}
