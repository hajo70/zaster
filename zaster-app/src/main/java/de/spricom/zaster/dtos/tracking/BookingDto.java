package de.spricom.zaster.dtos.tracking;

import de.spricom.zaster.dtos.common.IdDto;

import java.time.LocalDate;
import java.util.List;

public record BookingDto(
    IdDto id,
    LocalDate bookedAtDate,
    String description,
    List<TransferDto> transfers
) {
}
