package com.fisherman.companion.dto.utils;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class DateTimeUtil {
    public static String getUkrDateTimeMinusDays(final long days) {
        final LocalDateTime now = LocalDateTime.now();
        final LocalDateTime resultDateTime = now.minusDays(days);

        return formatLocalDateTimeInKyivZone(resultDateTime);
    }

    public static String getUkrDateTimePlusDays(final long days) {
        final LocalDateTime now = LocalDateTime.now();
        final LocalDateTime resultDateTime = now.plusDays(days);

        return formatLocalDateTimeInKyivZone(resultDateTime);
    }

    public static String getCurrentUkrDateTime() {
        final LocalDateTime now = LocalDateTime.now();

        return formatLocalDateTimeInKyivZone(now);
    }

    public static String formatLocalDateTimeInKyivZone(final LocalDateTime dateTime) {
        final ZoneId kyivTimeZone = ZoneId.of("Europe/Kiev");
        final LocalDateTime kyivDateTime = dateTime.atZone(kyivTimeZone).toLocalDateTime();

        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        return kyivDateTime.format(formatter);
    }

    public static String convertDateTimeToTimestampFormat(final String dateTime) {
        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
        final LocalDateTime localDateTime = LocalDateTime.parse(dateTime, formatter);

        final DateTimeFormatter toTimestampFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return localDateTime.format(toTimestampFormatter);
    }
}
