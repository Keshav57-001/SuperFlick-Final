package com.superflick.shared.util;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

/**
 * Stateless utility methods for date/time formatting used across the platform.
 * All methods are static — no instantiation needed.
 */
public final class DateUtils {

    private DateUtils() {}

    private static final DateTimeFormatter DISPLAY_FORMATTER =
            DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a");
    private static final DateTimeFormatter DATE_ONLY_FORMATTER =
            DateTimeFormatter.ofPattern("dd MMM yyyy");
    private static final ZoneId IST = ZoneId.of("Asia/Kolkata");

    /**
     * Formats a LocalDateTime to a human-readable display string.
     * Example: "14 Mar 2026, 04:30 PM"
     */
    public static String format(LocalDateTime dateTime) {
        if (dateTime == null) return null;
        return dateTime.format(DISPLAY_FORMATTER);
    }

    /**
     * Formats only the date portion.
     * Example: "14 Mar 2026"
     */
    public static String formatDate(LocalDateTime dateTime) {
        if (dateTime == null) return null;
        return dateTime.format(DATE_ONLY_FORMATTER);
    }

    /**
     * Returns a relative time string like "2 hours ago", "3 days ago".
     * Used in notification and chat timestamps.
     */
    public static String timeAgo(LocalDateTime dateTime) {
        if (dateTime == null) return "";
        LocalDateTime now = LocalDateTime.now();
        long minutes = ChronoUnit.MINUTES.between(dateTime, now);
        if (minutes < 1)  return "Just now";
        if (minutes < 60) return minutes + " min ago";
        long hours = ChronoUnit.HOURS.between(dateTime, now);
        if (hours < 24)   return hours + " hour" + (hours > 1 ? "s" : "") + " ago";
        long days = ChronoUnit.DAYS.between(dateTime, now);
        if (days < 7)     return days + " day" + (days > 1 ? "s" : "") + " ago";
        long weeks = days / 7;
        if (weeks < 5)    return weeks + " week" + (weeks > 1 ? "s" : "") + " ago";
        long months = ChronoUnit.MONTHS.between(dateTime, now);
        if (months < 12)  return months + " month" + (months > 1 ? "s" : "") + " ago";
        long years = ChronoUnit.YEARS.between(dateTime, now);
        return years + " year" + (years > 1 ? "s" : "") + " ago";
    }

    /**
     * Returns the number of days remaining until the given expiry datetime.
     * Returns 0 if the datetime is in the past.
     */
    public static long daysUntil(LocalDateTime expiry) {
        if (expiry == null) return 0L;
        long days = ChronoUnit.DAYS.between(LocalDateTime.now(), expiry);
        return Math.max(0L, days);
    }

    /**
     * Checks whether a given datetime is in the future (i.e. not expired).
     */
    public static boolean isActive(LocalDateTime expiry) {
        return expiry != null && expiry.isAfter(LocalDateTime.now());
    }

    /**
     * Returns a LocalDateTime representing exactly N days from now.
     * Used to compute subscription expiry dates.
     */
    public static LocalDateTime daysFromNow(int days) {
        return LocalDateTime.now(IST).plusDays(days);
    }
}