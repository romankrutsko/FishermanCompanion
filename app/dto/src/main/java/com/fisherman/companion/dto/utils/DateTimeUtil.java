package com.fisherman.companion.dto.utils;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class DateTimeUtil {
    public static String getUkrDateTimeMinusDays(final long days) {
        final ZonedDateTime now = ZonedDateTime.now(ZoneId.of("Europe/Kiev"));
        final ZonedDateTime resultDateTime = now.minusDays(days);

        return formatZonedDateTimeInKyivZone(resultDateTime);
    }

    public static String getUkrDateTimePlusDays(final long days) {
        final ZonedDateTime now = ZonedDateTime.now(ZoneId.of("Europe/Kiev"));
        final ZonedDateTime resultDateTime = now.plusDays(days);

        return formatZonedDateTimeInKyivZone(resultDateTime);
    }

    public static String getCurrentUkrDateTime() {
        final ZonedDateTime now = ZonedDateTime.now(ZoneId.of("Europe/Kiev"));
        return formatZonedDateTimeInKyivZone(now);
    }

    public static String formatZonedDateTimeInKyivZone(final ZonedDateTime dateTime) {
        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return dateTime.format(formatter);
    }

    public static String convertDateTimeToTimestampFormat(final String dateTime) {
        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
        final LocalDateTime localDateTime = LocalDateTime.parse(dateTime, formatter);

        final DateTimeFormatter toTimestampFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return localDateTime.format(toTimestampFormatter);
    }

    public static String trimSeconds(final String dateTime) {
        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        final LocalDateTime startDate = LocalDateTime.parse(dateTime, formatter);

        final LocalDateTime trimmedStartDate = startDate.withSecond(0);

        final DateTimeFormatter formatterWithoutSeconds = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        return trimmedStartDate.format(formatterWithoutSeconds);
    }
}
