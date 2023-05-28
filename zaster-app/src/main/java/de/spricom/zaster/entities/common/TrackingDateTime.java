package de.spricom.zaster.entities.common;

import java.time.LocalDate;

public class TrackingDateTime {

    /**
     * Date-Part without timezone for sorting and filtering.
     */
    private LocalDate date;

    /**
     * Zoned date-time according to https://www.w3.org/TR/xmlschema-2/#dateTime.
     */
    private String zonedDateTime;
}
