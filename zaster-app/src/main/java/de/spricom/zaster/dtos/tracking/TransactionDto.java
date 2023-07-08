package de.spricom.zaster.dtos.tracking;

import de.spricom.zaster.dtos.common.IdDto;

import java.time.LocalDate;
import java.util.List;

public record TransactionDto(
    IdDto id,
    LocalDate submittedAtDate,
    String description,
    List<BookingDto> bookings
) {
}
