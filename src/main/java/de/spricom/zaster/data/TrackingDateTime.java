package de.spricom.zaster.data;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZonedDateTime;
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

    public TrackingDateTime(ZonedDateTime ts) {
        this(ts.toLocalDate(), ts.toLocalTime());
    }

    public static TrackingDateTime now() {
        return new TrackingDateTime(ZonedDateTime.now());
    }

    public static TrackingDateTime of(LocalDate date) {
        return new TrackingDateTime(date, null);
    }

    public LocalDate toLocalDate() {
        return date;
    }

    public LocalDateTime toLocalDateTime() {
        return date.atTime(time == null ? LocalTime.NOON : time);
    }

    public String toString() {
        if (date == null) {
            return "null";
        }
        if (time == null) {
            return DateTimeFormatter.ISO_TIME.format(date);
        }
        return DateTimeFormatter.ISO_DATE_TIME.format(toLocalDateTime());
    }
}
