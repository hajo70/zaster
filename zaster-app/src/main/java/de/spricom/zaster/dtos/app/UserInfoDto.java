package de.spricom.zaster.dtos.app;

import de.spricom.zaster.dtos.settings.*;
import de.spricom.zaster.dtos.tracking.AccountDto;

import java.util.List;

/**
 * Container for all data after login
 */
public record UserInfoDto(
        UserDto user,
        TenantDto tenant,
        List<CurrencyDto> currencies,
        List<AccountDto> rootAccounts,
        List<LocaleDto> availableLocales,
        List<TimezoneDto> availableZoneIds,
        List<CurrencyDto> availableCurrencies
) {
}
