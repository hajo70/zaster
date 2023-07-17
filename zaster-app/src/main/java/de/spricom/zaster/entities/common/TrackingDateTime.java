package de.spricom.zaster.entities.common;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@Embeddable
public class TrackingDateTime {

    /**
     * Date-Part without timezone for sorting and filtering.
     */
    protected LocalDate date;

    /**
     * Zoned date-time according to https://www.w3.org/TR/xmlschema-2/#dateTime.
     */
    @Column(length = 64)
    protected String zonedDateTime;

    public TrackingDateTime() {
    }

    protected TrackingDateTime(ZonedDateTime ts) {
        date = ts.toLocalDate();
        zonedDateTime = DateTimeFormatter.ISO_ZONED_DATE_TIME.format(ts);
    }

    public static TrackingDateTime now() {
        return new TrackingDateTime(ZonedDateTime.now());
    }

    public static TrackingDateTime of(LocalDate date) {
        return new TrackingDateTime(date.atStartOfDay(ZoneId.systemDefault()).plusHours(12));
    }

    public String toString() {
        return zonedDateTime;
    }
}
