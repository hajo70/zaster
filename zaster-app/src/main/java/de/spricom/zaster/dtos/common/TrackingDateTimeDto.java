package de.spricom.zaster.dtos.common;

import dev.hilla.Nonnull;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;

public record TrackingDateTimeDto(
        @Nonnull
        Instant ts,
        @Nonnull
        LocalDate date,
        LocalTime time,
        String offset,
        String zone
) {
}
