package de.spricom.zaster.dtos.common;

import dev.hilla.Nonnull;

import java.time.LocalDate;
import java.time.LocalTime;

public record TrackingDateTimeDto(
        @Nonnull
        LocalDate date,
        LocalTime time,
        String offset,
        String zone
) {
}
