package de.spricom.zaster.dtos.tracking;

import de.spricom.zaster.dtos.common.IdDto;
import de.spricom.zaster.dtos.common.TrackingDateTimeDto;

import java.util.List;

public record BookingDto(
    IdDto id,
    TrackingDateTimeDto bookedAt,
    String description,
    List<TransferDto> transfers
) {
}
