package de.spricom.zaster.dtos.tracking;

import de.spricom.zaster.dtos.common.IdDto;

import java.math.BigDecimal;
import java.time.Instant;

public record BookingDto(
        IdDto id,
        String accountId,
        Instant bookedAt,
        BigDecimal amount
) {
    }
