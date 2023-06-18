package de.spricom.zaster.endpoints;

import javax.annotation.Nullable;
import java.util.List;


public record AccountGroup(
        String id,
        long version,
        String accountName,
        List<String> currencyCodes,
        @Nullable
        String parentId,
        @Nullable
        List<AccountGroup> children) {
}

