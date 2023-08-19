package de.spricom.zaster.dtos.settings;

import de.spricom.zaster.dtos.common.IdDto;

import java.time.ZoneId;
import java.util.Locale;

public record TenantDto(
        IdDto id,
        String name,
        Locale locale,
        ZoneId timeZone
) {
}
