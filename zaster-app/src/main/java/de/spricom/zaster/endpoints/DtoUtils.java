package de.spricom.zaster.endpoints;

import de.spricom.zaster.dtos.common.IdDto;
import de.spricom.zaster.entities.common.AbstractEntity;

public final class DtoUtils {

    private DtoUtils() {}

    public static IdDto id(AbstractEntity entity) {
        return new IdDto(entity.getId(), entity.getVersion());
    }

    public static void setId(AbstractEntity entity, IdDto id) {
        entity.setId(id.id());
        entity.setVersion(id.version());
    }
}
