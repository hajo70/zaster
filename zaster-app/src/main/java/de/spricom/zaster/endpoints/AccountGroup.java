package de.spricom.zaster.endpoints;

import de.spricom.zaster.entities.tracking.AccountGroupEntity;

import javax.annotation.Nullable;
import java.util.List;


public record AccountGroup(
        AccountGroupEntity entity,
        @Nullable
        String parentId,
        List<AccountGroup> children) {
}

