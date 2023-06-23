package de.spricom.zaster.dtos.tracking;

import de.spricom.zaster.dtos.common.IdDto;

import java.time.Instant;
import java.util.List;

public record TransactionDto(
    IdDto id,
    Instant submittedAt,
    String description,
    List<BookingDto> bookings
) {
}
