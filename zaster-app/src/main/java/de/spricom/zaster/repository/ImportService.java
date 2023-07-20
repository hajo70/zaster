package de.spricom.zaster.repository;

import de.spricom.zaster.entities.managment.TenantEntity;
import de.spricom.zaster.entities.tracking.FileSourceEntity;

import java.util.Optional;

public interface ImportService {
    Optional<FileSourceEntity> findByMd5(TenantEntity tenant, String md5);

    FileSourceEntity create(FileSourceEntity fileSource);
}
