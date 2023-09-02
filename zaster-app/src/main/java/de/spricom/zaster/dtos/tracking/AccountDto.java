package de.spricom.zaster.dtos.tracking;

import de.spricom.zaster.dtos.common.IdDto;
import dev.hilla.Nullable;

import java.util.List;


public record AccountDto(
        IdDto id,
        @Nullable
        String accountCode,
        String accountName,
        @Nullable
        List<AccountCurrencyDto> currencies,
        @Nullable
        String parentId,
        @Nullable
        List<AccountDto> children
) {
}

