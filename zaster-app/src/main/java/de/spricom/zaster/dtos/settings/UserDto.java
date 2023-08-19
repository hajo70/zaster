package de.spricom.zaster.dtos.settings;

import de.spricom.zaster.dtos.common.IdDto;
import de.spricom.zaster.enums.settings.UserRole;
import dev.hilla.Nullable;

import java.util.Collection;

public record UserDto(
        IdDto id,
        String username,
        String name,
        @Nullable
        String password,
        Collection<UserRole> roles
) {
}
