package de.spricom.zaster.dtos.settings;

import java.util.Locale;

public record LocaleDto(
        Locale locale,
        String name
        ) {
}
