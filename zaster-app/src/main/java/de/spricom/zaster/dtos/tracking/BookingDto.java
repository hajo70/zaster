package de.spricom.zaster.dtos.tracking;

import de.spricom.zaster.dtos.common.IdDto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record BookingDto(
        IdDto id,
        String accountId,
        LocalDate bookedAtDate,
        BigDecimal amount,
        String accountGroupId,
        String accountGroupName,
        String currencyId
) {
    }
