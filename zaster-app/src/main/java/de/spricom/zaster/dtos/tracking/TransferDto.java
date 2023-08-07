package de.spricom.zaster.dtos.tracking;

import de.spricom.zaster.dtos.common.IdDto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record TransferDto(
        IdDto id,
        String accountCurrencyId,
        LocalDate transferredAtDate,
        BigDecimal amount,
        String accountId,
        String accountName,
        String currencyId
) {
    }
