package de.spricom.zaster.dtos.tracking;

import de.spricom.zaster.entities.settings.CurrencyEntity;

import java.util.List;

public record AccountingDataDto(
        List<CurrencyEntity> currencies,
        List<AccountDto> rootAccounts
) {
}
