package de.spricom.zaster.dtos.tracking;

import de.spricom.zaster.dtos.common.IdDto;

public record AccountCurrencyDto(
        IdDto id,
        String currencyId
) {
}
