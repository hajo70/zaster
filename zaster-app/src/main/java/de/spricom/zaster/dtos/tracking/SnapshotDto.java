package de.spricom.zaster.dtos.tracking;

import de.spricom.zaster.dtos.common.IdDto;
import de.spricom.zaster.dtos.common.TrackingDateTimeDto;

import java.math.BigDecimal;

public record SnapshotDto(
        IdDto id,
        String accountCurrencyId,
        BigDecimal amount,
        TrackingDateTimeDto takenAt
) {
}
