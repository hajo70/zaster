package de.spricom.zaster.dtos.settings;

import de.spricom.zaster.dtos.common.IdDto;
import de.spricom.zaster.enums.tracking.CurrencyType;
import dev.hilla.Nullable;

public record CurrencyDto(
        @Nullable
        IdDto id,
        String currencyCode,
        String currencyName,
        CurrencyType currencyType
) {
}
