package de.spricom.zaster.repository.tracking;

import de.spricom.zaster.entities.tracking.SnapshotEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface SnapshotRepository
        extends JpaRepository<SnapshotEntity, String>, JpaSpecificationExecutor<SnapshotEntity> {
}
