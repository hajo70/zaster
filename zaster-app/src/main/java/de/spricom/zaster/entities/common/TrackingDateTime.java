package de.spricom.zaster.entities.common;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.*;
import java.time.format.DateTimeFormatter;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TrackingDateTime {

    /**
     * Date-Part without timezone for sorting and filtering.
     */
    private LocalDate date;

    private LocalTime time;

    private ZoneOffset offset;

    private ZoneId zone;

    public TrackingDateTime(ZonedDateTime ts) {
        this(ts.toLocalDate(),
                ts.toLocalTime(),
                ts.getOffset(),
                ts.getZone());
    }

    public static TrackingDateTime now() {
        return new TrackingDateTime(ZonedDateTime.now());
    }

    public static TrackingDateTime of(LocalDate date) {
        return new TrackingDateTime(date, null, null, null);
    }

    public LocalDate toLocalDate() {
        return date;
    }

    public LocalDateTime toLocalDateTime() {
        return date.atTime(time == null ? LocalTime.NOON : time);
    }

    public OffsetDateTime toOffsetDateTime() {
        if (offset == null) {
            return toZonedDateTime().toOffsetDateTime();
        }
        return toLocalDateTime().atOffset(offset);
    }

    public ZonedDateTime toZonedDateTime() {
        if (zone == null && offset == null) {
            return toLocalDateTime().atZone(ZoneId.systemDefault());
        }
        if (zone == null) {
            return toOffsetDateTime().toZonedDateTime();
        }
        if (offset != null) {
            return  toOffsetDateTime().atZoneSameInstant(zone);
        }
        return toLocalDateTime().atZone(zone);
    }

    public String toString() {
        if (date == null) {
            return "null";
        }
        if (time == null) {
            return DateTimeFormatter.ISO_TIME.format(date);
        }
        if (offset == null && zone == null) {
            return DateTimeFormatter.ISO_DATE_TIME.format(toLocalDateTime());
        }
        if (zone != null) {
            return DateTimeFormatter.ISO_ZONED_DATE_TIME.format(toZonedDateTime());
        }
        return DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(toOffsetDateTime());
    }
}
