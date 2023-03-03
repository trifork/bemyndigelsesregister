package dk.bemyndigelsesregister.util;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import static java.time.temporal.ChronoUnit.*;

public class DateUtils {
    private static final Map<String, DateTimeFormatter> dateTimeFormatters = new HashMap<>();

    public static Instant plusMinutes(Instant instant, int minutes) {
        return instant.plus(minutes, MINUTES);
    }

    public static Instant plusDays(Instant instant, int days) {
        return instant.plus(days, DAYS);
    }

    public static Instant plysYears(Instant instant, int years) {
        return ZonedDateTime.ofInstant(instant, ZoneId.systemDefault()).plus(years, YEARS).toInstant(); // zone is necessary to add years
    }

    public static int daysBetween(Instant from, Instant to) {
        return (int) DAYS.between(from, to);
    }

    public static Instant toInstant(int year, int month, int day) {
        return Instant.now().
                atZone(ZoneOffset.UTC)
                .withYear(year)
                .withMonth(month)
                .withDayOfMonth(day)
                .withHour(0)
                .withMinute(0)
                .withSecond(0)
                .withNano(0).toInstant();
    }

    public static String format(Instant instant, String format) {
        DateTimeFormatter formatter = dateTimeFormatters.get(format);
        if (formatter == null) {
            formatter = DateTimeFormatter.ofPattern(format).withZone(ZoneId.systemDefault());
            dateTimeFormatters.put(format, formatter);
        }
        return formatter.format(instant);
    }
}
