package de.spricom.zaster.dtos.common;

import dev.hilla.Nonnull;

public record IdDto(
        @Nonnull
        String uuid,
        @Nonnull
        long version
) {
}
