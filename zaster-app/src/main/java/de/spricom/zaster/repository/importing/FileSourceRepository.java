package de.spricom.zaster.repository.importing;

import de.spricom.zaster.entities.tracking.FileSourceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface FileSourceRepository extends JpaRepository<FileSourceEntity, String>, JpaSpecificationExecutor<FileSourceEntity> {
}
