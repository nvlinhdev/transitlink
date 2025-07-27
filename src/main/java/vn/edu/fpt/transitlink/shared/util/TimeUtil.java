package vn.edu.fpt.transitlink.shared.util;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

/**
 * Utility class for handling timestamps consistently across the application.
 * All timestamps are generated in UTC format.
 */
public final class TimeUtil {

    private static final DateTimeFormatter UTC_FORMATTER = DateTimeFormatter
            .ofPattern("yyyy-MM-dd HH:mm:ss 'UTC'")
            .withZone(ZoneOffset.UTC);

    // Private constructor to prevent instantiation
    private TimeUtil() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    /**
     * Get current timestamp in UTC format as string
     * @return formatted timestamp string (e.g., "2024-07-27 07:30:15 UTC")
     */
    public static String now() {
        return Instant.now().atZone(ZoneOffset.UTC).format(UTC_FORMATTER);
    }

    /**
     * Format given instant to UTC string
     * @param instant the instant to format
     * @return formatted timestamp string
     */
    public static String format(Instant instant) {
        return instant.atZone(ZoneOffset.UTC).format(UTC_FORMATTER);
    }

    /**
     * Get the DateTimeFormatter used for UTC formatting
     * @return the UTC formatter
     */
    public static DateTimeFormatter getUtcFormatter() {
        return UTC_FORMATTER;
    }
}