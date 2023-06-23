package de.spricom.zaster.dtos.tracking;

import de.spricom.zaster.dtos.common.IdDto;
import dev.hilla.Nullable;

import java.util.List;


public record AccountGroupDto(
        IdDto id,
        String accountName,
        @Nullable
        List<AccountDto> accounts,
        @Nullable
        String parentId,
        @Nullable
        List<AccountGroupDto> children
) {
}

